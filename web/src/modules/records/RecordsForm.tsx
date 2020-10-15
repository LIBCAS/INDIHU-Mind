import React, { useState, useEffect, useContext } from "react";
import { FormikProps, Form, Field, FieldProps, FieldArray } from "formik";
import Typography from "@material-ui/core/Typography";
import Button from "@material-ui/core/Button";
import classNames from "classnames";
import Add from "@material-ui/icons/Add";
import Remove from "@material-ui/icons/Remove";
import { get } from "lodash";
import { withRouter, RouteComponentProps } from "react-router-dom";

import { notEmpty } from "../../utils/form/validate";
import { templateGet } from "../../context/actions/template";
import { GlobalContext, StateProps } from "../../context/Context";
import { Formik } from "../../components/form/Formik";
import { Loader } from "../../components/loader/Loader";
import { InputText } from "../../components/form/InputText";
import { Divider } from "../../components/divider/Divider";
import { MessageSnackbar } from "../../components/messages/MessageSnackbar";

import { useStyles } from "./_recordsStyles";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";

import { RecordProps } from "../../types/record";
import { onSubmitRecord } from "./_utils";
import { IconButton } from "@material-ui/core";
import { recordGet } from "../../context/actions/record";

const leaderValidate = (value: string) => {
  let error;
  if (value && value !== "" && value.length !== 24) {
    error = "Leader musí mít 24 znaků";
  }
  return error;
};

export interface RecordRequest {
  created: string;
  dataFields?: (DataFieldsEntity)[] | null;
  deleted: string;
  id: string;
  leader: string;
  name: string;
  updated: string;
}
export interface DataFieldsEntity {
  id: string;
  indicator1: string;
  indicator2: string;
  subfields?: (SubfieldsEntity)[] | null;
  tag: string;
}
export interface SubfieldsEntity {
  code: string;
  data: string;
  id: string;
}

interface RecordsFormProps {
  setShowModal: Function;
  record?: RecordProps;
  afterEdit?: () => void;
}

