import { CardActionArea } from "@material-ui/core";
import Button from "@material-ui/core/Button";
import Card from "@material-ui/core/Card";
import Typography from "@material-ui/core/Typography";
import classNames from "classnames";
import React from "react";
import { RouteComponentProps, withRouter } from "react-router-dom";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { theme } from "../../theme/theme";
import { LabelProps } from "../../types/label";
import { Label } from "./Label";
import { useStyles } from "./_cardStyles";

interface CardCreateAddCardItemProps {
  card: {
    id: string;
    name: string;
    rawNote: string;
    labels?: LabelProps[];
    status?: "AVAILABLE" | "TRASHED";
  };
  onSelect?: (card: any) => void;
  onRestore?: (card: any) => void;
  onRemove?: (card: any) => void;
  onRemoveText?: string;
  topMargin?: number;
  showLabels?: boolean;
  disabled?: boolean;
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
  disabled,
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
          <div className={classes.cardTileTitle}>
            <Typography
              variant="h6"
              display="block"
              className={classNames(classesText.textBold)}
            >
              {card.name}
            </Typography>
            {card.status === "TRASHED" && (
              <span className={classes.cardTileTrashedTag}>V KOŠI</span>
            )}
          </div>
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
            disabled={disabled}
          >
            Obnovit kartu
          </Button>
        )}
        {onRemove && (
          <Button
            onClick={(e: React.MouseEvent<HTMLButtonElement>) => {
              if (!disabled) {
                e.stopPropagation();
                onRemove(card);
              }
            }}
            className={classNames(
              classes.cardLinkedButton,
              classes.cardLinkedButtonLast
            )}
            fullWidth
            color="secondary"
            style={{ cursor: disabled ? "not-allowed" : "pointer" }}
            disabled={disabled}
          >
            {onRemoveText ? onRemoveText : "Odebrat kartu"}
          </Button>
        )}
      </CardActionArea>
    </Card>
  );
};

export const CardTile = withRouter(CardTileView);
