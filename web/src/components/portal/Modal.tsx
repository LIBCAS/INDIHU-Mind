import React from "react";
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
}

export const Modal: React.FC<ModalProps> = ({
  open,
  setOpen,
  content,
  contentOutside,
  overflowVisible,
  onClose
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
    >
      <Paper className={classes.modal}>
        {contentOutside && contentOutside}
        {setOpen && <ButtonCancel onClick={handleClose} />}
        <div
          className={classes.modalContentWrapper}
          style={{
            maxHeight: `${windowHeight}px`,
            ...(overflowVisible && { overflowY: "visible" })
          }}
        >
          <div
            style={{ ...(overflowVisible && { overflowY: "visible" }) }}
            className={classes.modalContent}
          >
            {content}
          </div>
        </div>
      </Paper>
    </MuiModal>
  );
};
