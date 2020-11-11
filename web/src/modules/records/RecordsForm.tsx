import React, { useState, useEffect, useContext } from "react";
import { FormikProps, Field, FieldProps } from "formik";
import Button from "@material-ui/core/Button";
import { Tooltip } from "@material-ui/core";
import InfoIcon from "@material-ui/icons/Info";
import classNames from "classnames";
import { compact, get, find, filter, forEach } from "lodash";
import { withRouter, RouteComponentProps } from "react-router-dom";

import { notEmpty } from "../../utils/form/validate";
import { GlobalContext, StateProps } from "../../context/Context";
import { Formik } from "../../components/form/Formik";
import { Loader } from "../../components/loader/Loader";
import { InputText } from "../../components/form/InputText";
import { AsyncSelect } from "../../components/form/AsyncSelect";
import { Editor } from "../../components/form/Editor";
import { Divider } from "../../components/divider/Divider";
import { MessageSnackbar } from "../../components/messages/MessageSnackbar";
import { useStyles } from "./_recordsStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";

import {
  RecordProps,
  MarcEntity,
  DataFieldsEntity,
  SubfieldsEntity,
  FormDataField,
} from "../../types/record";
import { onSubmitRecord, contentNotEmpty } from "./_utils";
import { recordGetMarc } from "../../context/actions/record";
import { CreatorField } from "./CreatorField";
import {
  isCreator,
  getCreatorLabel,
  isCorporate,
  clearCreatorValue,
  createMarcLabel,
} from "../recordsTemplates/_utils";
import { getFiles } from "../attachments/_utils";
import { FileProps } from "../../types/file";
import { getCards } from "../cards/_utils";
import { LinkedCardProps } from "../../types/card";
import { Select } from "../../components/select";
import { DefaultCreators, RecordTypes } from "./_enums";
import { parseRecord, createCreatorRegex } from "./_utils";

export interface RecordRequest {
  created?: string;
  dataFields?: DataFieldsEntity[] | null;
  content?: string;
  deleted?: string;
  id: string;
  name: string;
  documents?: FileProps[];
  updated?: string;
  linkedCards?: LinkedCardProps[];
}

export interface Creator {
  value: string;
  data?: string;
}

export interface RecordFormValues {
  created?: string;
  dataFields?: FormDataField[] | null;
  content?: string;
  creators: Creator[];
  deleted?: string;
  id: string;
  name: string;
  updated?: string;
  linkedCards?: LinkedCardProps[];
}

interface RecordsFormProps {
  setShowModal: Function;
  item?: RecordProps;
  afterEdit?: () => void;
  redirect?: boolean;
  onSubmitCallback?: Function;
}

