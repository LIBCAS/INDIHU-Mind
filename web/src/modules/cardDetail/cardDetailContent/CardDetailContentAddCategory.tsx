import React, { useState, useRef } from "react";
import MuiAddCircleIcon from "@material-ui/icons/AddCircle";
import MuiIconButton from "@material-ui/core/IconButton";
import MuiTooltip from "@material-ui/core/Tooltip";
import { CardContentProps } from "../../../types/card";

import { CardDetailContentAddCategoryForm } from "./CardDetailContentAddCategoryForm";

import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";

interface CardDetailContentAddCategoryProps {
  card: CardContentProps;
  cardContent: CardContentProps[] | undefined;
  setCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
  refreshCard: () => void;
}

export const CardDetailContentAddCategory: React.FC<CardDetailContentAddCategoryProps> = ({
  card,
  cardContent,
  setCardContent,
  refreshCard
}) => {
  const classesSpacing = useSpacingStyles();

  const anchorEl = useRef(null);

  const [openForm, setOpenForm] = useState(false);

  const handleAddCategory = () => {
    setOpenForm(prev => !prev);
  };

  return (
    <React.Fragment>
      <div className={classesSpacing.mt1} ref={anchorEl}>
        <MuiTooltip title="Přidat kategorii" arrow={true}>
          <MuiIconButton
            style={{ marginTop: "8px" }}
            color="primary"
            aria-label="add-category"
            component="span"
            onClick={handleAddCategory}
          >
            <MuiAddCircleIcon fontSize="default" />
          </MuiIconButton>
        </MuiTooltip>
      </div>
      {openForm && (
        <CardDetailContentAddCategoryForm
          card={card}
          cardContent={cardContent}
          setCardContent={setCardContent}
          openForm={openForm}
          setOpenForm={setOpenForm}
          anchorEl={anchorEl}
          refreshCard={refreshCard}
        />
      )}
    </React.Fragment>
  );
};
