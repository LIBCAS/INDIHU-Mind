import React from "react";
import Typography from "@material-ui/core/Typography";
import { PlaceholderProps } from "react-select/lib/components/Placeholder";

import { OptionType } from "./_types";

export function SelectPlaceholder(props: PlaceholderProps<OptionType>) {
  return (
    <Typography
      color="textSecondary"
      className={props.selectProps.classes.placeholder}
      {...props.innerProps}
    >
      {props.children}
    </Typography>
  );
}
