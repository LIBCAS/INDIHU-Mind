import React from "react";
import classNames from "classnames";
import InputLabel from "@material-ui/core/InputLabel";
import Typography from "@material-ui/core/Typography";
import TextField from "@material-ui/core/TextField";
import { getIn } from "formik";

import { useStyles as useFormStyles } from "./_formStyles";

interface InputTextProps {
  field: any;
  form: any;
  label?: string | JSX.Element;
  type?: "text" | "number" | "email" | "password";
  multiline?: boolean;
  inputProps?: any;
  autoFocus?: boolean;
  fullWidth?: boolean;
}

export const InputText: React.FC<InputTextProps> = ({
  label,
  field,
  form,
  type,
  multiline,
  inputProps,
  autoFocus,
  fullWidth = true
}) => {
  const classesForm = useFormStyles();
  return (
    <>
      {label && (
        <InputLabel className={classesForm.label} htmlFor={field.name}>
          {label}
        </InputLabel>
      )}
      <TextField
        type={type}
        InputProps={{
          autoComplete: "off",
          disableUnderline: true
        }}
        multiline={multiline}
        fullWidth={fullWidth}
        autoFocus={autoFocus}
        inputProps={{
          ...inputProps,
          className: classNames(classesForm.default, {
            [classesForm.textArea]: multiline
          })
        }}
        error={Boolean(form.touched[field.name] && form.errors[field.name])}
        {...field}
      />
      {form.touched[field.name] && form.errors[field.name] && (
        <Typography className={classNames(classesForm.error)} color="error">
          {form.touched[field.name] &&
            form.errors[field.name] &&
            form.errors[field.name]}
        </Typography>
      )}
    </>
  );
};
