import React from "react";
import Typography from "@material-ui/core/Typography";
import { NoticeProps } from "react-select/lib/components/Menu";

import { OptionType } from "./_reactSelectTypes";

export function ReactSelectNoOptions(props: NoticeProps<OptionType>) {
  return (
    <Typography
      color="textSecondary"
      className={props.selectProps.classes.noOptionsMessage}
      {...props.innerProps}
    >
      {props.children}
    </Typography>
  );
}
