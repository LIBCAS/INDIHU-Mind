import React, { useRef, useState } from "react";
import Add from "@material-ui/icons/Add";
import { withRouter, RouteComponentProps } from "react-router-dom";

import { CardContentProps } from "../../../types/card";
import { Popover } from "../../../components/portal/Popover";
import { ButtonGrey } from "../../../components/control/ButtonGrey";
import { useStyles } from "./_cardStyles";

import { CardDetailContentCardForm } from "./CardDetailContentCardForm";

interface CardCreateAddCardProps {
  card: CardContentProps;
  cardContent: CardContentProps[] | undefined;
  setCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
}

const CardDetailContentCardView: React.FC<
  CardCreateAddCardProps & RouteComponentProps
> = ({ card, cardContent, setCardContent }) => {
  const classes = useStyles();
  const AddCardRef = useRef(null);
  const [popoverOpen, setPopoverOpen] = useState(false);
  return (
    <div ref={AddCardRef} className={classes.linkedCardsAddButtonWrapper}>
      <ButtonGrey
        text="Přidat kartu"
        onClick={() => setPopoverOpen(true)}
        bold
        big
        Icon={<Add fontSize="small" />}
      />
      <Popover
        open={popoverOpen}
        setOpen={setPopoverOpen}
        anchorEl={AddCardRef.current}
        cancelButton
        content={
          <CardDetailContentCardForm
            card={card}
            cardContent={cardContent}
            setCardContent={setCardContent}
            setOpen={setPopoverOpen}
          />
        }
      />
    </div>
  );
};

export const CardDetailContentCard = withRouter(CardDetailContentCardView);
