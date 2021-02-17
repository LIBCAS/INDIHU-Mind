import { LinearProgress } from "@material-ui/core";
import MuiToolbar from "@material-ui/core/Toolbar";
import MuiTypography from "@material-ui/core/Typography";
import classNames from "classnames";
import { isEqual, uniqWith } from "lodash";
import React, { useCallback, useEffect, useState } from "react";
import { CardTile } from "../../components/card/CardTile";
import { Gallery } from "../../components/gallery";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { CardContentProps, LinkedCardProps } from "../../types/card";
import { CardDetailContentAddAttribute } from "./cardDetailContent/CardDetailContentAddAttribute";
import { CardDetailContentAddCategory } from "./cardDetailContent/CardDetailContentAddCategory";
import { CardDetailContentAddFile } from "./cardDetailContent/CardDetailContentAddFile";
import { CardDetailContentAddLabel } from "./cardDetailContent/CardDetailContentAddLabel";
import { CardDetailContentAddRecord } from "./cardDetailContent/CardDetailContentAddRecord";
import { CardDetailContentAttribute } from "./cardDetailContent/CardDetailContentAttribute";
import { CardDetailContentCard } from "./cardDetailContent/CardDetailContentCard";
import { CardDetailContentCategory } from "./cardDetailContent/CardDetailContentCategory";
import { CardDetailContentFile } from "./cardDetailContent/CardDetailContentFile";
import { CardDetailContentLabel } from "./cardDetailContent/CardDetailContentLabel";
import { CardDetailContentNote } from "./cardDetailContent/CardDetailContentNote";
import { CardDetailContentRecord } from "./cardDetailContent/CardDetailContentRecord";
import { CardDetailContentTitle } from "./cardDetailContent/CardDetailContentTitle";
import { onEditCard } from "./cardDetailContent/_utils";
import { useStyles } from "./_cardDetailStyles";
import { loadNote } from "./_utils";

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
  refreshCard,
}) => {
  const classes = useStyles();

  const classesSpacing = useSpacingStyles();

  const classesLayout = useLayoutStyles();

  const onSelect = (card: LinkedCardProps) => {
    history.push(`/card/${card.id}`);
  };

  const cardInner = card.card;

  const [structuredNote, setStructuredNote] = useState<null | {
    data: string;
    id: string;
    size: number;
  }>(null);
  const [loadingNote, setLoadingNote] = useState(false);

  const fetchNote = useCallback(async () => {
    setLoadingNote(true);
    const structuredNoteFetched = await loadNote(card.card.id);
    setStructuredNote(structuredNoteFetched);
    setLoadingNote(false);
  }, [card.card.id]);
  useEffect(() => {
    fetchNote();
  }, [fetchNote]);

  const connectedCards = uniqWith(
    [...cardInner.linkedCards, ...cardInner.linkingCards],
    isEqual
  );

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
        cardInner.categories.map((cat) => (
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
      {loadingNote && (
        <LinearProgress style={{ width: "100%" }} color="primary" />
      )}
      <CardDetailContentNote
        setStructuredNote={setStructuredNote}
        note={structuredNote ? structuredNote.data : null}
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
          cardInner.labels.map((label) => (
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
          cardInner.records.map((record) => (
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
          card.attributes.map((attribute, index) => {
            return (
              <CardDetailContentAttribute
                key={attribute.id}
                card={card}
                cardContent={cardContent}
                setCardContent={setCardContent}
                attribute={attribute}
                attributeIndex={index}
                attributes={card.attributes}
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
            refreshCard={refreshCard}
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
        {connectedCards.map((connectedCard) => (
          <CardTile
            key={connectedCard.id}
            card={connectedCard}
            onSelect={onSelect}
            onRemove={() => {
              const removedCards = cardInner.linkedCards.filter(
                (lc) => lc.id !== connectedCard.id
              );
              onEditCard("linkedCards", removedCards, card, setCardContent);
            }}
          />
        ))}
      </div>
    </React.Fragment>
  );
};
