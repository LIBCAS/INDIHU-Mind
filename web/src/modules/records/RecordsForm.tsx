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
import { Switch } from "../../components/form/Switch";
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
  FormDataField
} from "../../types/record";
import { onSubmitRecord } from "./_utils";
import { recordGetMarc } from "../../context/actions/record";
import { CreatorField } from "./CreatorField";
import {
  isCreator,
  getCreatorLabel,
  isCorporate,
  clearCreatorValue,
  createMarcLabel
} from "../recordsTemplates/_utils";
import { getFiles } from "../attachments/_utils";
import { FileProps } from "../../types/file";
import { RecordType } from "../../enums";
import { getCards } from "../cards/_utils";
import { LinkedCardProps } from "../../types/card";

export interface RecordRequest {
  created: string;
  dataFields?: DataFieldsEntity[] | null;
  content?: string;
  deleted: string;
  id: string;
  name: string;
  document?: FileProps;
  updated: string;
  isBrief?: boolean;
  type?: RecordType;
  linkedCards?: LinkedCardProps[];
}

export interface Creator {
  value: string;
  data?: string;
}

export interface RecordFormValues {
  created: string;
  dataFields?: FormDataField[] | null;
  content?: string;
  creators: Creator[];
  deleted: string;
  id: string;
  name: string;
  updated: string;
  type?: RecordType;
  isBrief?: boolean;
  linkedCards?: LinkedCardProps[];
}

interface RecordsFormProps {
  setShowModal: Function;
  record?: RecordProps;
  afterEdit?: () => void;
  redirect?: boolean;
  onSubmitCallback?: Function;
}

const RecordsFormView: React.FC<RecordsFormProps & RouteComponentProps> = ({
  setShowModal,
  record,
  afterEdit,
  history,
  redirect = true,
  onSubmitCallback
}) => {
  const classes = useStyles();
  const classesSpacing = useSpacingStyles();
  const classesLayout = useLayoutStyles();
  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;
  const state: StateProps = context.state;
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<boolean | string>(false);
  const [initValues, setInitValues] = useState<RecordFormValues | null>(null);
  const marc: MarcEntity[] = state.record.marc;
  useEffect(() => {
    if (marc) {
      const creatorDataFields = record
        ? filter(record.dataFields, ({ tag }) => isCreator(tag))
        : [];
      const creators: Creator[] = [];
      creatorDataFields.map(({ tag, subfields }) => {
        (subfields || []).map(({ code, data }) =>
          creators.push({
            value: `${tag === "110" || tag === "710" ? "_" : ""}${code}`,
            data
          })
        );
      });
      setInitValues({
        ...{
          ...((record as any) || {}),
          dataFields: marc.map(m => {
            if (isCreator(m.tag)) {
              return undefined;
            }
            const field = find(get(record, "dataFields"), d => d.tag === m.tag);

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
                        subfield => subfield.code === m.code
                      ),
                      "data"
                    )
                  }
                : {})
            };
          })
        },
        creators: creators.length ? creators : [{ value: "a" }]
      });
    }
  }, [marc, record]);
  useEffect(() => {
    recordGetMarc(dispatch);
  }, []);
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
                    !isCorporate(value) && /^\s*#&&#\S+$/.test(data)
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
                        data
                      })
                    : creatorDataFields.push({
                        tag: isCorporate(value) ? "110" : "100",
                        subfields: [{ code: clearCreatorValue(value), data }]
                      })
                );

                creatorDataSubfields.map(s =>
                  creatorDataFields.push({
                    tag: "700",
                    subfields: [s]
                  })
                );

                creatorCorporateDataSubfields.map(s =>
                  creatorDataFields.push({
                    tag: "710",
                    subfields: [s]
                  })
                );

                const filteredValues = {
                  ...values,
                  dataFields: [
                    ...(() => {
                      const result: DataFieldsEntity[] = [];

                      forEach(
                        compact([
                          ...(values.dataFields || []).map(dataField => {
                            if (dataField && dataField.data) {
                              const { code, data, ...rest } = dataField;
                              return { ...rest, subfields: [{ code, data }] };
                            }

                            return undefined;
                          })
                        ]),
                        dataField => {
                          const found = find(
                            result,
                            ({ tag }) => tag === dataField.tag
                          );
                          if (found) {
                            found.subfields = [
                              ...(found.subfields || []),
                              ...(dataField.subfields || [])
                            ];
                          } else {
                            result.push(dataField);
                          }
                        }
                      );

                      return result;
                    })(),
                    ...creatorDataFields
                  ]
                };

                onSubmitRecord(
                  filteredValues,
                  setShowModal,
                  setError,
                  setLoading,
                  dispatch,
                  history,
                  record,
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
                    name="document"
                    render={({ field, form }: FieldProps<RecordFormValues>) => (
                      <>
                        <AsyncSelect
                          label="Dokument"
                          field={field}
                          form={form}
                          oneLine={true}
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
                {!values.type && (
                  <div
                    className={classNames(
                      classesSpacing.pt3,
                      classesSpacing.pb3,
                      classesSpacing.pr2
                    )}
                  >
                    <Field
                      name="isBrief"
                      title="Pouze text citace"
                      oneLine={true}
                      component={Switch}
                    />
                  </div>
                )}
                {values.isBrief || values.type === RecordType.BRIEF ? (
                  <div className={classesSpacing.p2}>
                    <Field name="content" component={Editor} />
                  </div>
                ) : (
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
                        if (isCreator(m.tag)) {
                          if (cretatorRendered) {
                            return <></>;
                          }

                          cretatorRendered = true;

                          return <CreatorField items={values.creators} />;
                        }

                        return (
                          <Field
                            key={m.tag}
                            name={`dataFields[${index}].data`}
                            render={({
                              field,
                              form
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
                )}
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
                  {record ? "Změnit citaci" : "Vytvořit citaci"}
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
