import React from "react";
import classNames from "classnames";

import { useStyles } from "./ButtonCancelStyles";

interface ButtonCancelProps {
  onClick(e: React.MouseEvent): void;
  variant?: "popover";
}

export const ButtonCancel: React.FC<ButtonCancelProps> = ({
  onClick,
  variant,
}) => {
  const classes = useStyles();
  return (
    <button
      className={classNames(classes.cancelWrapper, {
        [classes.variantPopover]: variant === "popover",
      })}
      onClick={onClick}
    >
      <div className={classes.cancelIcon}>&#10005;</div>
    </button>
  );
};
