import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import classNames from "classnames";
import { Field, FieldProps, FormikProps } from "formik";
import React, { useState } from "react";
import * as Yup from "yup";
import { Formik } from "../../components/form/Formik";
import { InputText } from "../../components/form/InputText";
import { Loader } from "../../components/loader/Loader";
import { MessageSnackbar } from "../../components/messages/MessageSnackbar";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { Attachment, AttachmentUpdateProps } from "./_types";
import { fileUpdate } from "./_utils";

interface AttachmentCardRenameProps {
  item: Attachment;
  close: () => void;
}

export const AttachmentCardRename: React.FC<AttachmentCardRenameProps> = ({
  item,
  close,
}) => {
  const classesSpacing = useSpacingStyles();
  const classesLayout = useLayoutStyles();

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<boolean>(false);

  const onSubmit = async (values: AttachmentUpdateProps) => {
    if (loading) return false;
    setLoading(true);
    const ok = await fileUpdate(values);
    setLoading(false);
    setError(!ok);
    if (ok) {
      close();
    }
  };

  return (
    <>
      <Loader loading={loading} />
      {error && (
        <MessageSnackbar
          setVisible={setError}
          message="Nepodařilo se přejmenovat dokument."
        />
      )}
      <Formik
        initialValues={{
          id: item.id,
          name: item.name,
          linkedCards: item.linkedCards || [],
          records: item.records || [],
        }}
        enableReinitialize
        validationSchema={Yup.object().shape({
          name: Yup.string()
            .required("Povinné")
            .max(255, "Smí mít maximálně 255 znaků"),
        })}
        onSubmit={onSubmit}
        render={(formikBag: FormikProps<AttachmentUpdateProps>) => (
          <form
            onReset={formikBag.handleReset}
            onSubmit={(e: any) => {
              e.preventDefault();
              e.stopPropagation();
              if (formikBag.isSubmitting) return false;
              formikBag.submitForm();
            }}
          >
            <div
              className={classNames(
                classesLayout.flex,
                classesLayout.flexWrap,
                classesLayout.directionColumn,
                classesSpacing.m1
              )}
            >
              <Typography
                variant="h5"
                color="inherit"
                align="center"
                gutterBottom
              >
                Přejmenovat dokument
              </Typography>
              <Field
                name="name"
                render={({
                  field,
                  form,
                }: FieldProps<AttachmentUpdateProps>) => (
                  <InputText
                    label="Název"
                    field={field}
                    form={form}
                    autoFocus={false}
                  />
                )}
              />
              <Button
                className={classesSpacing.mt3}
                variant="contained"
                color="primary"
                type="submit"
                disabled={loading}
              >
                Přejmenovat
              </Button>
            </div>
          </form>
        )}
      />
    </>
  );
};
