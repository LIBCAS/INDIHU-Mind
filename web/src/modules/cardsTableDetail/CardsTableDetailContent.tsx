import React, { useState } from "react";
import Typography from "@material-ui/core/Typography";
import classNames from "classnames";
import { withRouter, RouteComponentProps } from "react-router-dom";

import { FileItem } from "../../components/file/FileItem";
import { CardProps } from "../../types/card";

import { Gallery } from "../../components/gallery";
import { Record } from "../../components/card/Record";
import { Label } from "../../components/card/Label";
import { Category } from "../../components/card/Category";
import { CardTile } from "../../components/card/CardTile";
import { CardCreateRoot } from "../cardCreate/CardCreateRoot";

import { useStyles } from "./_cardsTableDetailStyles";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";

import { parseAttribute } from "../../utils/card";

interface CardsTableDetailContentProps {
  card: CardProps;
}

const CardsTableDetailContentView: React.FC<
  CardsTableDetailContentProps & RouteComponentProps
> = ({ card }) => {
  const classes = useStyles();

  const classesText = useTextStyles();

  const classesSpacing = useSpacingStyles();

  const classesLayout = useLayoutStyles();

  const [open, setOpen] = useState(false);

  return (
    <div style={{ position: "relative" }}>
      <CardCreateRoot open={open} setOpen={setOpen} item={card} edit />
      <Gallery items={card.documents} />
      {card.categories.length > 0 && (
        <div className={classesSpacing.mt2}>
          <Typography className={classNames(classesText.subtitle)}>
            kategorie
          </Typography>
          {card.categories.map((cat) => (
            <Category key={cat.id} category={cat} />
          ))}
        </div>
      )}
      {card.rawNote && (
        <div className={classesSpacing.mt2}>
          <Typography className={classNames(classesText.subtitle)}>
            popis
          </Typography>
          <Typography variant="body1" className={classesSpacing.mt2}>
            {card.rawNote}
          </Typography>
        </div>
      )}
      {card.labels.length > 0 && (
        <div className={classesSpacing.mt2}>
          <Typography className={classesText.subtitle}>štítky</Typography>
          <div
            className={classNames(classesLayout.flex, classesLayout.flexWrap)}
          >
            {card.labels.map((label) => (
              <Label key={label.id} label={label} />
            ))}
          </div>
        </div>
      )}
      {card.records.length > 0 && (
        <div className={classesSpacing.mt2}>
          <Typography className={classesText.subtitle}>Citace</Typography>
          <div
            className={classNames(classesLayout.flex, classesLayout.flexWrap)}
          >
            {card.records.map((record) => (
              <Record key={record.id} record={record} />
            ))}
          </div>
        </div>
      )}
      {card.attributes.length > 0 && (
        <div className={classesSpacing.mt2}>
          <Typography className={classesText.subtitle}>Atributy</Typography>
          <div
            className={classNames(classesLayout.flex, classesLayout.flexWrap)}
          >
            {card.attributes.map((attribute) => {
              return (
                <div
                  key={attribute.id}
                  className={classNames(
                    classesLayout.flex,
                    classesLayout.directionColumn,
                    classesSpacing.p1,
                    classes.attributeItem
                  )}
                >
                  <Typography className={classes.contentAttributeTitle}>
                    {attribute.name}
                  </Typography>
                  <Typography>{parseAttribute(attribute)}</Typography>
                </div>
              );
            })}
          </div>
        </div>
      )}
      {card.documents && card.documents.length > 0 && (
        <div className={classesSpacing.mt2}>
          <Typography className={classesText.subtitle}>Soubory</Typography>
          <div className={classes.columnsWrapper}>
            {card.documents.map((f) => (
              <FileItem key={f.id} file={f} />
            ))}
          </div>
        </div>
      )}
      {(card.linkedCards.length > 0 || card.linkingCards.length > 0) && (
        <div className={classesSpacing.mt2}>
          <Typography className={classesText.subtitle}>
            Propojené karty
          </Typography>
          <div className={classes.columnsWrapper}>
            {card.linkedCards.map((card) => (
              <CardTile key={card.id} card={card} />
            ))}
            {card.linkingCards.map((card) => (
              <CardTile key={card.id} card={card} />
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export const CardsTableDetailContent = withRouter(CardsTableDetailContentView);
