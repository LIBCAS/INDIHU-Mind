import React, { useState } from "react";
import { Formik, Form, Field } from "formik";
import Button from "@material-ui/core/Button";
import { MyField } from "./MyField";
import { useStyles } from "./_loginFormStyle";
import * as Yup from "yup";
import { MessageSnackbar } from "../../../components/messages/MessageSnackbar";
import { api } from "../../../utils/api";
import * as store from "../../../utils/store";

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

  const [errorShow, setErrorShow] = useState<boolean>(false);
  const [error, setError] = useState<string>("");
  const handleError = () => {
    setError("Špatné heslo nebo email.");
    setErrorShow(true);
  };

  return (
    <div className={classes.form}>
      {errorShow && (
        <MessageSnackbar setVisible={setErrorShow} message={error} />
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
                component={MyField}
              />
            </div>
            <div className={classes.field}>
              <Field
                name="password"
                label="Heslo"
                type="password"
                component={MyField}
              />
            </div>
            <Button
              className={classes.submit}
              variant="contained"
              color="primary"
              type="submit"
              disabled={isSubmitting}
            >
              Přihlásit se
            </Button>
          </Form>
        )}
      </Formik>
    </div>
  );
};
