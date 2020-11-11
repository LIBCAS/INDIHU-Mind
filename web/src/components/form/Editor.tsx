import React from "react";

import {
  Editor as EditorComponent,
  EditorProps as EditorComponentProps,
} from "../editor";
import { FormFieldWrapperProps } from "./FormFieldWrapper";

interface EditorProps extends FormFieldWrapperProps, EditorComponentProps {}

export const Editor: React.FC<EditorProps> = ({ field, form, ...props }) => {
  const handleChange = (value?: string) =>
    form.setFieldValue(field.name, value);

  return <EditorComponent {...field} {...props} onChange={handleChange} />;
};
