import Button from "@material-ui/core/Button";
import classNames from "classnames";
import React, { CSSProperties } from "react";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { useStyles } from "./ButtonGreyStyles";

interface ButtonAddProps {
  text: string;
  onClick: any;
  Icon?: any;
  bold?: boolean;
  big?: boolean;
  inline?: boolean;
  style?: CSSProperties;
  disabled?: boolean;
}

export const ButtonGrey: React.FC<ButtonAddProps> = ({
  text,
  onClick,
  Icon,
  bold,
  big,
  inline,
  style,
  disabled,
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
        label: classNames({ [classesText.textBold]: bold, [classes.big]: big }),
      }}
      style={style}
      disabled={disabled}
    >
      {Icon}
      {text}
    </Button>
  );
};
