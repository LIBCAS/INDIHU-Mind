import React, { useContext, useState, useEffect } from "react";
import { GlobalContext, StateProps } from "../../context/Context";
import { OptionType } from "../../components/form/reactSelect/_reactSelectTypes";
import { get } from "lodash";
import { parseLabel } from "../cardCreate/_utils";
import { Field, FieldProps, Formik, Form, FormikProps } from "formik";
import { ReactSelect } from "../../components/form/reactSelect/ReactSelect";
import { Loader } from "../../components/loader/Loader";
import { Button, Typography } from "@material-ui/core";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import classNames from "classnames";
import { recordTemplateGet } from "../../context/actions/recordTemplate";
import { Popover } from "../../components/portal/Popover";
import { notEmpty } from "../../utils/form/validate";
import * as store from "../../utils/store";
import {
  STATUS_ERROR_COUNT_CHANGE,
  STATUS_ERROR_TEXT_SET
} from "../../context/reducers/status";
import { api } from "../../utils/api";

interface RecordsPdfProps {
  open: boolean;
  setOpen: any;
  checkboxRows: any[];
  anchorEl: any;
}

export const RecordsPdf: React.FC<RecordsPdfProps> = ({
  open,
  setOpen,
  anchorEl,
  checkboxRows
}) => {
  const classesText = useTextStyles();
  const classesLayout = useLayoutStyles();
  const classesSpacing = useSpacingStyles();
  const context: any = useContext(GlobalContext);
  const state: StateProps = context.state;
  const dispatch: Function = context.dispatch;
  const [loading, setLoading] = useState(false);
  const [options, setOptions] = useState<OptionType[]>([]);
  useEffect(() => {
    recordTemplateGet(dispatch);
  }, []);
  useEffect(() => {
    const templatesParsed = state.recordTemplate.recordsTemplates.map(
      parseLabel
    );
    setOptions(templatesParsed);
  }, [state.recordTemplate.recordsTemplates]);

  const onSubmit = (values: any) => {
    if (loading) {
      return;
    }
    setLoading(true);
    // const id = get(values, 'templates.value');
    // const ids = checkboxRows.map(c => c.id);

    const id = "6f86f02c-9d41-41b8-a8e6-7252462c8b55";
    const ids = ["e7a8c70-1049-11ea-9a9f-362b9e155667"];
    const payload = { templateId: id, ids };
    if (id) {
      api()
        .post(`template/generate-pdf`, { body: JSON.stringify(payload) })
        .then(response => {
          let filename = "";
          let disposition = response.headers.get("Content-Disposition");
          if (disposition && disposition.indexOf("attachment") !== -1) {
            let filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
            let matches = filenameRegex.exec(disposition);
            if (matches != null && matches[1])
              filename = matches[1].replace(/['"]/g, "");
          }
          return response.blob().then(blob => ({ blob, name: filename }));
        })
        .then(({ blob, name }) => {
          const url = window.URL.createObjectURL(blob);
          let a = document.createElement("a");
          a.href = url;
          a.download = name;
          document.body.appendChild(a);
          a.click();
          dispatch({
            type: STATUS_ERROR_TEXT_SET,
            payload: `PDF bylo úspěšně vygenerováno`
          });
          dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
          setLoading(false);
          a.remove();
        })
        .catch(e => {
          setLoading(false);
          dispatch({
            type: STATUS_ERROR_TEXT_SET,
            payload: `PDF se nepovedlo vygenerovat`
          });
          dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
        });
    }
  };
  return (
    <>
      <Popover
        open={open}
        setOpen={setOpen}
        overflowVisible
        anchorEl={anchorEl.current}
        cancelButton={false}
        content={
          <div>
            <Formik
              initialValues={{
                templates: null
              }}
              enableReinitialize
              onSubmit={onSubmit}
              render={(formikBag: FormikProps<any>) => (
                <Form>
                  <div>
                    <Loader loading={loading} />
                    <Typography
                      className={classNames(
                        classesText.textCenter,
                        classesSpacing.mt1,
                        classesSpacing.mr1,
                        classesSpacing.ml1
                      )}
                      variant="h5"
                    >
                      Generovat PDF pro vybrané citace
                    </Typography>
                    <Field
                      name="templates"
                      validate={notEmpty}
                      render={({ field, form }: FieldProps<any>) => {
                        return (
                          <div className={classNames(classesSpacing.m1)}>
                            <div className={classesText.textCenter}>
                              <Button
                                type="submit"
                                color="primary"
                                variant="contained"
                                className={classNames(
                                  classesSpacing.mt2,
                                  classesSpacing.mb1
                                )}
                              >
                                Vygenerovat PDF
                              </Button>
                            </div>

                            <ReactSelect
                              form={form}
                              field={field}
                              loading={false}
                              label="Citační šablona"
                              options={options}
                              isMulti={false}
                              autoFocus
                              menuIsOpen
                            />
                          </div>
                        );
                      }}
                    />
                  </div>
                </Form>
              )}
            />
          </div>
        }
      />
    </>
  );
};
