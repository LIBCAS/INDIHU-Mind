import React from "react";
import { AddRecordPopper } from "../../../components/record/AddRecordPopper";
import { CardContentProps, CardProps } from "../../../types/card";
import { RecordProps } from "../../../types/record";
import { onEditCard } from "./_utils";

interface CardDetailContentAddRecordFormProps {
  card: CardProps;
  setCard: React.Dispatch<React.SetStateAction<CardProps | undefined>>;
  currentCardContent: CardContentProps;
  setCardContents: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
  openForm: any;
  setOpenForm: any;
  anchorEl: any;
}

export const CardDetailContentAddRecordForm: React.FC<CardDetailContentAddRecordFormProps> = ({
  card,
  setCard,
  currentCardContent,
  setCardContents,
  openForm,
  setOpenForm,
  anchorEl,
}) => {
  const onSubmit = async (values: RecordProps[]) => {
    onEditCard(
      "records",
      [...card.records, ...values],
      card,
      setCard,
      currentCardContent,
      setCardContents
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
