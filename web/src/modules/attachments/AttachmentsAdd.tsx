import React, { useState } from "react";
import { FormikProps } from "formik";
import Button from "@material-ui/core/Button";

import { FileProps } from "../../types/file";
import { FileUploadPicker } from "../../components/file/FileUploadPicker";
import { MessageSnackbar } from "../../components/messages/MessageSnackbar";
import { fileUpload, createErrorMessage } from "./_utils";

interface AttachmentsAddProps {
  update: () => void;
}

export const AttachmentsAdd: React.FC<AttachmentsAddProps> = ({ update }) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const onSubmit = async (
    values: FileProps,
    close: () => void,
    formikBag: FormikProps<FileProps>
  ) => {
    try {
      setLoading(true);

      const ok = await fileUpload(values);

      setLoading(false);

      if (ok) {
        update();
        close();
        setError(null);
      } else {
        formikBag.setSubmitting(false);

        setError("Nepovedlo se přidat soubor.");
      }
    } catch (e) {
      console.log(e);
      setLoading(false);

      formikBag.setSubmitting(false);

      setError(createErrorMessage(e));
    }
  };

  return (
    <>
      {error && <MessageSnackbar setVisible={setError} message={error} />}
      <FileUploadPicker
        onSubmit={onSubmit}
        loading={loading}
        isNew={true}
        ButtonComponent={({ onClick }) => (
          <Button
            variant="contained"
            color="primary"
            style={{ width: "100%" }}
            onClick={onClick}
          >
            Přidat
          </Button>
        )}
      />
    </>
  );
};
