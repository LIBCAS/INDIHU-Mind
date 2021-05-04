import MuiIconButton from "@material-ui/core/IconButton";
import MuiTooltip from "@material-ui/core/Tooltip";
import MuiAddCircleIcon from "@material-ui/icons/AddCircle";
import React, { useRef, useState } from "react";
import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";
import { CardContentProps, CardProps } from "../../../types/card";
import { CardDetailContentAddRecordForm } from "./CardDetailContentAddRecordForm";

interface CardDetailContentAddRecordProps {
  card: CardProps;
  setCard: React.Dispatch<React.SetStateAction<CardProps | undefined>>;
  currentCardContent: CardContentProps;
  setCardContents: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
  disabled?: boolean;
}

export const CardDetailContentAddRecord: React.FC<CardDetailContentAddRecordProps> = ({
  card,
  setCard,

  currentCardContent,

  setCardContents,
  disabled,
}) => {
  const classesSpacing = useSpacingStyles();

  const [openForm, setOpenForm] = useState(false);

  const anchorEl = useRef(null);

  const handleAddCitation = () => {
    setOpenForm(true);
  };

  return (
    <React.Fragment>
      <div className={classesSpacing.mt1} ref={anchorEl}>
        <MuiTooltip title="Přidat citaci" arrow={true}>
          <MuiIconButton
            style={{ marginTop: "8px" }}
            color="primary"
            aria-label="add-citation"
            component="span"
            onClick={handleAddCitation}
            disabled={disabled}
          >
            <MuiAddCircleIcon fontSize="default" />
          </MuiIconButton>
        </MuiTooltip>
      </div>
      <CardDetailContentAddRecordForm
        card={card}
        setCard={setCard}
        currentCardContent={currentCardContent}
        setCardContents={setCardContents}
        openForm={openForm}
        setOpenForm={setOpenForm}
        anchorEl={anchorEl}
      />
    </React.Fragment>
  );
};
