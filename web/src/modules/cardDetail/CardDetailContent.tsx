import React from "react";
import MuiTypography from "@material-ui/core/Typography";
import MuiToolbar from "@material-ui/core/Toolbar";
import classNames from "classnames";

import { CardContentProps, LinkedCardProps } from "../../types/card";

import { CardTile } from "../../components/card/CardTile";
import { Gallery } from "../../components/gallery";

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
  refreshCard: () => void;
}

export const CardDetailContent: React.FC<CardDetailContentProps> = ({
  card,
  cardContent,
  setCardContent,
  history,
  refreshCard
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
    <React.Fragment>
      <CardDetailContentTitle
        title={cardInner.name}
        card={card}
        cardContent={cardContent}
        setCardContent={setCardContent}
      />

      <Gallery className={classesSpacing.mt1} items={cardInner.documents} />

      <MuiToolbar disableGutters={true}>
        <MuiTypography style={{ marginTop: "15px" }} variant="h6">
          Kategorie
        </MuiTypography>
        {card.lastVersion && (
          <CardDetailContentAddCategory
            card={card}
            cardContent={cardContent}
            setCardContent={setCardContent}
            refreshCard={refreshCard}
          />
        )}
      </MuiToolbar>
      {(cardInner.categories.length &&
        cardInner.categories.length > 0 &&
        cardInner.categories.map(cat => (
          <CardDetailContentCategory
            key={cat.id}
            category={cat}
            card={card}
            cardContent={cardContent}
            setCardContent={setCardContent}
          />
        ))) || (
        <MuiTypography variant="subtitle2">
          Tato karta nemá kategorie
        </MuiTypography>
      )}

      <MuiToolbar disableGutters={true}>
        <MuiTypography style={{ marginTop: "15px" }} variant="h6">
          Popis
        </MuiTypography>
      </MuiToolbar>
      <CardDetailContentNote
        note={cardInner.note}
        card={card}
        cardContent={cardContent}
        setCardContent={setCardContent}
      />

      <MuiToolbar disableGutters={true}>
        <MuiTypography style={{ marginTop: "15px" }} variant="h6">
          Štítky
        </MuiTypography>
        {card.lastVersion && (
          <CardDetailContentAddLabel
            card={card}
            cardContent={cardContent}
            setCardContent={setCardContent}
          />
        )}
      </MuiToolbar>
      <div className={classNames(classesLayout.flex, classesLayout.flexWrap)}>
        {(cardInner.labels.length &&
          cardInner.labels.length > 0 &&
          cardInner.labels.map(label => (
            <CardDetailContentLabel
              key={label.id}
              label={label}
              card={card}
              cardContent={cardContent}
              setCardContent={setCardContent}
            />
          ))) || (
          <MuiTypography variant="subtitle2">
            Tato karta nemá štítky
          </MuiTypography>
        )}
      </div>

      <MuiToolbar disableGutters={true}>
        <MuiTypography style={{ marginTop: "15px" }} variant="h6">
          Citace
        </MuiTypography>
        {card.lastVersion && (
          <CardDetailContentAddRecord
            card={card}
            cardContent={cardContent}
            setCardContent={setCardContent}
          />
        )}
      </MuiToolbar>
      <div className={classNames(classesLayout.flex, classesLayout.flexWrap)}>
        {(cardInner.records.length &&
          cardInner.records.length > 0 &&
          cardInner.records.map(record => (
            <CardDetailContentRecord
              key={record.id}
              record={record}
              card={card}
              cardContent={cardContent}
              setCardContent={setCardContent}
            />
          ))) || (
          <MuiTypography variant="subtitle2">
            Tato karta nemá citace
          </MuiTypography>
        )}
      </div>

      <MuiToolbar disableGutters={true}>
        <MuiTypography style={{ marginTop: "15px" }} variant="h6">
          Atributy
        </MuiTypography>
        {card.lastVersion && (
          <CardDetailContentAddAttribute
            card={card}
            cardContent={cardContent}
            setCardContent={setCardContent}
          />
        )}
      </MuiToolbar>
      <div
        className={classNames(
          classesLayout.flex,
          classesLayout.directionColumn,
          classesSpacing.mt1
        )}
      >
        {(card.attributes &&
          card.attributes.length &&
          card.attributes.length > 0 &&
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
          })) || (
          <MuiTypography variant="subtitle2">
            Tato karta nemá atributy
          </MuiTypography>
        )}
      </div>

      <MuiToolbar disableGutters={true}>
        <MuiTypography style={{ marginTop: "15px" }} variant="h6">
          Soubory
        </MuiTypography>
        {card.lastVersion && (
          <CardDetailContentAddFile
            card={card}
            cardContent={cardContent}
            setCardContent={setCardContent}
          />
        )}
      </MuiToolbar>
      <div>
        <CardDetailContentFile
          card={card}
          cardContent={cardContent}
          setCardContent={setCardContent}
        />
      </div>

      <MuiToolbar disableGutters={true}>
        <MuiTypography style={{ marginTop: "15px" }} variant="h6">
          Propojené karty
        </MuiTypography>
      </MuiToolbar>

      <div className={classes.columnsWrapper}>
        {card.lastVersion && (
          <CardDetailContentCard card={card} setCardContent={setCardContent} />
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
    </React.Fragment>
  );
};
