import React from "react";

import {
  Select as SelectComponent,
  SelectProps as SelectComponentProps
} from "../select";
import { FormFieldWrapper, FormFieldWrapperProps } from "./FormFieldWrapper";

interface SelectProps extends FormFieldWrapperProps, SelectComponentProps {}

export const Select: React.FC<SelectProps> = ({
  form,
  field,
  label,
  onChange,
  ...props
}) => {
  const handleChange = (value: any) => {
    form.setFieldValue(field.name, value);
    onChange && onChange(value);
  };

  return (
    <FormFieldWrapper {...props} form={form} field={field} label={label}>
      <SelectComponent {...props} value={field.value} onChange={handleChange} />
    </FormFieldWrapper>
  );
};
