import React, { useState, useRef } from "react";
import Add from "@material-ui/icons/Add";
import { CardContentProps } from "../../../types/card";
import { ButtonGrey } from "../../../components/control/ButtonGrey";

import { CardDetailContentAddCategoryForm } from "./CardDetailContentAddCategoryForm";

import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";

interface CardDetailContentAddCategoryProps {
  card: CardContentProps;
  cardContent: CardContentProps[] | undefined;
  setCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
}

export const CardDetailContentAddCategory: React.FC<
  CardDetailContentAddCategoryProps
> = ({ card, cardContent, setCardContent }) => {
  const classesSpacing = useSpacingStyles();
  const anchorEl = useRef(null);
  const [openForm, setOpenForm] = useState(false);
  return (
    <>
      <div className={classesSpacing.mt1} ref={anchorEl}>
        <ButtonGrey
          text="Přidat kategorii"
          onClick={() => setOpenForm(prev => !prev)}
          bold
          inline
          Icon={<Add fontSize="small" />}
        />
      </div>
      {openForm && (
        <CardDetailContentAddCategoryForm
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
