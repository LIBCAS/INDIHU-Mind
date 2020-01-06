import React, { HTMLAttributes } from "react";
import { BaseTextFieldProps } from "@material-ui/core/TextField";

type InputComponentProps = Pick<BaseTextFieldProps, "inputRef"> &
  HTMLAttributes<HTMLDivElement>;

export function ReactSelectInput({ inputRef, ...props }: InputComponentProps) {
  return <div ref={inputRef} {...props} />;
}
