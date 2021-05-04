import Typography from "@material-ui/core/Typography";
import Delete from "@material-ui/icons/Delete";
import React from "react";
import { RouteComponentProps, withRouter } from "react-router-dom";
import { Popconfirm } from "../../../components/portal/Popconfirm";
import { CardContentProps, CardProps } from "../../../types/card";
import { RecordProps } from "../../../types/record";
import { useStyles } from "./_cardStyles";
import { onEditCard } from "./_utils";

interface CardDetailContentRecordViewProps {
  card: CardProps;
  setCard: React.Dispatch<React.SetStateAction<CardProps | undefined>>;
  currentCardContent: CardContentProps;
  setCardContents: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
  record: RecordProps;
  disabled?: boolean;
}

const CardDetailContentRecordView: React.FC<
  CardDetailContentRecordViewProps & RouteComponentProps
> = ({
  card,
  setCard,
  currentCardContent,
  setCardContents,
  record,
  history,
  disabled,
}) => {
  const classes = useStyles();

  const onClick = () => {
    history.push(`/record/${record.id}`);
  };

  const onDelete = () => {
    const records = card.records.filter((r) => r.id !== record.id);

    onEditCard(
      "records",
      records,
      card,
      setCard,
      currentCardContent,
      setCardContents
    );
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
      {!disabled && currentCardContent.lastVersion && (
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
