import React from "react";
import { ControlProps } from "react-select";
import TextField from "@material-ui/core/TextField";
import InputLabel from "@material-ui/core/InputLabel";

import { SelectInput } from "./SelectInput";
import { OptionType } from "./_types";

import { useStyles } from "../form/_styles";

export function SelectControl(props: ControlProps<OptionType>) {
  const classesForm = useStyles();
  const label = props.selectProps.TextFieldProps.label;
  return (
    <>
      {label && <InputLabel className={classesForm.label}>{label}</InputLabel>}
      <TextField
        fullWidth
        inputRef={props.innerRef}
        InputProps={{
          autoComplete: "off",
          disableUnderline: true,
          inputComponent: SelectInput,
          inputProps: {
            className: props.selectProps.classes.input,
            // inputref: ,
            children: props.children,
            ...props.innerProps,
          },
        }}
        {...props.selectProps.TextFieldProps}
        label=""
      />
    </>
  );
}
