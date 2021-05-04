import React, { useContext } from "react";
import { RouteComponentProps, withRouter } from "react-router-dom";
import { Label } from "../../../components/card/Label";
import { labelActiveSet } from "../../../context/actions/label";
import { GlobalContext } from "../../../context/Context";
import { CardContentProps, CardProps } from "../../../types/card";
import { LabelProps } from "../../../types/label";
import { onEditCard } from "./_utils";

interface CardDetailContentLabelViewProps {
  card: CardProps;
  setCard: React.Dispatch<React.SetStateAction<CardProps | undefined>>;
  currentCardContent: CardContentProps;
  setCardContents: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
  label: LabelProps;
  disabled?: boolean;
}

const CardDetailContentLabelView: React.FC<
  CardDetailContentLabelViewProps & RouteComponentProps
> = ({
  card,
  setCard,
  currentCardContent,
  setCardContents,
  label,
  history,
  disabled,
}) => {
  const context: any = useContext(GlobalContext);

  const dispatch: Function = context.dispatch;

  const handleLabelClick = () => {
    labelActiveSet(dispatch, label);
    history.push("/cards");
  };

  const handleDelete = () => {
    const labels = card.labels.filter((lab) => lab.id !== label.id);
    onEditCard(
      "labels",
      labels,
      card,
      setCard,
      currentCardContent,
      setCardContents
    );
  };

  return (
    <React.Fragment>
      <Label
        label={label}
        onClick={handleLabelClick}
        onDelete={
          !disabled && currentCardContent.lastVersion ? handleDelete : undefined
        }
      />
    </React.Fragment>
  );
};

export const CardDetailContentLabel = withRouter(CardDetailContentLabelView);
