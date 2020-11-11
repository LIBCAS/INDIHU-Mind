import React from "react";
import MenuItem from "@material-ui/core/MenuItem";
import { OptionProps } from "react-select";

import { OptionType } from "./_types";

export function SelectOption(props: OptionProps<OptionType>) {
  return (
    <MenuItem
      ref={props.innerRef}
      selected={props.isFocused}
      component="div"
      style={{
        fontWeight: props.isSelected ? 500 : 400,
        whiteSpace: "normal",
      }}
      {...props.innerProps}
    >
      {/* {props.children} */}
      <span dangerouslySetInnerHTML={{ __html: props.label as string }} />
    </MenuItem>
  );
}
