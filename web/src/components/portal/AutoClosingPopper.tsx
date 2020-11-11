import React from "react";
import Paper from "@material-ui/core/Paper";
import classNames from "classnames";
import ReactResizeDetector from "react-resize-detector";
import Popper from "@material-ui/core/Popper";
import ClickAwayListener from "@material-ui/core/ClickAwayListener";

import { useStyles } from "./AutoClosingPopperStyles";
import Fade from "@material-ui/core/Fade";

interface PopperProps {
  open: boolean;
  setOpen: Function;
  anchorEl?: any;
  content: any;
  onClickAwayCallback: Function;
  position?: "bottom" | "top";
}

export const AutoClosingPopper: React.FC<PopperProps> = ({
  open,
  setOpen,
  anchorEl,
  content,
  onClickAwayCallback,
  position = "bottom",
}) => {
  const classes = useStyles();
  const onResize = () => {
    window.dispatchEvent(new CustomEvent("resize"));
  };
  const id = open ? "simple-popper" : undefined;

  const handleClickAway = () => {
    setOpen(false);
    onClickAwayCallback();
  };
  return (
    <Popper
      id={id}
      open={open}
      anchorEl={anchorEl}
      className={classes.autoClosingPopper}
      transition
      placement={position}
      modifiers={{
        preventOverflow: {
          enabled: true,
        },
        flip: {
          enabled: false,
        },
      }}
    >
      {({ TransitionProps }) => (
        <ClickAwayListener onClickAway={handleClickAway}>
          <Fade {...TransitionProps} timeout={350}>
            <Paper className={classNames(classes.paper)}>
              {content}
              <ReactResizeDetector
                handleWidth
                handleHeight
                onResize={onResize}
              />
            </Paper>
          </Fade>
        </ClickAwayListener>
      )}
    </Popper>
  );
};
