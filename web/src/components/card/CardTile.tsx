import React from "react";
import Typography from "@material-ui/core/Typography";
import Button from "@material-ui/core/Button";
import classNames from "classnames";
import { withRouter, RouteComponentProps } from "react-router-dom";

import { useStyles } from "./_cardStyles";

interface CardCreateAddCardItemProps {
  card: { id: string; name: string; note: string };
  text?: string | any;
  onSelect?: (card: any) => void;
  onRestore?: (card: any) => void;
  onRemove?: (card: any) => void;
  onRemoveText?: string;
}

const CardTileView: React.FC<CardCreateAddCardItemProps &
  RouteComponentProps> = ({
  card,
  onSelect,
  onRestore,
  onRemove,
  onRemoveText,
  text,
  history
}) => {
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
      <Button
        onClick={() => {
          if (onSelect) {
            onSelect(card);
          } else {
            history.push(`/card/${card.id}`);
          }
        }}
        className={classNames(classes.cardLinkedButton, {
          [classes.cardLinkedButtonLast]: onRemove === undefined
        })}
        fullWidth
      >
        {text ? text : "Zobrazit kartu"}
      </Button>
      {onRestore && (
        <Button
          onClick={() => {
            onRestore(card);
          }}
          className={classNames(classes.cardLinkedButton)}
          fullWidth
          color="primary"
        >
          Obnovit kartu
        </Button>
      )}
      {onRemove && (
        <Button
          onClick={() => {
            onRemove(card);
          }}
          className={classNames(
            classes.cardLinkedButton,
            classes.cardLinkedButtonLast
          )}
          fullWidth
          color="secondary"
        >
          {onRemoveText ? onRemoveText : "Odebrat kartu"}
        </Button>
      )}
    </div>
  );
};

export const CardTile = withRouter(CardTileView);
