import React, { useState } from "react";
import { Formik, Form, Field } from "formik";
import Button from "@material-ui/core/Button";
import * as Yup from "yup";

import { CustomTextField } from "../../form/CustomTextField";
import { useStyles } from "./_loginFormStyle";
import { MessageSnackbar } from "../../../components/messages/MessageSnackbar";
import { api } from "../../../utils/api";
import * as store from "../../../utils/store";
import { Modal } from "../../portal/Modal";
import { ResetPasswordEmail } from "../../../modules/resetPassword/ResetPasswordEmail";

interface LoginFormValues {
  email: string;
  password: string;
}

const initialValues = {
  email: "",
  password: ""
};

const LoginSchema = Yup.object().shape({
  email: Yup.string()
    .email("Špatný formát emailu")
    .required("Povinné"),
  password: Yup.string().required("Povinné")
});

export const LoginForm: React.FC<any> = ({ history }) => {
  const classes = useStyles();

  const [open, setOpen] = useState<boolean>(false);
  const [message, setMessage] = useState<boolean | string>(false);
  const handleError = () => {
    setMessage("Špatné heslo nebo email.");
  };

  return (
    <>
      <div className={classes.form}>
        {message && (
          <MessageSnackbar setVisible={setMessage} message={message} />
        )}
        <Formik
          initialValues={initialValues}
          validationSchema={LoginSchema}
          onSubmit={(values: LoginFormValues, actions: any) => {
            actions.setSubmitting(true);
            store.remove("token");
            const param = btoa(
              unescape(encodeURIComponent(`${values.email}:${values.password}`))
            );
            api({ skipToken: true }, { Authorization: `Basic ${param}` })
              .post("login")
              .then(() => {
                const token = store.get("token", undefined);
                if (token) {
                  history.push("/cards");
                } else {
                  handleError();
                }
                actions.setSubmitting(false);
              })
              .catch(() => {
                handleError();
                actions.setSubmitting(false);
              });
          }}
        >
          {({ isSubmitting }) => (
            <Form>
              <div className={classes.field}>
                <Field
                  name="email"
                  label="Email"
                  type="text"
                  component={CustomTextField}
                />
              </div>
              <div className={classes.field}>
                <Field
                  name="password"
                  label="Heslo"
                  type="password"
                  component={CustomTextField}
                />
              </div>
              <div className={classes.buttons}>
                <Button
                  variant="contained"
                  color="primary"
                  type="submit"
                  disabled={isSubmitting}
                >
                  Přihlásit se
                </Button>
                <div className={classes.link} onClick={() => setOpen(true)}>
                  Zapomenuté heslo
                </div>
              </div>
            </Form>
          )}
        </Formik>
      </div>
      <Modal
        open={open}
        setOpen={setOpen}
        content={
          <ResetPasswordEmail
            onClose={() => setOpen(false)}
            setMessage={setMessage}
          />
        }
      />
    </>
  );
};
