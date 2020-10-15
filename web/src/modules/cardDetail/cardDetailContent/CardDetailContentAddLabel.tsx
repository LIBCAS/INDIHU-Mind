import React, { useState, useRef } from "react";
import Add from "@material-ui/icons/Add";
import { CardContentProps } from "../../../types/card";
import { ButtonGrey } from "../../../components/control/ButtonGrey";

import { CardDetailContentAddLabelForm } from "./CardDetailContentAddLabelForm";

// import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";

interface CardDetailContentAddLabelProps {
  card: CardContentProps;
  cardContent: CardContentProps[] | undefined;
  setCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
}

export const CardDetailContentAddLabel: React.FC<
  CardDetailContentAddLabelProps
> = ({ card, cardContent, setCardContent }) => {
  // const classesSpacing = useSpacingStyles();
  const [openForm, setOpenForm] = useState(false);
  const anchorEl = useRef(null);
  return (
    <>
      <div ref={anchorEl}>
        <ButtonGrey
          text="Přidat štítek"
          onClick={() => setOpenForm(prev => !prev)}
          bold
          inline
          Icon={<Add fontSize="small" />}
        />
      </div>
      {openForm && (
        <CardDetailContentAddLabelForm
          card={card}
          cardContent={cardContent}
          setCardContent={setCardContent}
          openForm={openForm}
          setOpenForm={setOpenForm}
          anchorEl={anchorEl}
        />
      )}
    </>
  );
};
