import React from "react";
import InputLabel from "@material-ui/core/InputLabel";

import { GPSPicker as GPSPickerComponent } from "../gpsPicker";
import { useStyles as useFormStyles } from "./_formStyles";

interface GPSPickerProps {
  field: any;
  form: any;
  label?: any;
}

export const GPSPicker: React.FC<GPSPickerProps> = ({ field, form, label }) => {
  const classesForm = useFormStyles();

  const handleChange = (value: any) => {
    form.setFieldValue(field.name, value);
  };

  return (
    <>
      {label && (
        <InputLabel className={classesForm.label} htmlFor={field.name}>
          {label}
        </InputLabel>
      )}
      <GPSPickerComponent {...field} onChange={handleChange} />
    </>
  );
};
