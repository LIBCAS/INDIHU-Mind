import React, { useState, useRef } from "react";
import { CardContentProps } from "../../../types/card";
import MuiTooltip from "@material-ui/core/Tooltip";
import MuiIconButton from "@material-ui/core/IconButton";
import MuiAddCircleIcon from "@material-ui/icons/AddCircle";

import { CardDetailContentAddRecordForm } from "./CardDetailContentAddRecordForm";

import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";

interface CardDetailContentAddRecordProps {
  card: CardContentProps;
  cardContent: CardContentProps[] | undefined;
  setCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
}

export const CardDetailContentAddRecord: React.FC<CardDetailContentAddRecordProps> = ({
  card,
  cardContent,
  setCardContent
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
          >
            <MuiAddCircleIcon fontSize="default" />
          </MuiIconButton>
        </MuiTooltip>
      </div>
      <CardDetailContentAddRecordForm
        card={card}
        cardContent={cardContent}
        setCardContent={setCardContent}
        openForm={openForm}
        setOpenForm={setOpenForm}
        anchorEl={anchorEl}
      />
    </React.Fragment>
  );
};
