import React from "react";
import ReactDOM from "react-dom";
import MaterialSnackbar from "@material-ui/core/Snackbar";
import IconButton from "@material-ui/core/IconButton";
import CloseIcon from "@material-ui/icons/Close";

import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";

import { useStyles } from "./SnackbarStyles";

interface SnackbarProps {
  open: boolean;
  onClose: any;
  message: any;
}

export const Snackbar: React.FC<SnackbarProps> = ({
  open,
  onClose,
  message
}) => {
  const classes = useStyles();
  const classesSpacing = useSpacingStyles();
  return ReactDOM.createPortal(
    <MaterialSnackbar
      anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
      open={open}
      autoHideDuration={6000}
      onClose={() => onClose()}
      className={classes.root}
      ContentProps={{
        "aria-describedby": "message-id"
      }}
      message={<div id="message-id">{message}</div>}
      action={
        <IconButton
          color="secondary"
          className={classesSpacing.p1}
          onClick={() => onClose()}
        >
          <CloseIcon />
        </IconButton>
      }
    />,
    document.getElementById("root") as HTMLElement
  );
};
