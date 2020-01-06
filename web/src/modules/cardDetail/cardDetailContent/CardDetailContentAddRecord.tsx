import React, { useState, useRef } from "react";
import Add from "@material-ui/icons/Add";
import { CardContentProps } from "../../../types/card";
import { ButtonGrey } from "../../../components/control/ButtonGrey";

import { CardDetailContentAddRecordForm } from "./CardDetailContentAddRecordForm";

// import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";

interface CardDetailContentAddRecordProps {
  card: CardContentProps;
  cardContent: CardContentProps[] | undefined;
  setCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
}

export const CardDetailContentAddRecord: React.FC<
  CardDetailContentAddRecordProps
> = ({ card, cardContent, setCardContent }) => {
  // const classesSpacing = useSpacingStyles();
  const [openForm, setOpenForm] = useState(false);
  const anchorEl = useRef(null);
  return (
    <>
      <div ref={anchorEl}>
        <ButtonGrey
          text="Přidat citaci"
          onClick={() => setOpenForm(prev => !prev)}
          bold
          inline
          Icon={<Add fontSize="small" />}
        />
      </div>
      {openForm && (
        <CardDetailContentAddRecordForm
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
