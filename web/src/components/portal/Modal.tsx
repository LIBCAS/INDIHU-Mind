import React from "react";
import classNames from "classnames";
import MuiModal from "@material-ui/core/Modal";
import Paper from "@material-ui/core/Paper";
import { useWindowHeight } from "../../hooks/useWindowHeight";
import { ButtonCancel } from "../control/ButtonCancel";
import { useStyles } from "./ModalStyles";

interface ModalProps {
  open: boolean;
  setOpen: any;
  content: any;
  contentOutside?: any;
  overflowVisible?: boolean;
  // Optional Fn to handle if modal should close
  onClose?: () => boolean;
  fullSize?: boolean;
  disableEnforceFocus?: boolean;
  withPadding?: boolean;
}

export const Modal: React.FC<ModalProps> = ({
  open,
  setOpen,
  content,
  contentOutside,
  overflowVisible,
  onClose,
  fullSize = false,
  disableEnforceFocus = false,
  withPadding = false
}) => {
  const classes = useStyles();

  const windowHeight = useWindowHeight(85);

  const handleClose = () => {
    if (onClose) {
      const shouldClose = onClose();

      if (open !== shouldClose) {
        setOpen(!shouldClose);
      }
    } else {
      setOpen(false);
    }
  };

  return (
    <MuiModal
      style={{ ...(overflowVisible && { overflowY: "auto" }) }}
      open={open}
      onClose={handleClose}
      disableEnforceFocus={disableEnforceFocus}
    >
      <Paper className={classes.modal}>
        {contentOutside && contentOutside}
        {setOpen && <ButtonCancel onClick={handleClose} />}
        <div
          className={classNames(
            classes.modalContentWrapper,
            fullSize && classes.modalContentWrapperFull,
            withPadding && classes.modalWithPadding
          )}
          style={{
            maxHeight: fullSize ? undefined : `${windowHeight}px`,
            ...(overflowVisible && { overflowY: "visible" })
          }}
        >
          <div
            style={{
              ...(overflowVisible && {
                overflowY: "visible",
                overflowX: "visible"
              })
            }}
            className={classNames(
              classes.modalContent,
              fullSize && classes.modalContentFull
            )}
          >
            {content}
          </div>
        </div>
      </Paper>
    </MuiModal>
  );
};
