import MuiIconButton from "@material-ui/core/IconButton";
import MuiTooltip from "@material-ui/core/Tooltip";
import MuiAddCircleIcon from "@material-ui/icons/AddCircle";
import React, { useRef, useState } from "react";
import { Popover } from "../../../components/portal/Popover";
import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";
import { CardContentProps, CardProps } from "../../../types/card";
import { CardDetailContentAddAttributeForm } from "./CardDetailContentAddAttributeForm";

interface CardDetailContentAddAttributeProps {
  card: CardProps;
  setCard: React.Dispatch<React.SetStateAction<CardProps | undefined>>;
  currentCardContent: CardContentProps;
  cardContents: CardContentProps[] | undefined;
  setCardContents: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
  disabled?: boolean;
}

export const CardDetailContentAddAttribute: React.FC<CardDetailContentAddAttributeProps> = ({
  card,
  setCard,
  currentCardContent,
  cardContents,
  setCardContents,
  disabled,
}) => {
  const classesSpacing = useSpacingStyles();

  const [popoverOpen, setPopoverOpen] = useState<boolean>(false);

  const AddAttributeRef = useRef(null);

  const handleAddAttribute = () => {
    setPopoverOpen((prev) => !prev);
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
          disabled={disabled}
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
            currentCardContent={currentCardContent}
            card={card}
            setCard={setCard}
            setCardContents={setCardContents}
          />
        }
      />
    </div>
  );
};
