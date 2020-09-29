import React from "react";
import { MultiValueProps } from "react-select/lib/components/MultiValue";
import Chip from "@material-ui/core/Chip";
import CancelIcon from "@material-ui/icons/Cancel";
import classNames from "classnames";

import { OptionType } from "./_types";

export function SelectMultiValue(props: MultiValueProps<OptionType>) {
  return (
    <Chip
      tabIndex={-1}
      label={
        <span dangerouslySetInnerHTML={{ __html: props.children as string }} />
      }
      className={classNames(props.selectProps.classes.chip, {
        [props.selectProps.classes.chipFocused]: props.isFocused
      })}
      onDelete={props.removeProps.onClick}
      deleteIcon={<CancelIcon {...props.removeProps} />}
    />
  );
}
