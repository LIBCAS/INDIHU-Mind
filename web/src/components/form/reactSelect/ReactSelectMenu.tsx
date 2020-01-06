import React from "react";
import { MenuProps } from "react-select/lib/components/Menu";
import Paper from "@material-ui/core/Paper";

import { OptionType } from "./_reactSelectTypes";

export function ReactSelectMenu(props: MenuProps<OptionType>) {
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
