import Typography from "@material-ui/core/Typography";
import React from "react";
import { RouteComponentProps, withRouter } from "react-router-dom";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useStylesText } from "../../theme/styles/textStyles";
import { CardsLink } from "../cards/CardsLink";

interface CardCreateAddCardProps {
  formikBag: any;
}

const CardCreateAddCardView: React.FC<
  CardCreateAddCardProps & RouteComponentProps
> = ({ formikBag }) => {
  const classesText = useStylesText();
  const classesSpacing = useSpacingStyles();

  return (
    <>
      <div className={classesSpacing.mt2} />
      <Typography className={classesText.subtitle}>Propojené karty</Typography>
      <CardsLink
        onSelect={(linkedCards: any[]) => {
          formikBag.setFieldValue("linkedCards", linkedCards);
        }}
        excludedCards={
          formikBag.values
            ? formikBag.values.linkedCards.map(({ id }: { id: string }) => id)
            : []
        }
      />
    </>
  );
};

export const CardCreateAddCard = withRouter(CardCreateAddCardView);
