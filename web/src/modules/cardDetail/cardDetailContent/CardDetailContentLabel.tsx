import React, { useContext } from "react";
import { withRouter, RouteComponentProps } from "react-router-dom";

import { onEditCard } from "./_utils";

import { CardContentProps } from "../../../types/card";
import { GlobalContext } from "../../../context/Context";
import { labelActiveSet } from "../../../context/actions/label";
import { LabelProps } from "../../../types/label";
import { Label } from "../../../components/card/Label";

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

  const handleLabelClick = () => {
    labelActiveSet(dispatch, label);
    history.push("/cards");
  };

  const handleDelete = () => {
    const labels = card.card.labels.filter((lab) => lab.id !== label.id);
    onEditCard("labels", labels, card, setCardContent);
  };

  return (
    <React.Fragment>
      <Label
        label={label}
        onClick={handleLabelClick}
        onDelete={card.lastVersion ? handleDelete : undefined}
      />
    </React.Fragment>
  );
};

export const CardDetailContentLabel = withRouter(CardDetailContentLabelView);
