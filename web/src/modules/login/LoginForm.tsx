import React, { useState } from "react";
import { Form, Field, FieldProps } from "formik";
import Typography from "@material-ui/core/Typography";
import Button from "@material-ui/core/Button";
import * as Yup from "yup";
import classNames from "classnames";
import * as store from "../../utils/store";

import { Formik } from "../../components/form/Formik";
import { api } from "../../utils/api";
import { InputText } from "../../components/form/InputText";
import { Divider } from "../../components/divider/Divider";
import { MessageSnackbar } from "../../components/messages/MessageSnackbar";

import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";

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
  const classesSpacing = useSpacingStyles();
  const classesLayout = useLayoutStyles();
  const [errorShow, setErrorShow] = useState<boolean>(false);
  const [error, setError] = useState<string>("");

  const handleError = () => {
    setError("Špatné heslo nebo email.");
    setErrorShow(true);
  };
  return (
    <>
      {errorShow && (
        <MessageSnackbar setVisible={setErrorShow} message={error} />
      )}
      <Formik
        initialValues={initialValues}
        validationSchema={LoginSchema}
        onSubmit={(values: LoginFormValues) => {
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
            })
            .catch(() => {
              handleError();
            });
        }}
        render={() => (
          <Form>
            <div
              style={{ maxWidth: "300px" }}
              className={classNames(
                classesLayout.flex,
                classesLayout.flexWrap,
                classesLayout.justifyCenter,
                classesSpacing.m1
              )}
            >
              <Typography
                variant="h5"
                color="primary"
                align="center"
                noWrap
                gutterBottom
              >
                Přihlášení indihu-mind
              </Typography>
              <Field
                name="email"
                render={({ field, form }: FieldProps<LoginFormValues>) => (
                  <InputText
                    label="Email"
                    type="email"
                    field={field}
                    form={form}
                    autoFocus
                  />
                )}
              />
              <Field
                name="password"
                render={({ field, form }: FieldProps<LoginFormValues>) => (
                  <InputText
                    label="Heslo"
                    type="password"
                    field={field}
                    form={form}
                    autoFocus
                  />
                )}
              />
              <Divider className={classesSpacing.mt3} />
              <Button
                className={classesSpacing.mt3}
                variant="contained"
                color="primary"
                type="submit"
              >
                Přihlásit se
              </Button>
            </div>
          </Form>
        )}
      />
    </>
  );
};
