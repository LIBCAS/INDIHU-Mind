import React from "react";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import MaterialSwitch from "@material-ui/core/Switch";
import InputLabel from "@material-ui/core/InputLabel";

import { useStyles as useFormStyles } from "./_formStyles";

interface SwitchProps {
  label: string | JSX.Element;
  field: any;
  form: any;
  inputProps?: any;
  title?: any;
  autoFocus?: boolean;
  secondary?: boolean;
}

export const Switch: React.FC<SwitchProps> = ({
  label,
  field,
  title,
  secondary
}) => {
  const classesForm = useFormStyles();
  return (
    <>
      {title && (
        <InputLabel className={classesForm.label} htmlFor={field.name}>
          {title}
        </InputLabel>
      )}
      <FormControlLabel
        control={
          <MaterialSwitch
            autoFocus
            checked={field.value}
            color="primary"
            {...field}
            classes={
              secondary
                ? {
                    switchBase: classesForm.switchBase,
                    disabled: classesForm.disabled,
                    track: classesForm.track,
                    checked: classesForm.checked,
                    colorPrimary: classesForm.disabled
                  }
                : {}
            }
          />
        }
        label={label}
      />
    </>
  );
};

export default Switch;
