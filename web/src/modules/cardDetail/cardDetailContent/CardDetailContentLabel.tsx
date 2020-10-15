import React, { useContext } from "react";
import { withRouter, RouteComponentProps } from "react-router-dom";
import Typography from "@material-ui/core/Typography";
import Cancel from "@material-ui/icons/Cancel";

import { onEditCard } from "./_utils";

import { Popconfirm } from "../../../components/portal/Popconfirm";
import { CardContentProps } from "../../../types/card";
import { GlobalContext } from "../../../context/Context";
import { labelActiveSet } from "../../../context/actions/label";
import { LabelProps } from "../../../types/label";
import { useStyles } from "./_cardStyles";

interface CardDetailContentLabelViewProps {
  card: CardContentProps;
  cardContent: CardContentProps[] | undefined;
  setCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
  label: LabelProps;
}

const CardDetailContentLabelView: React.FC<
  CardDetailContentLabelViewProps & RouteComponentProps
> = ({ card, setCardContent, label, history }) => {
  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;
  const classes = useStyles();
  const onClick = () => {
    labelActiveSet(dispatch, label);
    history.push("/cards");
  };
  const onDelete = () => {
    const labels = card.card.labels.filter(lab => lab.id !== label.id);
    onEditCard("labels", labels, card, setCardContent);
  };
  return (
    <div key={label.id} className={classes.label}>
      <Typography onClick={onClick} className={classes.labelText}>
        {label.name}
      </Typography>
      <span
        onClick={onClick}
        className={classes.labelDot}
        style={{ background: label.color }}
      />
      {card.lastVersion && (
        <Popconfirm
          confirmText="Odebrat štítek?"
          onConfirmClick={onDelete}
          tooltip="Smazat"
          Button={() => <Cancel />}
        />
      )}
    </div>
  );
};

export const CardDetailContentLabel = withRouter(CardDetailContentLabelView);
