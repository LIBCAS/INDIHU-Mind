import React, { useState } from "react";

import { Snackbar } from "../portal/Snackbar";

interface MessageSnackbarProps {
  setVisible: Function;
  message?: any;
}

export const MessageSnackbar: React.FC<MessageSnackbarProps> = ({
  message,
  setVisible
}) => {
  const [open, setOpen] = useState(true);
  const onClose = () => {
    setOpen(false);
    setVisible(false);
  };
  return (
    <Snackbar
      open={open}
      onClose={onClose}
      message={
        message ? message : "Někde se stala chyba. Zkuste to prosím znovu"
      }
    />
  );
};
