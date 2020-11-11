import React from "react";
import InputLabel from "@material-ui/core/InputLabel";

import { GPSPicker as GPSPickerComponent } from "../gpsPicker";
import { useStyles as useFormStyles } from "./_styles";

interface GPSPickerProps {
  field: any;
  form: any;
  label?: any;
}

export const GPSPicker: React.FC<GPSPickerProps> = ({ field, form, label }) => {
  const classesForm = useFormStyles();

  const handleChange = (value: any) => {
    field.onChange(value);
    form.setFieldValue(field.name, value);
  };

  const touched = form.touched[field.name];
  const error = form.errors[field.name];

  return (
    <>
      {label && (
        <InputLabel className={classesForm.label} htmlFor={field.name}>
          {label}
        </InputLabel>
      )}
      <GPSPickerComponent
        {...field}
        onChange={handleChange}
        error={touched ? error : undefined}
      />
    </>
  );
};
