import React, { useState } from "react";
import Button from "@material-ui/core/Button";

import { CardCreateRoot } from "./CardCreateRoot";

export const CardCreateButton: React.FC = () => {
  const [showModal, setShowModal] = useState<boolean>(false);

  return (
    <>
      <Button
        onClick={() => setShowModal(true)}
        variant="contained"
        color="primary"
        fullWidth
      >
        Nová karta
      </Button>
      <CardCreateRoot showModal={showModal} setShowModal={setShowModal} />
    </>
  );
};
