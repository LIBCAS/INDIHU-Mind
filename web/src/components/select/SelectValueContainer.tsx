import React from "react";
import { ValueContainerProps } from "react-select";

import { OptionType } from "./_types";

export function SelectValueContainer(props: ValueContainerProps<OptionType>) {
  return (
    <div className={props.selectProps.classes.valueContainer}>
      {props.children}
    </div>
  );
}
