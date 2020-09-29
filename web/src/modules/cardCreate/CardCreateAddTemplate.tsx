import React, { useState, useContext } from "react";
import { FormikProps, Field } from "formik";
import Button from "@material-ui/core/Button";

import { GlobalContext } from "../../context/Context";
import { notEmpty } from "../../utils/form/validate";

import { Formik } from "../../components/form/Formik";
import { Loader } from "../../components/loader/Loader";
import { MessageSnackbar } from "../../components/messages/MessageSnackbar";
import { InputText } from "../../components/form/InputText";

import { useStyles } from "./_cardCreateStyles";

import { AttributeProps } from "../../types/attribute";

import { onSubmitTemplate } from "./_utils";

const initialValues = {
  name: ""
};

interface CardCreateAddTemplateProps {
  setTemplateOpen: Function;
  attributes: AttributeProps[];
  loadTemplates: Function;
}

export const CardCreateAddTemplate: React.FC<CardCreateAddTemplateProps> = ({
  setTemplateOpen,
  attributes,
  loadTemplates
}) => {
  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(false);
  const classes = useStyles();
  return (
    <>
      <Loader loading={loading} />
      {error && <MessageSnackbar setVisible={setError} />}
      <Formik
        initialValues={initialValues}
        onSubmit={values => {
          if (loading) return false;
          setError(false);
          setLoading(true);
          onSubmitTemplate(
            values,
            attributes,
            setTemplateOpen,
            loadTemplates,
            setLoading,
            setError,
            dispatch
          );
        }}
        render={(formikBag: FormikProps<any>) => (
          <form
            onReset={formikBag.handleReset}
            onSubmit={(e: any) => {
              e.preventDefault();
              e.stopPropagation();
              if (formikBag.isSubmitting) return false;
              formikBag.submitForm();
            }}
          >
            <div className={classes.attributeWrapper}>
              <Field
                name="name"
                validate={notEmpty}
                render={({ field, form }: any) => (
                  <InputText
                    field={field}
                    form={form}
                    label="Název"
                    inputProps={{ autoFocus: false }}
                  />
                )}
              />
              <div className={classes.actionWrapper}>
                <Button size="small" color="primary" type="submit">
                  Vytvořit šablonu
                </Button>
                <Button
                  size="small"
                  color="inherit"
                  onClick={() => setTemplateOpen(false)}
                >
                  Zrušit
                </Button>
              </div>
            </div>
          </form>
        )}
      />
    </>
  );
};
