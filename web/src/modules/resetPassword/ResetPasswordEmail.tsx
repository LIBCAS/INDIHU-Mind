import React from "react";
import { Formik, Form, Field } from "formik";
import Typography from "@material-ui/core/Typography";
import Button from "@material-ui/core/Button";
import * as Yup from "yup";

import { CustomTextField } from "../../components/form/CustomTextField";
import { useStyles } from "./_styles";
import { resetPassword } from "./_utils";

interface ResetPasswordEmailFormValues {
  email: string;
}

interface ResetPasswordEmailProps {
  onClose: () => void;
  setMessage: (m: boolean | string) => void;
}

export const ResetPasswordEmail: React.FC<ResetPasswordEmailProps> = ({
  onClose,
  setMessage
}) => {
  const classes = useStyles();

  return (
    <>
      <Formik
        initialValues={{ email: "" }}
        validationSchema={Yup.object().shape({
          email: Yup.string().required("Povinné")
        })}
        onSubmit={async (
          { email }: ResetPasswordEmailFormValues,
          actions: any
        ) => {
          actions.setSubmitting(true);
          const ok = await resetPassword(email);
          actions.setSubmitting(false);
          if (ok) {
            onClose();
          }
          setMessage(
            ok
              ? "Instrukce byly zaslány na Váš email."
              : "Nepodařilo se resetovat heslo. Zkontrolujte, prosím, zadaný email."
          );
        }}
      >
        {({ isSubmitting }) => (
          <Form>
            <div className={classes.resetPasswordEmail}>
              <Typography
                variant="h6"
                color="inherit"
                align="center"
                gutterBottom
              >
                Zadejte email, kam budou poslány instrukce pro změnu hesla.
              </Typography>
              <div className={classes.field}>
                <Field
                  name="email"
                  label="Email"
                  type="text"
                  component={CustomTextField}
                />
              </div>
              <Button
                variant="contained"
                color="primary"
                type="submit"
                disabled={isSubmitting}
              >
                Potvrdit
              </Button>
            </div>
          </Form>
        )}
      </Formik>
    </>
  );
};
