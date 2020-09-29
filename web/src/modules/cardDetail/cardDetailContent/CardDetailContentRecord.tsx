import React from "react";
import { withRouter, RouteComponentProps } from "react-router-dom";
import Typography from "@material-ui/core/Typography";
import Delete from "@material-ui/icons/Delete";

import { onEditCard } from "./_utils";

import { Popconfirm } from "../../../components/portal/Popconfirm";
import { CardContentProps } from "../../../types/card";
import { useStyles } from "./_cardStyles";
import { RecordProps } from "../../../types/record";

interface CardDetailContentRecordViewProps {
  card: CardContentProps;
  cardContent: CardContentProps[] | undefined;
  setCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
  record: RecordProps;
}

const CardDetailContentRecordView: React.FC<CardDetailContentRecordViewProps &
  RouteComponentProps> = ({ card, setCardContent, record, history }) => {
  const classes = useStyles();

  const onClick = () => {
    history.push(`/record/${record.id}`);
  };

  const onDelete = () => {
    const records = card.card.records.filter(r => r.id !== record.id);

    onEditCard("records", records, card, setCardContent);
  };

  return (
    <div
      key={record.id}
      className={classes.label}
      style={{ margin: "4px", paddingRight: "7px" }}
    >
      <Typography onClick={onClick} className={classes.labelText}>
        {record.name}
      </Typography>
      {card.lastVersion && (
        <Popconfirm
          confirmText="Odebrat citaci?"
          onConfirmClick={onDelete}
          tooltip="Odebrat"
          acceptText="Odebrat"
          Button={<Delete />}
        />
      )}
    </div>
  );
};

export const CardDetailContentRecord = withRouter(CardDetailContentRecordView);
