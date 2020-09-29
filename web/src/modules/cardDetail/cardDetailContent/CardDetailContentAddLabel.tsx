import React, { useState, useRef } from "react";
import { CardContentProps } from "../../../types/card";
import MuiTooltip from "@material-ui/core/Tooltip";
import MuiAddCircleIcon from "@material-ui/icons/AddCircle";
import MuiIconButton from "@material-ui/core/IconButton";
import { CardDetailContentAddLabelForm } from "./CardDetailContentAddLabelForm";
import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";

interface CardDetailContentAddLabelProps {
  card: CardContentProps;
  cardContent: CardContentProps[] | undefined;
  setCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
}

export const CardDetailContentAddLabel: React.FC<CardDetailContentAddLabelProps> = ({
  card,
  cardContent,
  setCardContent
}) => {
  const classesSpacing = useSpacingStyles();

  const [openForm, setOpenForm] = useState(false);

  const anchorEl = useRef(null);

  const handleAddLabel = () => {
    setOpenForm(prev => !prev);
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
          >
            <MuiAddCircleIcon fontSize="default" />
          </MuiIconButton>
        </MuiTooltip>
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
    </React.Fragment>
  );
};
