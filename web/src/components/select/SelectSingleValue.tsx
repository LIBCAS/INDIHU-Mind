import React from "react";
import { SingleValueProps } from "react-select";
import Typography from "@material-ui/core/Typography";

import { OptionType } from "./_types";

export function SelectSingleValue(props: SingleValueProps<OptionType>) {
  return (
    <Typography
      className={props.selectProps.classes.singleValue}
      {...props.innerProps}
    >
      {props.children}
    </Typography>
  );
}
