import React, { useState, useEffect, useContext } from "react";

import { GlobalContext, StateProps } from "../../../context/Context";

import { OptionType } from "../../../components/select/_types";
import { parseLabel } from "../../cardCreate/_utils";
import { CardContentProps } from "../../../types/card";
import { onEditCard } from "./_utils";

import { recordGet } from "../../../context/actions/record";
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
  anchorEl
}) => {
  const context: any = useContext(GlobalContext);
  const state: StateProps = context.state;
  const dispatch: Function = context.dispatch;
  const [options, setOptions] = useState<OptionType[]>([]);

  useEffect(() => {
    recordGet(dispatch);
  }, []);

  useEffect(() => {
    const flatten = state.record.records
      .filter(rec => !card.card.records.some(arrRec => arrRec.id === rec.id))
      .map(parseLabel);
    setOptions(flatten);
  }, [state.record.records]);

  const onSubmit = async (values: RecordProps[]) => {
    const allRecords: RecordProps[] = await recordGet(dispatch);
    const records = card.card.records.concat(values).map(parseLabel);
    // to transform option to normal record
    const resultTransformed = allRecords.filter(o =>
      records.some(c => c.value === o.id)
    );
    onEditCard("records", resultTransformed, card, setCardContent);
    setOpenForm(false);
  };
  return (
    <AddRecordPopper
      anchorEl={anchorEl.current}
      open={openForm}
      setOpen={setOpenForm}
      options={options}
      onSubmit={onSubmit}
    />
  );
};
