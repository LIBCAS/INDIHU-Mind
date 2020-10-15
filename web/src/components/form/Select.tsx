import React from "react";
import classNames from "classnames";
import Input from "@material-ui/core/Input";
import InputLabel from "@material-ui/core/InputLabel";
import MenuItem from "@material-ui/core/MenuItem";
import MaterialSelect from "@material-ui/core/Select";
import Typography from "@material-ui/core/Typography";

import { useStyles as useFormStyles } from "./_formStyles";

interface SelectProps {
  options: {
    value: string;
    label: string;
  }[];
  field: any;
  form: any;
  label?: string;
  placeholder?: string;
  onChange?: any;
  multiple?: boolean;
}

export const Select: React.FC<SelectProps> = ({
  label,
  options,
  field,
  form,
  onChange,
  multiple,
  placeholder
}) => {
  const classesForm = useFormStyles();
  const handleChange = (e: any) => {
    field.onChange(e);
    onChange && onChange(e);
  };
  return (
    <>
      {label && (
        <InputLabel className={classesForm.label} htmlFor={field.name}>
          {label}
        </InputLabel>
      )}
      <MaterialSelect
        displayEmpty
        error={Boolean(form.touched[field.name] && form.errors[field.name])}
        {...field}
        value={form.values[field.name]}
        multiple={multiple}
        onChange={handleChange}
        input={
          <Input
            disableUnderline
            inputProps={{
              className: classNames(classesForm.default, classesForm.select)
            }}
          />
        }
      >
        <MenuItem className={classesForm.label} disabled value="">
          {placeholder ? placeholder : "Vyberte"}
        </MenuItem>
        {options.map((opt, i) => (
          <MenuItem key={i} value={opt.value}>
            {opt.label}
          </MenuItem>
        ))}
      </MaterialSelect>
      <Typography className={classesForm.selectError} color="error">
        {form.touched[field.name] &&
          form.errors[field.name] &&
          form.errors[field.name]}
      </Typography>
    </>
  );
};
