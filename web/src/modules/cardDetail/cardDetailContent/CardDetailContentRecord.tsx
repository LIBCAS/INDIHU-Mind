import React, { useContext } from "react";
import { withRouter, RouteComponentProps } from "react-router-dom";
import Typography from "@material-ui/core/Typography";
import Cancel from "@material-ui/icons/Cancel";

import { onEditCard } from "./_utils";

import { Popconfirm } from "../../../components/portal/Popconfirm";
import { CardContentProps } from "../../../types/card";
import { GlobalContext } from "../../../context/Context";
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

const CardDetailContentRecordView: React.FC<
  CardDetailContentRecordViewProps & RouteComponentProps
> = ({ card, setCardContent, record, history }) => {
  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;
  const classes = useStyles();
  const onClick = () => {
    history.push(`/record/${record.id}`);
  };
  const onDelete = () => {
    const records = card.card.records.filter(r => r.id !== record.id);
    onEditCard("records", records, card, setCardContent);
  };
  return (
    <div key={record.id} className={classes.label}>
      <Typography onClick={onClick} className={classes.labelText}>
        {record.name}
      </Typography>
      {card.lastVersion && (
        <Popconfirm
          confirmText="Odebrat citaci?"
          onConfirmClick={onDelete}
          tooltip="Smazat"
          Button={() => <Cancel />}
        />
      )}
    </div>
  );
};

export const CardDetailContentRecord = withRouter(CardDetailContentRecordView);
