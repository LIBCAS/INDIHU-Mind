import React from "react";
import classNames from "classnames";
import TextField from "@material-ui/core/TextField";

import { useStyles as useFormStyles } from "./_formStyles";
import { FormFieldWrapper, FormFieldWrapperProps } from "./FormFieldWrapper";

interface InputTextProps extends FormFieldWrapperProps {
  type?: "text" | "number" | "email" | "password";
  disabled?: boolean;
  multiline?: boolean;
  inputProps?: any;
  autoFocus?: boolean;
  fullWidth?: boolean;
  className?: string;
  rowsMax?: number;
}

export const InputText: React.FC<InputTextProps> = ({
  label,
  field,
  form,
  type,
  disabled,
  multiline,
  inputProps,
  autoFocus,
  fullWidth = true,
  className,
  rowsMax,
  ...props
}) => {
  const classesForm = useFormStyles();

  return (
    <FormFieldWrapper {...props} form={form} field={field} label={label}>
      <TextField
        rowsMax={rowsMax}
        type={type}
        InputProps={{
          autoComplete: "off",
          disableUnderline: true
        }}
        multiline={multiline}
        fullWidth={fullWidth}
        autoFocus={autoFocus}
        disabled={disabled}
        className={className}
        inputProps={{
          ...inputProps,
          className: classNames(classesForm.default, {
            [classesForm.textArea]: multiline
          })
        }}
        error={Boolean(form.touched[field.name] && form.errors[field.name])}
        {...field}
      />
    </FormFieldWrapper>
  );
};
