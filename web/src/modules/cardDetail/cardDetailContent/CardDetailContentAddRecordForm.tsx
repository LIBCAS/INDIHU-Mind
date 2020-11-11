import React from "react";

import { CardContentProps } from "../../../types/card";
import { onEditCard } from "./_utils";

import { AddRecordPopper } from "../../../components/record/AddRecordPopper";
import { RecordProps } from "../../../types/record";

interface CardDetailContentAddRecordFormProps {
  card: CardContentProps;
  cardContent: CardContentProps[] | undefined;
  setCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
  openForm: any;
  setOpenForm: any;
  anchorEl: any;
}

export const CardDetailContentAddRecordForm: React.FC<CardDetailContentAddRecordFormProps> = ({
  card,
  setCardContent,
  openForm,
  setOpenForm,
  anchorEl,
}) => {
  const onSubmit = async (values: RecordProps[]) => {
    onEditCard(
      "records",
      [...card.card.records, ...values],
      card,
      setCardContent
    );
    setOpenForm(false);
  };

  return (
    <AddRecordPopper
      anchorEl={anchorEl.current}
      open={openForm}
      setOpen={setOpenForm}
      // options={options}
      onSubmit={onSubmit}
    />
  );
};
