import React from "react";
import MaterialPopover from "@material-ui/core/Popover";
import Paper from "@material-ui/core/Paper";
import classNames from "classnames";
import ReactResizeDetector from "react-resize-detector";

import { ButtonCancel } from "../control/ButtonCancel";

import { useStyles } from "./PopoverStyles";

interface PopoverProps {
  open: boolean;
  setOpen: Function;
  anchorEl: any;
  content: any;
  autoWidth?: boolean;
  cancelButton?: boolean;
  width?: number;
  overflowVisible?: boolean;
  anchorReference?: "anchorEl" | "none" | "anchorPosition";
}

export const Popover: React.FC<PopoverProps> = ({
  open,
  setOpen,
  anchorEl,
  content,
  autoWidth,
  cancelButton,
  width,
  overflowVisible,
  anchorReference = "anchorEl"
}) => {
  const classes = useStyles();
  const onResize = () => {
    window.dispatchEvent(new CustomEvent("resize"));
  };
  return (
    <MaterialPopover
      id="simple-popper"
      open={open}
      anchorEl={anchorEl}
      onClose={() => setOpen(false)}
      anchorReference={anchorReference}
      anchorPosition={{
        top: window.innerHeight / 2 - 16,
        left: window.innerWidth / 2
      }}
      // classes={cancelButton ? { paper: classes.popoverPaper } : {}}
      classes={{
        paper: classNames({
          [classes.popoverPaper]: cancelButton,
          [classes.overflowVisible]: overflowVisible,
          root: classNames(classes.root) // TEST
        })
      }}
      style={{
        overflow: "auto"
      }}
      // Throws error, added root: classNames(classes.root) in classes props instead, ModalClasses={{ root: classNames(classes.root) }}
      anchorOrigin={{
        vertical: "top",
        horizontal: "center"
      }}
      transformOrigin={{
        vertical: "bottom",
        horizontal: "center"
      }}
    >
      <Paper
        className={classNames(classes.paper, {
          [classes.autoWidth]: autoWidth
        })}
        style={width ? { width } : {}}
      >
        {cancelButton && (
          <ButtonCancel variant="popover" onClick={() => setOpen(false)} />
        )}
        {content}
        <ReactResizeDetector handleWidth handleHeight onResize={onResize} />
      </Paper>
    </MaterialPopover>
  );
};
