import React from "react";
import classNames from "classnames";

import { useStyles } from "./DividerStyles";

interface DividerProps {
  className?: any;
}

export const Divider: React.FC<DividerProps> = ({ className }) => {
  const classes = useStyles();
  return <div className={classNames(classes.divider, className)} />;
};
