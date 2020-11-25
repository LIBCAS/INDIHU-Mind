import React from "react";
import Typography from "@material-ui/core/Typography";
import Button from "@material-ui/core/Button";
import classNames from "classnames";
import { withRouter, RouteComponentProps } from "react-router-dom";

import { useStyles } from "./_cardStyles";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";

import { theme } from "../../theme/theme";
import Card from "@material-ui/core/Card";
import { CardActionArea } from "@material-ui/core";
import { LabelProps } from "../../types/label";
import { Label } from "./Label";

interface CardCreateAddCardItemProps {
  card: { id: string; name: string; rawNote: string; labels?: LabelProps[] };
  onSelect?: (card: any) => void;
  onRestore?: (card: any) => void;
  onRemove?: (card: any) => void;
  onRemoveText?: string;
  topMargin?: number;
  showLabels?: boolean;
}

const CardTileView: React.FC<
  CardCreateAddCardItemProps & RouteComponentProps
> = ({
  card,
  onSelect,
  onRestore,
  onRemove,
  onRemoveText,
  history,
  topMargin = 1.5,
  showLabels = false,
}) => {
  const classes = useStyles();
  const classesText = useTextStyles();

  const { rawNote, labels } = card;

  return (
    <Card
      className={classes.customScrollbar}
      style={{
        display: "flex",
        flexWrap: "wrap",
        marginTop: theme.spacing(topMargin),
      }}
      onClick={() => {
        if (onSelect) {
          onSelect(card);
        } else {
          history.push(`/card/${card.id}`);
        }
      }}
    >
      {" "}
      <CardActionArea>
        <div
          className={classNames(classes.cardLinked, {
            [classes.cardLinkedNote]: rawNote,
          })}
        >
          <Typography
            variant="h6"
            display="block"
            className={classNames(classesText.textBold, classes.cardTileTitle)}
          >
            {card.name}
          </Typography>
          {rawNote && (
            <Typography
              variant="body1"
              className={classNames(classesText.noWrap, classes.cardTileNote)}
            >
              {rawNote}
            </Typography>
          )}
          {showLabels && labels && (
            <div className={classes.cardTileLabelsContainer}>
              {labels.map((label) => (
                <Label key={label.id} label={label}></Label>
              ))}
            </div>
          )}
        </div>
        {onRestore && (
          <Button
            onClick={(e: React.MouseEvent<HTMLButtonElement>) => {
              e.stopPropagation();
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
            onClick={(e: React.MouseEvent<HTMLButtonElement>) => {
              e.stopPropagation();
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
      </CardActionArea>
    </Card>
  );
};

export const CardTile = withRouter(CardTileView);
