import React, { useState } from "react";
import { Modal } from "../../components/portal/Modal";
import { Formik, Form, Field } from "formik";
import * as Yup from "yup";
import Typography from "@material-ui/core/Typography";
import { MessageSnackbar } from "../../components/messages/MessageSnackbar";
import { CustomTextField } from "../../components/form/CustomTextField";
import Button from "@material-ui/core/Button";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import classNames from "classnames";
import { changePassword } from "./_utils";

interface PasswordChangeProps {
  open: boolean;
  setOpen: Function;
}

interface PasswordChangeFormValues {
  currentPassword: string;
  newPassword: string;
  newPasswordRepeated: string;
}

export const PasswordChange: React.FC<PasswordChangeProps> = ({
  open,
  setOpen,
}) => {
  const classesSpacing = useSpacingStyles();

  const [message, setMessage] = useState<boolean | string>(false);

  return (
    <>
      {message && <MessageSnackbar setVisible={setMessage} message={message} />}
      <Modal
        withPadding={true}
        {...{ open, setOpen }}
        content={
          <Formik
            initialValues={{
              currentPassword: "",
              newPassword: "",
              newPasswordRepeated: "",
            }}
            validationSchema={Yup.object().shape({
              currentPassword: Yup.string().required("Povinné"),
              newPassword: Yup.string().required("Povinné"),
              newPasswordRepeated: Yup.string().required("Povinné"),
            })}
            onSubmit={async (
              {
                currentPassword,
                newPassword,
                newPasswordRepeated,
              }: PasswordChangeFormValues,
              actions: any
            ) => {
              actions.setSubmitting(true);
              if (newPassword !== newPasswordRepeated) {
                setMessage("Zadaná hesla se neshodují.");
              } else {
                const result = await changePassword(
                  currentPassword,
                  newPassword
                );
                if (result.ok) {
                  setOpen(false);
                  setMessage("Heslo bylo úspěšne změneno.");
                } else {
                  if (result.error === "WRONG_PASSWORD") {
                    setMessage("Zadali jste nesprávné heslo.");
                  } else {
                    setMessage("Nepodařilo se změnit heslo.");
                  }
                }
              }
              actions.setSubmitting(false);
            }}
          >
            {({ isSubmitting, values }) => (
              <Form>
                <Typography
                  variant="h5"
                  color="inherit"
                  align="center"
                  gutterBottom
                >
                  Změna hesla
                </Typography>
                <div>
                  <Field
                    name="currentPassword"
                    label="Současné heslo"
                    type="password"
                    component={CustomTextField}
                  />
                </div>
                <div>
                  <Field
                    name="newPassword"
                    label="Nové heslo"
                    type="password"
                    component={CustomTextField}
                  />
                </div>
                <div>
                  <Field
                    name="newPasswordRepeated"
                    label="Potvrzení nového hesla"
                    type="password"
                    validate={(value: string) =>
                      value === values.newPassword
                        ? undefined
                        : "Hesla se neshodují."
                    }
                    component={CustomTextField}
                  />
                </div>
                <Button
                  className={classNames(
                    classesSpacing.centerHorizontally,
                    classesSpacing.mt2
                  )}
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
        }
      />
    </>
  );
};
