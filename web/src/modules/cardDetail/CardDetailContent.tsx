import React from "react";
import Typography from "@material-ui/core/Typography";
import classNames from "classnames";

import { CardContentProps, LinkedCardProps } from "../../types/card";

import { CardTile } from "../../components/card/CardTile";

import { CardDetailContentAttribute } from "./cardDetailContent/CardDetailContentAttribute";
import { CardDetailContentAddAttribute } from "./cardDetailContent/CardDetailContentAddAttribute";
import { CardDetailContentAddFile } from "./cardDetailContent/CardDetailContentAddFile";
import { CardDetailContentCard } from "./cardDetailContent/CardDetailContentCard";
import { CardDetailContentAddLabel } from "./cardDetailContent/CardDetailContentAddLabel";
import { CardDetailContentLabel } from "./cardDetailContent/CardDetailContentLabel";
import { CardDetailContentAddCategory } from "./cardDetailContent/CardDetailContentAddCategory";
import { CardDetailContentTitle } from "./cardDetailContent/CardDetailContentTitle";
import { CardDetailContentNote } from "./cardDetailContent/CardDetailContentNote";
import { CardDetailContentCategory } from "./cardDetailContent/CardDetailContentCategory";
import { CardDetailContentFile } from "./cardDetailContent/CardDetailContentFile";
import { CardDetailContentRecord } from "./cardDetailContent/CardDetailContentRecord";
import { onEditCard } from "./cardDetailContent/_utils";

import { useStyles } from "./_cardDetailStyles";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { CardDetailContentAddRecord } from "./cardDetailContent/CardDetailContentAddRecord";

interface CardDetailContentProps {
  card: CardContentProps;
  cardContent: CardContentProps[] | undefined;
  history: any;
  setCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
}

export const CardDetailContent: React.FC<CardDetailContentProps> = ({
  card,
  cardContent,
  setCardContent,
  history
}) => {
  const classes = useStyles();

  const classesText = useTextStyles();

  const classesSpacing = useSpacingStyles();

  const classesLayout = useLayoutStyles();

  const onSelect = (card: LinkedCardProps) => {
    history.push(`/card/${card.id}`);
  };

  const cardInner = card.card;

  return (
    <>
      <CardDetailContentTitle
        title={cardInner.name}
        card={card}
        cardContent={cardContent}
        setCardContent={setCardContent}
      />
      {cardInner.categories.map(cat => (
        <CardDetailContentCategory
          key={cat.id}
          category={cat}
          card={card}
          cardContent={cardContent}
          setCardContent={setCardContent}
        />
      ))}
      {card.lastVersion && (
        <div className={classes.contentCategoryCreate}>
          <CardDetailContentAddCategory
            card={card}
            cardContent={cardContent}
            setCardContent={setCardContent}
          />
        </div>
      )}

      <CardDetailContentNote
        note={cardInner.note}
        card={card}
        cardContent={cardContent}
        setCardContent={setCardContent}
      />
      <Typography
        className={classNames(classesText.subtitle, classesSpacing.mt2)}
      >
        štítky
      </Typography>
      <div className={classNames(classesLayout.flex, classesLayout.flexWrap)}>
        {cardInner.labels.map(label => (
          <CardDetailContentLabel
            key={label.id}
            label={label}
            card={card}
            cardContent={cardContent}
            setCardContent={setCardContent}
          />
        ))}
        {card.lastVersion && (
          <CardDetailContentAddLabel
            card={card}
            cardContent={cardContent}
            setCardContent={setCardContent}
          />
        )}
      </div>
      <Typography
        className={classNames(classesText.subtitle, classesSpacing.mt2)}
      >
        Citace
      </Typography>
      <div className={classNames(classesLayout.flex, classesLayout.flexWrap)}>
        {cardInner.records.map(record => (
          <CardDetailContentRecord
            key={record.id}
            record={record}
            card={card}
            cardContent={cardContent}
            setCardContent={setCardContent}
          />
        ))}
        {card.lastVersion && (
          <CardDetailContentAddRecord
            card={card}
            cardContent={cardContent}
            setCardContent={setCardContent}
          />
        )}
      </div>
      <div
        className={classNames(
          classesLayout.flex,
          classesLayout.directionColumn,
          classesSpacing.mt1
        )}
      >
        {card.attributes &&
          card.attributes.map(attribute => {
            return (
              <CardDetailContentAttribute
                key={attribute.id}
                card={card}
                cardContent={cardContent}
                setCardContent={setCardContent}
                attribute={attribute}
              />
            );
          })}
      </div>
      {card.lastVersion && (
        <CardDetailContentAddAttribute
          card={card}
          cardContent={cardContent}
          setCardContent={setCardContent}
        />
      )}

      <Typography
        className={classNames(classesText.subtitle, classesSpacing.mt2)}
      >
        Soubory
      </Typography>
      <div>
        <CardDetailContentFile
          card={card}
          cardContent={cardContent}
          setCardContent={setCardContent}
        />
      </div>
      {card.lastVersion && (
        <CardDetailContentAddFile
          card={card}
          cardContent={cardContent}
          setCardContent={setCardContent}
        />
      )}
      <Typography
        className={classNames(classesText.subtitle, classesSpacing.mt1)}
      >
        Propojené karty
      </Typography>

      <div className={classes.columnsWrapper}>
        {card.lastVersion && (
          <CardDetailContentCard
            card={card}
            cardContent={cardContent}
            setCardContent={setCardContent}
          />
        )}
        {cardInner.linkedCards.map(linkedCard => (
          <CardTile
            key={linkedCard.id}
            card={linkedCard}
            onSelect={onSelect}
            onRemove={() => {
              const removedCards = cardInner.linkedCards.filter(
                lc => lc.id !== linkedCard.id
              );
              onEditCard("linkedCards", removedCards, card, setCardContent);
            }}
          />
        ))}
        {cardInner.linkingCards.map(linkingCard => (
          <CardTile
            key={linkingCard.id}
            card={linkingCard}
            onRemove={() => {
              const removedCards = cardInner.linkingCards.filter(
                lc => lc.id !== linkingCard.id
              );
              onEditCard("linkingCards", removedCards, card, setCardContent);
            }}
          />
        ))}
      </div>
    </>
  );
};
