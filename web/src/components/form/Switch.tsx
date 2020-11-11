import React from "react";
import classNames from "classnames";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import MaterialSwitch from "@material-ui/core/Switch";
import InputLabel from "@material-ui/core/InputLabel";

import { useStyles as useFormStyles } from "./_styles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";

interface SwitchWrapperProps {
  children: JSX.Element;
  field: any;
  title?: any;
  oneLine?: boolean;
}

interface SwitchProps {
  label: string | JSX.Element;
  field: any;
  form: any;
  inputProps?: any;
  title?: any;
  autoFocus?: boolean;
  secondary?: boolean;
  oneLine?: boolean;
}

const SwitchWrapper: React.FC<SwitchWrapperProps> = ({
  field,
  title,
  oneLine,
  children,
}) => {
  const classesLayout = useLayoutStyles();
  const classesForm = useFormStyles();

  const content = (
    <>
      {title && (
        <InputLabel
          className={classNames(
            classesForm.label,
            oneLine && classesForm.oneLineFieldLabel
          )}
          htmlFor={field.name}
        >
          {title}
        </InputLabel>
      )}
      {children}
    </>
  );

  return oneLine ? (
    <div className={classNames(classesLayout.flex, classesLayout.alignCenter)}>
      {content}
    </div>
  ) : (
    content
  );
};

export const Switch: React.FC<SwitchProps> = ({
  label,
  field,
  title,
  secondary,
  oneLine,
}) => {
  const classesForm = useFormStyles();
  return (
    <SwitchWrapper field={field} title={title} oneLine={oneLine}>
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
                    colorPrimary: classesForm.disabled,
                  }
                : {}
            }
          />
        }
        label={label}
      />
    </SwitchWrapper>
  );
};

export default Switch;
