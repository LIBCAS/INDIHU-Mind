import React from "react";

import {
  AsyncSelect as AsyncSelectComponent,
  AsyncSelectProps as AsyncSelectComponentProps
} from "../asyncSelect";
import { FormFieldWrapper, FormFieldWrapperProps } from "./FormFieldWrapper";

interface AsyncSelectProps
  extends FormFieldWrapperProps,
    AsyncSelectComponentProps {}

export const AsyncSelect: React.FC<AsyncSelectProps> = ({
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
      <AsyncSelectComponent
        {...props}
        value={field.value}
        onChange={handleChange}
      />
    </FormFieldWrapper>
  );
};
