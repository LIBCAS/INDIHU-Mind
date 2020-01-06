import React, { useState, useRef } from "react";
import Add from "@material-ui/icons/Add";

import { CardContentProps } from "../../../types/card";
import { ButtonGrey } from "../../../components/control/ButtonGrey";
import { Popover } from "../../../components/portal/Popover";

import { CardDetailContentAddAttributeForm } from "./CardDetailContentAddAttributeForm";
import { useStyles } from "./_cardStyles";

interface CardDetailContentAddAttributeProps {
  card: CardContentProps;
  cardContent: CardContentProps[] | undefined;
  setCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
}

export const CardDetailContentAddAttribute: React.FC<
  CardDetailContentAddAttributeProps
> = ({ card, cardContent, setCardContent }) => {
  const classes = useStyles();
  const [popoverOpen, setPopoverOpen] = useState<boolean>(false);
  const AddAttributeRef = useRef(null);
  return (
    <div ref={AddAttributeRef} className={classes.addWrapper}>
      <ButtonGrey
        text="Přidat atribut"
        onClick={() => setPopoverOpen(prev => !prev)}
        bold
        Icon={<Add fontSize="small" />}
      />
      <Popover
        open={popoverOpen}
        setOpen={setPopoverOpen}
        anchorEl={AddAttributeRef.current}
        content={
          <CardDetailContentAddAttributeForm
            setOpen={setPopoverOpen}
            card={card}
            cardContent={cardContent}
            setCardContent={setCardContent}
          />
        }
      />
    </div>
  );
};
