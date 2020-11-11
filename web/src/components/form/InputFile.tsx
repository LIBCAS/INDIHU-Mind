import React, { useState } from "react";
import Button from "@material-ui/core/Button";
import { FormikProps } from "formik";

import { MessageSnackbar } from "../messages/MessageSnackbar";
import { FileProps } from "../../types/file";
import { FileType } from "../../enums";

interface InputFileProps {
  formikBag: FormikProps<FileProps>;
  setFormStage: Function;
}

export const InputFile: React.FC<InputFileProps> = ({
  formikBag,
  setFormStage,
}) => {
  const [message, setMessage] = useState<boolean | string>(false);

  const { setFieldValue } = formikBag;

  const onChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { target } = event;
    if (target && target.files) {
      const files = Array.from(target.files);
      const f = files[0];

      const name = f.name.substr(0, f.name.lastIndexOf("."));
      const extension = f.name.slice(((f.name.lastIndexOf(".") - 1) >>> 0) + 2);

      if (!name || !extension) {
        setMessage("Chyba: Soubor nemá příponu. Nahrajte soubor s příponou.");
        return;
      }

      setFieldValue("name", name);
      setFieldValue("providerType", FileType.LOCAL);
      setFieldValue("type", extension);
      setFieldValue("content", f);
      setFormStage("file-selected");
    }
  };

  return (
    <>
      {message && <MessageSnackbar setVisible={setMessage} message={message} />}
      <input
        disabled={formikBag.isSubmitting}
        style={{ display: "none" }}
        id="raised-button-file"
        type="file"
        onChange={onChange}
      />
      <label style={{ width: "100%" }} htmlFor="raised-button-file">
        <Button
          fullWidth
          variant="outlined"
          component="span"
          disabled={formikBag.isSubmitting}
        >
          Nahrát lokální soubor
        </Button>
      </label>
    </>
  );
};
