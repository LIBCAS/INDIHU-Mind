import React from "react";
import Typography from "@material-ui/core/Typography";
import Button from "@material-ui/core/Button";
import classNames from "classnames";
import { withRouter, RouteComponentProps } from "react-router-dom";
import moment from "moment";

import { CardProps } from "../../types/card";

import { useStyles } from "./_cardsStyles";

interface CardCreateAddCardItemProps {
  card: CardProps;
  text?: string | any;
  onSelect?: (card: any) => void;
}

const CardTileItemView: React.FC<
  CardCreateAddCardItemProps & RouteComponentProps
> = ({ card, onSelect, text, history }) => {
  const classes = useStyles();
  const { note } = card;
  return (
    <div style={{ display: "flex", flexWrap: "wrap" }}>
      <div
        className={classNames(classes.cardLinked, {
          [classes.cardLinkedNote]: note
        })}
      >
        <Typography variant="h6" display="block" style={{ width: "100%" }}>
          {card.name}
        </Typography>
        {note && (
          <Typography variant="body1" display="inline">
            {note}
          </Typography>
        )}
      </div>
      <div className={classes.cardLinkedDate}>
        {moment(card.updated).format("DD. MM. YYYY")}
      </div>
      <Button
        onClick={() => {
          if (onSelect) {
            onSelect(card);
          } else {
            history.push(`/card/${card.id}`);
          }
        }}
        className={classNames(
          classes.cardLinkedButton,
          classes.cardLinkedButtonLast
        )}
        fullWidth
      >
        {text ? text : "Zobrazit kartu"}
      </Button>
    </div>
  );
};

export const CardTileItem = withRouter(CardTileItemView);
