import Button from "@material-ui/core/Button";
import Add from "@material-ui/icons/Add";
import classNames from "classnames";
import { Form } from "formik";
import React, { useRef, useState } from "react";
import { RouteComponentProps, withRouter } from "react-router-dom";
import { ButtonGrey } from "../../../components/control/ButtonGrey";
import { Formik } from "../../../components/form/Formik";
import { Popover } from "../../../components/portal/Popover";
import { useStyles as useLayoutStyles } from "../../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";
import { CardContentProps, CardProps } from "../../../types/card";
import { CardsLink } from "../../cards/CardsLink";
import { useStyles } from "./_cardStyles";
import { onEditCard } from "./_utils";

interface CardCreateAddCardProps {
  card: CardProps;
  setCard: React.Dispatch<React.SetStateAction<CardProps | undefined>>;
  currentCardContent: CardContentProps;
  setCardContents: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
  disabled?: boolean;
}

const CardDetailContentCardView: React.FC<
  CardCreateAddCardProps & RouteComponentProps
> = ({ card, setCard, currentCardContent, setCardContents, disabled }) => {
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
        disabled={disabled}
        Icon={<Add fontSize="small" />}
      />
      <Popover
        open={popoverOpen}
        setOpen={setPopoverOpen}
        anchorEl={AddCardRef.current}
        cancelButton
        content={
          <Formik
            initialValues={{ linkedCards: card.linkedCards }}
            onSubmit={(values) => {
              const newLinkedCards = card.linkedCards.concat(
                values.linkedCards
              );
              onEditCard(
                "linkedCards",
                newLinkedCards,
                card,
                setCard,
                currentCardContent,
                setCardContents
              );
              setPopoverOpen(false);
            }}
            render={(formikBag) => (
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
                  excludedCards={[
                    card.id,
                    ...card.linkedCards.map(({ id }) => id),
                  ]}
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
