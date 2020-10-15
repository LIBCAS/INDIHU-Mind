import React, { useCallback, useMemo } from "react";

import { RecordsForm } from "./RecordsForm";
import { Modal } from "../../components/portal/Modal";

interface RecordsMenuProps {
  selectedRow: any;
  showModal: boolean;
  setShowModal: any;
  afterEdit: () => void;
}

export const RecordsMenu: React.FC<RecordsMenuProps> = ({
  selectedRow,
  showModal,
  setShowModal,
  afterEdit
}) => {
  const Content = useMemo(
    () => (
      <RecordsForm
        setShowModal={setShowModal}
        record={selectedRow}
        afterEdit={afterEdit}
      />
    ),
    [setShowModal, selectedRow, afterEdit]
  );
  return (
    <>
      <Modal open={showModal} setOpen={setShowModal} content={Content} />
    </>
  );
};
