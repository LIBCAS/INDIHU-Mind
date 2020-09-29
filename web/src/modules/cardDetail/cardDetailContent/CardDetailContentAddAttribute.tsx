import React, { useState, useRef } from "react";
import MuiTooltip from "@material-ui/core/Tooltip";
import MuiIconButton from "@material-ui/core/IconButton";
import MuiAddCircleIcon from "@material-ui/icons/AddCircle";

import { CardContentProps } from "../../../types/card";
import { Popover } from "../../../components/portal/Popover";

import { CardDetailContentAddAttributeForm } from "./CardDetailContentAddAttributeForm";
import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";

interface CardDetailContentAddAttributeProps {
  card: CardContentProps;
  cardContent: CardContentProps[] | undefined;
  setCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
}

export const CardDetailContentAddAttribute: React.FC<CardDetailContentAddAttributeProps> = ({
  card,
  cardContent,
  setCardContent
}) => {
  const classesSpacing = useSpacingStyles();

  const [popoverOpen, setPopoverOpen] = useState<boolean>(false);

  const AddAttributeRef = useRef(null);

  const handleAddAttribute = () => {
    setPopoverOpen(prev => !prev);
  };

  return (
    <div ref={AddAttributeRef} className={classesSpacing.mt1}>
      <MuiTooltip title="Přidat atribut" arrow={true}>
        <MuiIconButton
          style={{ marginTop: "8px" }}
          color="primary"
          aria-label="add-attribute"
          component="span"
          onClick={handleAddAttribute}
        >
          <MuiAddCircleIcon fontSize="default" />
        </MuiIconButton>
      </MuiTooltip>
      <Popover
        open={popoverOpen}
        setOpen={setPopoverOpen}
        anchorEl={AddAttributeRef.current}
        overflowVisible={true}
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
