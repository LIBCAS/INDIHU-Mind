import React from "react";
import { MenuProps } from "react-select/lib/components/Menu";
import Paper from "@material-ui/core/Paper";

import { OptionType } from "./_types";

export function SelectMenu(props: MenuProps<OptionType>) {
  return (
    <Paper
      square
      className={props.selectProps.classes.paper}
      {...props.innerProps}
    >
      {props.children}
    </Paper>
  );
}
