import React, { useState, useEffect } from "react";
import { RouteComponentProps } from "react-router-dom";
import { Formik, Form, Field } from "formik";
import Typography from "@material-ui/core/Typography";
import Button from "@material-ui/core/Button";
import * as Yup from "yup";

import { useStyles } from "./_styles";
import { MessageSnackbar } from "../../components/messages/MessageSnackbar";
import { CustomTextField } from "../../components/form/CustomTextField";
import Navbar from "../../components/login/navbar/Navbar";
import { newPassword } from "./_utils";

interface ResetPasswordFormValues {
  password: string;
  password2: string;
}

export const ResetPassword: React.FC<RouteComponentProps> = ({
  history,
  location,
}) => {
  const classes = useStyles();
  const [error, setError] = useState<string | boolean>(false);
  const [token, setToken] = useState<string>("");

  useEffect(() => {
    const token = new URLSearchParams(location.search).get("token");
    if (token) {
      setToken(token);
    } else {
      history.push("/");
    }
  }, [history, location.search]);

  return (
    <>
      <div className={classes.header}>
        <Navbar />
      </div>
      <div className={classes.resetPasswordContainer}>
        {error && <MessageSnackbar setVisible={setError} message={error} />}
        <div className={classes.resetPassword}>
          <Formik
            initialValues={{ password: "", password2: "" }}
            validationSchema={Yup.object().shape({
              password: Yup.string().required("Povinné"),
              password2: Yup.string().required("Povinné"),
            })}
            onSubmit={async (
              { password, password2 }: ResetPasswordFormValues,
              actions: any
            ) => {
              actions.setSubmitting(true);
              if (password !== password2) {
                setError("Zadaná hesla se neshodují.");
              } else {
                const ok = await newPassword(token, password);
                if (ok) {
                  history.replace("/");
                } else {
                  setError("Nepodařilo se změnit heslo.");
                }
              }
              actions.setSubmitting(false);
            }}
          >
            {({ isSubmitting }) => (
              <Form>
                <Typography
                  variant="h5"
                  color="inherit"
                  align="center"
                  gutterBottom
                >
                  Zadejte nové heslo
                </Typography>
                <div className={classes.field}>
                  <Field
                    name="password"
                    label="Heslo"
                    type="password"
                    component={CustomTextField}
                  />
                </div>
                <div className={classes.field}>
                  <Field
                    name="password2"
                    label="Potvrzení hesla"
                    type="password"
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
              </Form>
            )}
          </Formik>
        </div>
      </div>
    </>
  );
};
