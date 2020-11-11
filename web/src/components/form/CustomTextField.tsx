import * as React from "react";
import { FieldProps } from "formik";
import { TextField } from "@material-ui/core";

interface CustomTextFieldProps extends FieldProps {
  label: string;
  type: string;
}

export const CustomTextField: React.FC<CustomTextFieldProps> = ({
  field,
  form: { touched, errors },
  label,
  type,
}) => {
  const errorText =
    errors[field.name] && touched[field.name] ? errors[field.name] : "";

  return (
    <TextField
      id="field"
      label={label}
      type={type}
      helperText={errorText}
      error={!!errorText}
      fullWidth
      {...field}
    />
  );
};
