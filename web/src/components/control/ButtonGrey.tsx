import React from "react";
import Button from "@material-ui/core/Button";
import classNames from "classnames";

import { useStyles } from "./ButtonGreyStyles";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";

interface ButtonAddProps {
  text: string;
  onClick: any;
  Icon?: any;
  bold?: boolean;
  big?: boolean;
  inline?: boolean;
}

export const ButtonGrey: React.FC<ButtonAddProps> = ({
  text,
  onClick,
  Icon,
  bold,
  big,
  inline
}) => {
  const classes = useStyles();
  const classesText = useTextStyles();
  return (
    <Button
      fullWidth={inline ? false : true}
      color="primary"
      onClick={onClick}
      classes={{
        root: classNames(classes.button),
        label: classNames({ [classesText.textBold]: bold, [classes.big]: big })
      }}
    >
      {Icon}
      {text}
    </Button>
  );
};
