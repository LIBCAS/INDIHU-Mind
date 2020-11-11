import React, { useState } from "react";
import { FormikProps } from "formik";

import { FileProps } from "../../types/file";
import { FileUploadPopper } from "../../components/file/FileUploadPopper";
import { MessageSnackbar } from "../../components/messages/MessageSnackbar";
import { fileUpload, createErrorMessage } from "./_utils";

interface AttachmentsAddProps {
  buttonRef: any;
  open: boolean;
  setOpen: (open: boolean) => void;
  refresh: Function;
}

export const AttachmentsAdd: React.FC<AttachmentsAddProps> = ({
  refresh,
  ...props
}) => {
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
        refresh();
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
      <FileUploadPopper
        {...props}
        onSubmit={onSubmit}
        loading={loading}
        isNew={true}
      />
    </>
  );
};
