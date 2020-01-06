import React from "react";
import MenuItem from "@material-ui/core/MenuItem";
import { OptionProps } from "react-select/lib/components/Option";

import { OptionType } from "./_reactSelectTypes";

export function ReactSelectOption(props: OptionProps<OptionType>) {
  return (
    <MenuItem
      ref={props.innerRef}
      selected={props.isFocused}
      component="div"
      style={{
        fontWeight: props.isSelected ? 500 : 400,
        whiteSpace: "normal"
      }}
      {...props.innerProps}
    >
      {/* {props.children} */}
      <span dangerouslySetInnerHTML={{ __html: props.label as string }} />
    </MenuItem>
  );
}
