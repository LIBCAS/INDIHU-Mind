import React, { useRef, useState } from "react";
import Add from "@material-ui/icons/Add";
import { withRouter, RouteComponentProps } from "react-router-dom";

import { CardContentProps } from "../../../types/card";
import { Popover } from "../../../components/portal/Popover";
import { ButtonGrey } from "../../../components/control/ButtonGrey";
import { useStyles } from "./_cardStyles";
import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";
import { useStyles as useLayoutStyles } from "../../../theme/styles/layoutStyles";
import { onEditCard } from "./_utils";
import { CardsLink } from "../../cards/CardsLink";
import { Formik } from "../../../components/form/Formik";
import Button from "@material-ui/core/Button";
import { Form } from "formik";
import classNames from "classnames";

interface CardCreateAddCardProps {
  card: CardContentProps;
  setCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
}

const CardDetailContentCardView: React.FC<CardCreateAddCardProps &
  RouteComponentProps> = ({ card, setCardContent }) => {
  const classes = useStyles();
  const classesSpacing = useSpacingStyles();
  const classesLayout = useLayoutStyles();

  const AddCardRef = useRef(null);
  const [popoverOpen, setPopoverOpen] = useState(false);
  return (
    <div ref={AddCardRef} className={classes.linkedCardsAddButtonWrapper}>
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
          <Formik
            initialValues={{ linkedCards: card.card.linkedCards }}
            onSubmit={values => {
              const newLinkedCards = card.card.linkedCards.concat(
                values.linkedCards
              );
              onEditCard("linkedCards", newLinkedCards, card, setCardContent);
              setPopoverOpen(false);
            }}
            render={formikBag => (
              <Form
                className={classNames(
                  classesSpacing.p3,
                  classesLayout.flex,
                  classesLayout.directionColumn,
                  classes.addLinkedCardWrapper
                )}
              >
                <CardsLink
                  onSelect={(linkedCards: any[]) => {
                    formikBag.setFieldValue("linkedCards", linkedCards);
                  }}
                />
                <Button
                  variant="contained"
                  color="primary"
                  className={classesSpacing.mb1}
                  type="submit"
                >
                  Uložit propojené karty
                </Button>
              </Form>
            )}
          />
        }
      />
    </div>
  );
};

export const CardDetailContentCard = withRouter(CardDetailContentCardView);
