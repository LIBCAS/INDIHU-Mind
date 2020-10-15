import React, { useState, useContext } from "react";
import { Form, Field, FieldProps } from "formik";
import Typography from "@material-ui/core/Typography";
import Button from "@material-ui/core/Button";
import * as Yup from "yup";
import classNames from "classnames";

import { GlobalContext } from "../../context/Context";
import { usersUpdated } from "../../context/actions/users";
import { Formik } from "../../components/form/Formik";
import { api } from "../../utils/api";
import { InputText } from "../../components/form/InputText";
import { Divider } from "../../components/divider/Divider";
import { Loader } from "../../components/loader/Loader";
import { MessageSnackbar } from "../../components/messages/MessageSnackbar";

import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";

interface UserCreateFormValues {
  email: string;
}

const initialValues = {
  email: ""
};

const UserCreateSchema = Yup.object().shape({
  email: Yup.string()
    .email("Špatný formát emailu")
    .required("Povinné")
});

interface UserCreateFormProps {
  setOpen: Function;
  setUserCreated: Function;
}

export const UserCreateForm: React.FC<UserCreateFormProps> = ({
  setOpen,
  setUserCreated
}) => {
  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;
  const classesSpacing = useSpacingStyles();
  const classesLayout = useLayoutStyles();
  const [errorShow, setErrorShow] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(false);

  const handleError = () => {
    setErrorShow(true);
  };
  return (
    <>
      <Loader loading={loading} />
      {errorShow && <MessageSnackbar setVisible={setErrorShow} />}
      <Formik
        initialValues={initialValues}
        validationSchema={UserCreateSchema}
        onSubmit={(values: UserCreateFormValues) => {
          if (loading) return false;
          setLoading(true);
          api()
            .post(`admin/user/${values.email}/register`)
            .then(() => {
              usersUpdated(dispatch, true);
              setUserCreated(true);
              setOpen(false);
              setLoading(false);
            })
            .catch(() => {
              handleError();
              setLoading(false);
            });
        }}
        render={() => (
          <Form>
            <div
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
                gutterBottom
              >
                Registrace uživatele
              </Typography>
              <Field
                name="email"
                render={({ field, form }: FieldProps<UserCreateFormValues>) => (
                  <InputText
                    label="Email"
                    type="email"
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
                Registrovat
              </Button>
            </div>
          </Form>
        )}
      />
    </>
  );
};
