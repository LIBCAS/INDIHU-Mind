import React from "react";
import { Modal as MaterialModal } from "@material-ui/core";
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
}

export const Modal: React.FC<ModalProps> = ({
  open,
  setOpen,
  content,
  contentOutside,
  overflowVisible
}) => {
  const classes = useStyles();
  const windowHeight = useWindowHeight(85);
  return (
    <MaterialModal
      style={{ ...(overflowVisible && { overflowY: "auto" }) }}
      open={open}
      onClose={() => setOpen && setOpen(false)}
    >
      <Paper className={classes.modal}>
        {contentOutside && contentOutside}
        {setOpen && <ButtonCancel onClick={() => setOpen(false)} />}
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
    </MaterialModal>
  );
};
