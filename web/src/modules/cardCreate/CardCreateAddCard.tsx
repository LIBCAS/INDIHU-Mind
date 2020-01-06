import React, { useRef, useState } from "react";
import Typography from "@material-ui/core/Typography";
import classNames from "classnames";
import Add from "@material-ui/icons/Add";
import { withRouter, RouteComponentProps } from "react-router-dom";

import { Popover } from "../../components/portal/Popover";
import { ButtonGrey } from "../../components/control/ButtonGrey";
import { useStyles as useStylesText } from "../../theme/styles/textStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles } from "./_cardCreateStyles";

import { CardCreateAddCardForm } from "./CardCreateAddCardForm";
import { CardTile } from "../../components/card/CardTile";

interface CardCreateAddCardProps {
  formikBag: any;
  setShowModal: Function;
}

const CardCreateAddCardView: React.FC<
  CardCreateAddCardProps & RouteComponentProps
> = ({ formikBag }) => {
  const classes = useStyles();
  const classesText = useStylesText();
  const classesSpacing = useSpacingStyles();
  const AddCardRef = useRef(null);
  const [popoverOpen, setPopoverOpen] = useState(false);

  const onSelect = (card: any) => {
    // history.push(`/card/${card.id}`);
    // setShowModal(false);
    window.open(`/card/${card.id}`, "_blank");
  };
  const onRemove = (card: any) => {
    const newLinkedCards = formikBag.values.linkedCards.filter(
      (c: any) => c.id !== card.id
    );
    formikBag.setFieldValue("linkedCards", newLinkedCards);
  };
  return (
    <>
      <Typography
        className={classNames(classesText.subtitle, classesSpacing.mt2)}
      >
        Propojené karty
      </Typography>
      <div className={classes.wrapperHalfItems}>
        <div ref={AddCardRef} style={{ display: "flex", marginTop: "12px" }}>
          <ButtonGrey
            text="Přidat kartu"
            onClick={() => setPopoverOpen(true)}
            bold
            big
            Icon={<Add fontSize="small" />}
          />
          <Popover
            open={popoverOpen}
            setOpen={setPopoverOpen}
            anchorEl={AddCardRef.current}
            cancelButton
            content={
              <CardCreateAddCardForm
                formikBagParent={formikBag}
                setOpen={setPopoverOpen}
              />
            }
          />
        </div>
        {formikBag.values.linkedCards.map((card: any) => (
          <CardTile
            key={card.id}
            card={card}
            onSelect={onSelect}
            onRemove={onRemove}
          />
        ))}
      </div>
    </>
  );
};

export const CardCreateAddCard = withRouter(CardCreateAddCardView);
