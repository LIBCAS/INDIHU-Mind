import React, { ReactChild, ReactChildren } from "react";
import classNames from "classnames";
import InputLabel from "@material-ui/core/InputLabel";
import Typography from "@material-ui/core/Typography";

import { useStyles as useFormStyles } from "./_formStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";

interface FormFieldWrapperInnerProps {
  children: ReactChild | ReactChildren;
}

export interface FormFieldWrapperProps {
  form: any;
  field: any;
  oneLine?: boolean;
  label?: string | JSX.Element;
  labelClassName?: string;
}

type Props = FormFieldWrapperProps & FormFieldWrapperInnerProps;

export const FormFieldWrapper: React.FC<Props> = ({
  children,
  form,
  field,
  oneLine = false,
  label,
  labelClassName
}) => {
  const classesForm = useFormStyles();
  const classesLayout = useLayoutStyles();

  const touched = form.touched[field.name];
  const error = form.errors[field.name];

  const fieldElement = (
    <>
      {children}
      {touched && error ? (
        <Typography className={classNames(classesForm.error)} color="error">
          {error}
        </Typography>
      ) : (
        <></>
      )}
    </>
  );

  const fieldElementWithLabel = (
    <>
      {label && (
        <InputLabel
          className={classNames(
            classesForm.label,
            oneLine && classesForm.oneLineFieldLabel,
            labelClassName
          )}
          htmlFor={field.name}
        >
          {label}
        </InputLabel>
      )}
      {oneLine ? (
        <div className={classesForm.oneLineField}>{fieldElement}</div>
      ) : (
        fieldElement
      )}
    </>
  );

  return oneLine ? (
    <div
      className={classNames(
        classesLayout.flex,
        classesLayout.alignCenter,
        classesLayout.fullWidth
      )}
    >
      {fieldElementWithLabel}
    </div>
  ) : (
    fieldElementWithLabel
  );
};
