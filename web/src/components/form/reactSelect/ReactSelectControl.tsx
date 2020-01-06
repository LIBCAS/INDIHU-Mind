import React from "react";
import { ControlProps } from "react-select/lib/components/Control";
import TextField from "@material-ui/core/TextField";
import InputLabel from "@material-ui/core/InputLabel";

import { ReactSelectInput } from "./ReactSelectInput";
import { OptionType } from "./_reactSelectTypes";

import { useStyles } from "../_formStyles";

export function ReactSelectControl(props: ControlProps<OptionType>) {
  const classesForm = useStyles();
  return (
    <>
      <InputLabel className={classesForm.label}>
        {props.selectProps.TextFieldProps.label}
      </InputLabel>
      <TextField
        fullWidth
        inputRef={props.innerRef}
        InputProps={{
          autoComplete: "off",
          disableUnderline: true,
          inputComponent: ReactSelectInput,
          inputProps: {
            className: props.selectProps.classes.input,
            // inputref: ,
            children: props.children,
            ...props.innerProps
          }
        }}
        {...props.selectProps.TextFieldProps}
        label=""
      />
    </>
  );
}
