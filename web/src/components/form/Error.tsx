import React from "react";
import classNames from "classnames";
import Typography from "@material-ui/core/Typography";

import { useStyles as useFormStyles } from "./_styles";

export interface Props {
  show?: boolean;
  error?: string;
}

export const Error: React.FC<Props> = ({ error, show = true }) => {
  const classesForm = useFormStyles();

  return show && error ? (
    <Typography className={classNames(classesForm.error)} color="error">
      {error}
    </Typography>
  ) : (
    <></>
  );
};