const RecordsFormView: React.FC<RecordsFormProps & RouteComponentProps> = ({
  setShowModal,
  item,
  afterEdit,
  history,
  redirect = true,
  onSubmitCallback,
}) => {
  const classes = useStyles();
  const classesSpacing = useSpacingStyles();
  const classesLayout = useLayoutStyles();
  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;
  const state: StateProps = context.state;
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<boolean | string>(false);
  const [recordType, setRecordType] = useState<any | null>(null);
  const [content, setContent] = useState<any>();
  const [contentKey, setContentKey] = useState<boolean>(false);

  const refreshContent = () => setContentKey(!contentKey);

  const [initValues, setInitValues] = useState<RecordFormValues>({
    dataFields: [],
    creators: [],
    id: "",
    name: "",
  });

  const marc: MarcEntity[] = state.record.marc;
  useEffect(() => {
    if (marc) {
      const creatorDataFields = item
        ? filter(item.dataFields, ({ tag }) => isCreator(tag))
        : [];
      const creators: Creator[] = [];
      creatorDataFields.forEach(({ tag, subfields }) => {
        (subfields || []).forEach(({ code, data }) =>
          creators.push({
            value: `${tag === "110" || tag === "710" ? "_" : ""}${code}`,
            data,
          })
        );
      });
      const content = item ? item.content : undefined;
      setInitValues({
        ...{
          ...((item as any) || {}),
          dataFields: marc.map((m) => {
            if (isCreator(m.tag)) {
              return undefined;
            }
            const field = find(get(item, "dataFields"), (d) => d.tag === m.tag);

            return {
              tag: m.tag,
              ...(m.indicator1 ? { indicator1: m.indicator1 } : {}),
              ...(m.indicator2 ? { indicator2: m.indicator2 } : {}),
              code: m.code,
              ...(get(field, "subfields")
                ? {
                    data: get(
                      find(
                        get(field, "subfields"),
                        (subfield) => subfield.code === m.code
                      ),
                      "data"
                    ),
                  }
                : {}),
            };
          }),
          content,
        },
        creators: creators.length ? creators : DefaultCreators,
      });
      setContent(content);
    }
  }, [marc, item]);
  useEffect(() => {
    recordGetMarc(dispatch);
  }, [dispatch]);

  return (
    <>
      <Loader loading={loading} />
      {error && <MessageSnackbar setVisible={setError} message={error} />}
      <Formik
        validateOnChange
        initialValues={initValues}
        enableReinitialize
        onSubmit={() => {}}
        render={(formikBag: FormikProps<RecordFormValues>) => {
          const { values } = formikBag;

          let briefContent: string | undefined;

          if (values.content) {
            try {
              briefContent = get(JSON.parse(values.content), "blocks[0].text");
            } catch {
              briefContent = undefined;
            }
          }

          const handleParse = () => {
            if (briefContent) {
              const result = parseRecord(
                briefContent,
                recordType,
                formikBag.values.dataFields
              );

              if (result) {
                formikBag.setFieldValue("dataFields", result.dataFields);
                formikBag.setFieldValue("creators", result.creators);
                setRecordType(null);
              } else {
                setError(
                  "Chyba: Nepodařilo se převést citaci do strukturované podoby."
                );
              }
            }
          };

          return (
            <form
              className={classes.recordForm}
              autoComplete="off"
              onReset={formikBag.handleReset}
              onSubmit={(e: any) => {
                e.preventDefault();
                e.stopPropagation();

                if (loading) return;
                setLoading(true);

                const { creators, ...values } = formikBag.values;

                const creatorDataFields: DataFieldsEntity[] = [];
                const creatorDataSubfields: SubfieldsEntity[] = [];
                const creatorCorporateDataSubfields: SubfieldsEntity[] = [];
                const filtered = filter(
                  creators,
                  ({ value, data }) => value && data
                ) as {
                  value: string;
                  data: string;
                }[];
                const wrongCreator = find(
                  filtered,
                  ({ value, data }) =>
                    !isCorporate(value) &&
                    createCreatorRegex({
                      prefix: "^\\s*",
                      suffix: "\\S+$",
                    }).test(data)
                );

                if (wrongCreator) {
                  setError(
                    `Chyba: V poli ${getCreatorLabel(
                      false,
                      clearCreatorValue(wrongCreator.value)
                    )} je Příjmení povinné.`
                  );
                  return;
                }
                filtered.map(({ value, data }, index) =>
                  index
                    ? (isCorporate(value)
                        ? creatorCorporateDataSubfields
                        : creatorDataSubfields
                      ).push({
                        code: clearCreatorValue(value),
                        data,
                      })
                    : creatorDataFields.push({
                        tag: isCorporate(value) ? "110" : "100",
                        subfields: [{ code: clearCreatorValue(value), data }],
                      })
                );

                creatorDataSubfields.map((s) =>
                  creatorDataFields.push({
                    tag: "700",
                    subfields: [s],
                  })
                );

                creatorCorporateDataSubfields.map((s) =>
                  creatorDataFields.push({
                    tag: "710",
                    subfields: [s],
                  })
                );

                const filteredValues = {
                  ...values,
                  dataFields: [
                    ...(() => {
                      const result: DataFieldsEntity[] = [];

                      forEach(
                        compact([
                          ...(values.dataFields || []).map((dataField) => {
                            if (dataField && dataField.data) {
                              const { code, data, ...rest } = dataField;
                              return { ...rest, subfields: [{ code, data }] };
                            }

                            return undefined;
                          }),
                        ]),
                        (dataField) => {
                          const found = find(
                            result,
                            ({ tag }) => tag === dataField.tag
                          );
                          if (found) {
                            found.subfields = [
                              ...(found.subfields || []),
                              ...(dataField.subfields || []),
                            ];
                          } else {
                            result.push(dataField);
                          }
                        }
                      );

                      return result;
                    })(),
                    ...creatorDataFields,
                  ],
                };

                onSubmitRecord(
                  filteredValues,
                  setShowModal,
                  setError,
                  setLoading,
                  dispatch,
                  history,
                  item,
                  afterEdit,
                  redirect,
                  onSubmitCallback
                );
              }}
            >
              <div className={classNames(classes.mainPanel)}>
                <div
                  className={classNames(
                    classesSpacing.pt2,
                    classesSpacing.pb2,
                    classesSpacing.pr2
                  )}
                >
                  <Field
                    name="name"
                    validate={notEmpty}
                    render={({ field, form }: FieldProps<RecordFormValues>) => (
                      <>
                        <InputText
                          label="Název"
                          type="text"
                          field={field}
                          form={form}
                          autoFocus={false}
                          oneLine={true}
                        />
                      </>
                    )}
                  />
                  <Field
                    name="documents"
                    render={({ field, form }: FieldProps<RecordFormValues>) => (
                      <>
                        <AsyncSelect
                          label="Dokumenty"
                          field={field}
                          form={form}
                          oneLine={true}
                          isMulti={true}
                          loadOptions={async (text?: string) =>
                            get(await getFiles(text), "items", [])
                          }
                        />
                      </>
                    )}
                  />
                  <Field
                    name="linkedCards"
                    render={({ field, form }: FieldProps<RecordFormValues>) => (
                      <>
                        <AsyncSelect
                          label="Karty"
                          field={field}
                          form={form}
                          oneLine={true}
                          isMulti={true}
                          loadOptions={async (text?: string) =>
                            get(await getCards(text), "items", [])
                          }
                        />
                      </>
                    )}
                  />
                </div>
                <Divider />
                <div
                  className={
                    contentNotEmpty(formikBag.values.content)
                      ? classes.contentWrapperSticky
                      : ""
                  }
                >
                  <div
                    className={classNames(
                      classesSpacing.p2,
                      classesSpacing.pb1
                    )}
                  >
                    <Field
                      key={contentKey}
                      name="content"
                      briefOptions={true}
                      value={content}
                      component={Editor}
                    />
                  </div>
                </div>
                <div
                  className={classNames(
                    classesSpacing.pl2,
                    classesSpacing.pr2,
                    classesSpacing.pb1
                  )}
                >
                  <div
                    className={classNames(
                      classesLayout.flex,
                      classesLayout.alignCenter,
                      classesLayout.spaceBetween
                    )}
                  >
                    <Button
                      variant="outlined"
                      className={classesSpacing.mr1}
                      onClick={() => {
                        formikBag.setFieldValue("content", undefined);
                        setContent(undefined);
                        setTimeout(refreshContent);
                      }}
                      disabled={!briefContent}
                    >
                      Vymazat text
                    </Button>
                    <div
                      className={classNames(
                        classesLayout.flex,
                        classesLayout.alignCenter,
                        classesLayout.justifyEnd,
                        classesLayout.flexWrap
                      )}
                    >
                      <div>Převést do strukturované podoby:</div>
                      <div
                        className={classesSpacing.ml1}
                        style={{ minWidth: 300 }}
                      >
                        <Select
                          placeholder="Vyberte typ citace"
                          options={RecordTypes}
                          onChange={(value) => setRecordType(value)}
                          isClearable={false}
                        />
                      </div>
                      <Button
                        variant="outlined"
                        color="primary"
                        className={classesSpacing.ml1}
                        onClick={handleParse}
                        disabled={!briefContent || !recordType}
                      >
                        Převést
                      </Button>
                    </div>
                  </div>
                </div>
                <div
                  className={classNames(
                    classesLayout.flex,
                    classesLayout.flexWrap,
                    classesSpacing.pr2
                  )}
                >
                  {(() => {
                    let cretatorRendered = false;

                    return (marc || []).map((m, index) => {
                      const key = `${m.tag}-${index}`;

                      if (isCreator(m.tag)) {
                        if (cretatorRendered) {
                          return <div key={key} />;
                        }

                        cretatorRendered = true;

                        return (
                          <CreatorField key={key} items={values.creators} />
                        );
                      }

                      return (
                        <Field
                          key={key}
                          name={`dataFields[${index}].data`}
                          render={({
                            field,
                            form,
                          }: FieldProps<RecordFormValues>) => (
                            <InputText
                              type="text"
                              label={
                                <div
                                  className={classNames(
                                    classesLayout.flex,
                                    classesLayout.alignCenter,
                                    classesLayout.justifyEnd
                                  )}
                                >
                                  <div className={classesSpacing.mr1}>
                                    {m.czech}
                                  </div>
                                  <Tooltip title={createMarcLabel(m)}>
                                    <InfoIcon />
                                  </Tooltip>
                                </div>
                              }
                              field={field}
                              form={form}
                              oneLine={true}
                            />
                          )}
                        />
                      );
                    });
                  })()}
                </div>
              </div>
              <Divider />
              <div
                className={classNames(
                  classes.buttonsPanel,
                  classesLayout.flex,
                  classesLayout.justifyCenter,
                  classesLayout.alignCenter
                )}
              >
                <Button variant="contained" color="primary" type="submit">
                  {item ? "Změnit citaci" : "Vytvořit citaci"}
                </Button>
              </div>
            </form>
          );
        }}
      />
    </>
  );
};

export const RecordsForm = withRouter(RecordsFormView);
