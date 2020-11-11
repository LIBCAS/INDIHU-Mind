import React, { useState } from "react";
import Button, { ButtonProps } from "@material-ui/core/Button";

import { Modal } from "./Modal";

interface ModalButtonProps extends ButtonProps {
  label: string;
  Content: any;
  modalProps?: any;
  contentProps?: any;
}

export const ModalButton: React.FC<ModalButtonProps> = ({
  label,
  Content,
  modalProps,
  contentProps,
  ...props
}) => {
  const [open, setOpen] = useState(false);

  return (
    <>
      <Modal
        {...{
          ...modalProps,
          open,
          setOpen,
          content: <Content {...contentProps} close={() => setOpen(false)} />,
        }}
      />
      <Button variant="contained" {...props} onClick={() => setOpen(true)}>
        {label}
      </Button>
    </>
  );
};
