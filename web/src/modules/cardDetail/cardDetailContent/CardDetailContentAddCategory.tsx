import MuiIconButton from "@material-ui/core/IconButton";
import MuiTooltip from "@material-ui/core/Tooltip";
import MuiAddCircleIcon from "@material-ui/icons/AddCircle";
import React, { useRef, useState } from "react";
import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";
import { CardContentProps, CardProps } from "../../../types/card";
import { CardDetailContentAddCategoryForm } from "./CardDetailContentAddCategoryForm";

interface CardDetailContentAddCategoryProps {
  card: CardProps;
  setCard: React.Dispatch<React.SetStateAction<CardProps | undefined>>;
  currentCardContent: CardContentProps;
  cardContents: CardContentProps[] | undefined;
  setCardContents: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
  refreshCard: () => void;
  disabled?: boolean;
}

export const CardDetailContentAddCategory: React.FC<CardDetailContentAddCategoryProps> = ({
  card,
  setCard,
  currentCardContent,
  cardContents,
  setCardContents,
  refreshCard,
  disabled,
}) => {
  const classesSpacing = useSpacingStyles();

  const anchorEl = useRef(null);

  const [openForm, setOpenForm] = useState(false);

  const handleAddCategory = () => {
    setOpenForm((prev) => !prev);
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
            disabled={disabled}
          >
            <MuiAddCircleIcon fontSize="default" />
          </MuiIconButton>
        </MuiTooltip>
      </div>
      {openForm && (
        <CardDetailContentAddCategoryForm
          card={card}
          setCard={setCard}
          currentCardContent={currentCardContent}
          setCardContents={setCardContents}
          openForm={openForm}
          setOpenForm={setOpenForm}
          anchorEl={anchorEl}
          refreshCard={refreshCard}
        />
      )}
    </React.Fragment>
  );
};
