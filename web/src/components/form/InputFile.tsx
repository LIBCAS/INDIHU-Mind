import React from "react";
import Button from "@material-ui/core/Button";
import { FormikProps } from "formik";

// import { GlobalContext } from "../../context/Context";
// import {
//   STATUS_ERROR_COUNT_CHANGE,
//   STATUS_ERROR_TEXT_SET
// } from "../../context/reducers/status";
import { FileProps } from "../../types/file";
import { FileType } from "../../enums";

interface InputFileProps {
  formikBag: FormikProps<FileProps>;
  setFormStage: Function;
}

export const InputFile: React.FC<InputFileProps> = ({
  formikBag,
  setFormStage
}) => {
  // const context: any = useContext(GlobalContext);
  // const dispatch: Function = context.dispatch;
  const { setFieldValue } = formikBag;
  const onChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { target } = event;
    if (target && target.files) {
      const files = Array.from(target.files);
      files.forEach(f => {
        // USER_QUOTA_REACHED, FILE_TOO_BIG, FILE_FORBIDDEN
        //  check max 10 MB & check forbidden extensions (.exe etc.)
        // const filesize = f.size / 1024 / 1024;
        // if (filesize > 10) {
        //   dispatch({
        //     type: STATUS_ERROR_TEXT_SET,
        //     payload: `Chyby - překročena maximální velikost souboru (10 MB)`
        //   });
        //   dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
        //   return false;
        // }
        const name = f.name.substr(0, f.name.lastIndexOf("."));
        setFieldValue("name", name);
        setFieldValue("providerType", FileType.LOCAL);
        const extension = f.name.slice(
          ((f.name.lastIndexOf(".") - 1) >>> 0) + 2
        );
        setFieldValue("type", extension);
        setFieldValue("content", f);
      });
      setFormStage("file-selected");
    }
  };
  return (
    <>
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