const RecordsFormView: React.FC<RecordsFormProps & RouteComponentProps> = ({
  setShowModal,
  record,
  afterEdit,
  history
}) => {
  const classes = useStyles();
  const classesText = useTextStyles();
  const classesSpacing = useSpacingStyles();
  const classesLayout = useLayoutStyles();
  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;
  const state: StateProps = context.state;
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<boolean>(false);
  const [initValues, setInitValues] = useState<RecordRequest | null>(null);
  const marc = state.record.marc;
  let fields: any;
  if (marc) {
    fields = state.record.marc.fields;
  }
  useEffect(() => {
    if (record) {
      setInitValues(record as any);
    }
  }, [record]);
  return (
    <>
      <Loader loading={loading} />
      {error && <MessageSnackbar setVisible={setError} />}
      <Formik
        validateOnChange
        initialValues={initValues}
        enableReinitialize
        onSubmit={(values: RecordRequest) => {
          if (loading) return;
          setLoading(true);
          onSubmitRecord(
            values,
            setShowModal,
            setError,
            setLoading,
            dispatch,
            history,
            record,
            afterEdit
          );
        }}
        render={(formikBag: FormikProps<RecordRequest>) => {
          const { values } = formikBag;
          return (
            <Form>
              <div
                className={classNames(
                  classesLayout.flex,
                  classesLayout.flexWrap,
                  classesLayout.justifyCenter,
                  classesLayout.directionColumn,
                  classesSpacing.ml2,
                  classesSpacing.mr2
                )}
              >
                <Field
                  name="name"
                  validate={notEmpty}
                  render={({ field, form }: FieldProps<RecordRequest>) => (
                    <>
                      <InputText
                        label="Název"
                        type="text"
                        field={field}
                        form={form}
                        autoFocus={record ? false : true}
                      />
                    </>
                  )}
                />
                <Field
                  name="leader"
                  validate={leaderValidate}
                  render={({ field, form }: FieldProps<RecordRequest>) => (
                    <InputText
                      label="Leader"
                      type="text"
                      field={field}
                      form={form}
                    />
                  )}
                />
                <div
                  className={classNames(
                    classesLayout.flex,
                    classesLayout.flexWrap,
                    classesLayout.spaceBetween,
                    classesSpacing.mt2,
                    classesLayout.halfItems,
                    classesLayout.fullItemsMobile
                  )}
                >
                  <FieldArray
                    name="dataFields"
                    render={dataFields => (
                      <>
                        <div>
                          <Typography
                            className={classNames(
                              classesText.subtitle,
                              classesSpacing.mb2
                            )}
                          >
                            TAGY
                          </Typography>
                          {Object.keys(fields).map(key => {
                            if (key === "LDR") return;
                            return (
                              <div
                                key={key}
                                className={classNames(
                                  classesLayout.flex,
                                  classesLayout.alignCenter
                                )}
                              >
                                <IconButton
                                  disabled={Boolean(
                                    !get(fields[key], `repeatable`, false) &&
                                      values.dataFields &&
                                      values.dataFields.some(d => d.tag === key)
                                  )}
                                  color="inherit"
                                  onClick={() => dataFields.push({ tag: key })}
                                >
                                  <Add color="inherit" />
                                </IconButton>
                                <Typography key={key} display="block">
                                  {`${key} ${get(fields[key], "label", "N/A")}`}
                                </Typography>
                              </div>
                            );
                          })}
                        </div>
                        <div>
                          <Typography
                            className={classNames(
                              classesText.subtitle,
                              classesSpacing.mb2
                            )}
                          >
                            Citace
                          </Typography>
                          {values.dataFields &&
                            values.dataFields.map((d, index) => {
                              return (
                                <div
                                  key={d.tag + index}
                                  className={classNames(classes.recordWrapper)}
                                >
                                  <div
                                    className={classNames(
                                      classesLayout.flex,
                                      classesLayout.alignCenter
                                    )}
                                  >
                                    <Typography
                                      className={classNames(
                                        classesText.textBold
                                      )}
                                    >
                                      {d.tag}
                                    </Typography>
                                    <IconButton
                                      color="inherit"
                                      onClick={() => dataFields.remove(index)}
                                    >
                                      <Remove color="inherit" />
                                    </IconButton>
                                  </div>
                                  <div
                                    className={classNames(
                                      classesLayout.flex,
                                      classesLayout.spaceBetween
                                    )}
                                  >
                                    <div style={{ width: "45%" }}>
                                      <Field
                                        name={`dataFields.${index}.indicator1`}
                                        render={({
                                          field,
                                          form
                                        }: FieldProps<RecordRequest>) => (
                                          <InputText
                                            type="text"
                                            field={field}
                                            form={form}
                                            fullWidth={false}
                                            inputProps={{
                                              placeholder: "Indikátor 1"
                                            }}
                                          />
                                        )}
                                      />
                                    </div>
                                    <div style={{ width: "45%" }}>
                                      <Field
                                        name={`dataFields.${index}.indicator2`}
                                        render={({
                                          field,
                                          form
                                        }: FieldProps<RecordRequest>) => (
                                          <InputText
                                            type="text"
                                            field={field}
                                            form={form}
                                            fullWidth={false}
                                            inputProps={{
                                              placeholder: "Indikátor 2"
                                            }}
                                          />
                                        )}
                                      />
                                    </div>
                                  </div>
                                  <FieldArray
                                    name={`dataFields.${index}.subfields`}
                                    render={subfields => (
                                      <>
                                        <div
                                          className={classNames(
                                            classesLayout.flex,
                                            classesLayout.alignCenter
                                          )}
                                        >
                                          <Typography
                                            className={classNames(
                                              classesText.textBold
                                            )}
                                          >
                                            Code
                                          </Typography>
                                          <IconButton
                                            color="inherit"
                                            onClick={() =>
                                              subfields.push({
                                                code: "",
                                                data: ""
                                              })
                                            }
                                          >
                                            <Add color="inherit" />
                                          </IconButton>
                                        </div>
                                        {get(
                                          values,
                                          `dataFields[${index}].subfields`,
                                          []
                                        ).map(
                                          (
                                            subField: SubfieldsEntity,
                                            indexSubField: number
                                          ) => (
                                            <div
                                              key={
                                                subField.code + indexSubField
                                              }
                                            >
                                              <div
                                                className={classNames(
                                                  classesLayout.flex,
                                                  classesLayout.alignCenter
                                                )}
                                              >
                                                <div
                                                  style={{
                                                    width: "5rem",
                                                    marginRight: "1rem"
                                                  }}
                                                >
                                                  <Field
                                                    name={`dataFields.${index}.subfields.${indexSubField}.code`}
                                                    render={({
                                                      field,
                                                      form
                                                    }: FieldProps<
                                                      RecordRequest
                                                    >) => (
                                                      <InputText
                                                        type="text"
                                                        field={field}
                                                        form={form}
                                                        fullWidth={false}
                                                        inputProps={{
                                                          placeholder: "Code"
                                                        }}
                                                      />
                                                    )}
                                                  />
                                                </div>
                                                <div
                                                  style={{
                                                    width: "100%",
                                                    display: "flex",
                                                    alignItems: "center"
                                                  }}
                                                >
                                                  <Field
                                                    name={`dataFields.${index}.subfields.${indexSubField}.data`}
                                                    render={({
                                                      field,
                                                      form
                                                    }: FieldProps<
                                                      RecordRequest
                                                    >) => (
                                                      <InputText
                                                        type="text"
                                                        field={field}
                                                        form={form}
                                                        inputProps={{
                                                          placeholder: "Data"
                                                        }}
                                                      />
                                                    )}
                                                  />
                                                  <IconButton
                                                    color="inherit"
                                                    className={classNames(
                                                      classesSpacing.mlAuto
                                                    )}
                                                    onClick={() =>
                                                      subfields.remove(
                                                        indexSubField
                                                      )
                                                    }
                                                  >
                                                    <Remove color="inherit" />
                                                  </IconButton>
                                                </div>
                                              </div>
                                            </div>
                                          )
                                        )}
                                      </>
                                    )}
                                  />
                                </div>
                              );
                            })}
                        </div>
                      </>
                    )}
                  />
                </div>
              </div>
              <Divider className={classesSpacing.mt3} />
              <div
                className={classNames(
                  classesLayout.flex,
                  classesLayout.justifyCenter,
                  classesSpacing.mb1
                )}
              >
                <Button
                  className={classesSpacing.mt3}
                  variant="contained"
                  color="primary"
                  type="submit"
                >
                  {record ? "Změnit citaci" : "Vytvořit citaci"}
                </Button>
              </div>
            </Form>
          );
        }}
      />
    </>
  );
};

export const RecordsForm = withRouter(RecordsFormView);
