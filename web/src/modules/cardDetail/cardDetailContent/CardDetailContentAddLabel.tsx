import MuiIconButton from "@material-ui/core/IconButton";
import MuiTooltip from "@material-ui/core/Tooltip";
import MuiAddCircleIcon from "@material-ui/icons/AddCircle";
import React, { useRef, useState } from "react";
import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";
import { CardContentProps, CardProps } from "../../../types/card";
import { CardDetailContentAddLabelForm } from "./CardDetailContentAddLabelForm";

interface CardDetailContentAddLabelProps {
  card: CardProps;
  setCard: React.Dispatch<React.SetStateAction<CardProps | undefined>>;
  currentCardContent: CardContentProps;
  cardContents: CardContentProps[] | undefined;
  setCardContents: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
  disabled?: boolean;
}

export const CardDetailContentAddLabel: React.FC<CardDetailContentAddLabelProps> = ({
  card,
  setCard,
  currentCardContent,
  cardContents,
  setCardContents,
  disabled,
}) => {
  const classesSpacing = useSpacingStyles();

  const [openForm, setOpenForm] = useState(false);

  const anchorEl = useRef(null);

  const handleAddLabel = () => {
    setOpenForm((prev) => !prev);
  };

  return (
    <React.Fragment>
      <div className={classesSpacing.mt1} ref={anchorEl}>
        <MuiTooltip title="Přidat štítek" arrow={true}>
          <MuiIconButton
            style={{ marginTop: "8px" }}
            color="primary"
            aria-label="add-label"
            component="span"
            onClick={handleAddLabel}
            disabled={disabled}
          >
            <MuiAddCircleIcon fontSize="default" />
          </MuiIconButton>
        </MuiTooltip>
      </div>
      {openForm && (
        <CardDetailContentAddLabelForm
          currentCardContent={currentCardContent}
          card={card}
          setCard={setCard}
          setCardContents={setCardContents}
          openForm={openForm}
          setOpenForm={setOpenForm}
          anchorEl={anchorEl}
        />
      )}
    </React.Fragment>
  );
};
