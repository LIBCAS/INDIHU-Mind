import React from "react";
import {
  Formik as FormikDefault,
  FormikProps as FormikDefaultProps,
  FormikHelpers,
} from "formik";

interface FormikProps {
  initialValues: any;
  onSubmit: (values: any, formikHelpers: FormikHelpers<any>) => void;
  render: ((props: FormikDefaultProps<any>) => React.ReactNode) | undefined;
  validationSchema?: any;
  enableReinitialize?: boolean | undefined;
  validateOnBlur?: boolean;
  validateOnChange?: boolean;
}

export const Formik: React.FC<FormikProps> = ({
  initialValues,
  validationSchema,
  onSubmit,
  render,
  enableReinitialize,
  validateOnBlur,
  validateOnChange,
}) => {
  return (
    <FormikDefault
      initialValues={initialValues}
      validationSchema={validationSchema}
      validateOnChange={validateOnChange}
      validateOnBlur={validateOnBlur}
      onSubmit={onSubmit}
      render={render}
      enableReinitialize={enableReinitialize}
    />
  );
};
