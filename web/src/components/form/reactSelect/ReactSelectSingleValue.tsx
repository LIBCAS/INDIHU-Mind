import React from "react";
import { SingleValueProps } from "react-select/lib/components/SingleValue";
import Typography from "@material-ui/core/Typography";

import { OptionType } from "./_reactSelectTypes";

export function ReactSelectSingleValue(props: SingleValueProps<OptionType>) {
  return (
    <Typography
      className={props.selectProps.classes.singleValue}
      {...props.innerProps}
    >
      {props.children}
    </Typography>
  );
}
