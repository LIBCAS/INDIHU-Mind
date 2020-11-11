import React, { useState } from "react";

import { Modal } from "../../components/portal/Modal";
import { MessageSnackbar } from "../../components/messages/MessageSnackbar";

import { UserCreateForm } from "./UserCreateForm";

interface UserCreateModalProps {
  open: boolean;
  setOpen: React.Dispatch<React.SetStateAction<boolean>>;
}

export const UserCreateModal: React.FC<UserCreateModalProps> = ({
  open,
  setOpen,
}) => {
  const [userCreated, setUserCreated] = useState(false);
  return (
    <>
      {userCreated && (
        <MessageSnackbar
          setVisible={setUserCreated}
          message="Nový uživatel vytvořen"
        />
      )}
      <Modal
        open={open}
        setOpen={setOpen}
        content={
          <UserCreateForm setOpen={setOpen} setUserCreated={setUserCreated} />
        }
      />
    </>
  );
};
