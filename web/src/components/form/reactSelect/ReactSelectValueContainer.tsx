import React from "react";
import { ValueContainerProps } from "react-select/lib/components/containers";

import { OptionType } from "./_reactSelectTypes";

export function ReactSelectValueContainer(
  props: ValueContainerProps<OptionType>
) {
  return (
    <div className={props.selectProps.classes.valueContainer}>
      {props.children}
    </div>
  );
}
