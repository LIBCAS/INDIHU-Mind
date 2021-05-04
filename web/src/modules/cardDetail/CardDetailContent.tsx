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
import { CardContentProps, CardProps, LinkedCardProps } from "../../types/card";
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
  card: CardProps;
  setCard: React.Dispatch<React.SetStateAction<CardProps | undefined>>;
  currentCardContent: CardContentProps;
  cardContents: CardContentProps[] | undefined;
  history: any;
  setCardContents: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
  refreshCard: () => void;
  isTrashed: boolean;
}

export const CardDetailContent: React.FC<CardDetailContentProps> = ({
  card,
  setCard,
  currentCardContent,
  cardContents,
  setCardContents,
  history,
  refreshCard,
  isTrashed,
}) => {
  const classes = useStyles();

  const classesSpacing = useSpacingStyles();

  const classesLayout = useLayoutStyles();

  const onSelect = (card: LinkedCardProps) => {
    history.push(`/card/${card.id}`);
  };

  const [structuredNote, setStructuredNote] = useState<null | {
    data: string;
    id: string;
    size: number;
  }>(null);
  const [loadingNote, setLoadingNote] = useState(false);

  const fetchNote = useCallback(async () => {
    setLoadingNote(true);
    const structuredNoteFetched = await loadNote(currentCardContent.card.id);
    setStructuredNote(structuredNoteFetched);
    setLoadingNote(false);
  }, [currentCardContent.card.id]);
  useEffect(() => {
    fetchNote();
  }, [fetchNote]);

  const connectedCards = uniqWith(
    [...card.linkedCards, ...card.linkingCards],
    isEqual
  );

  const isLatestVersion = currentCardContent.lastVersion;

  return (
    <React.Fragment>
      {isLatestVersion && (
        <>
          <CardDetailContentTitle
            card={card}
            setCard={setCard}
            title={card.name}
            currentCardContent={currentCardContent}
            cardContents={cardContents}
            setCardContents={setCardContents}
            disabled={isTrashed}
          />

          <Gallery className={classesSpacing.mt1} items={card.documents} />

          <MuiToolbar disableGutters={true}>
            <MuiTypography style={{ marginTop: "15px" }} variant="h6">
              Kategorie
            </MuiTypography>
            {currentCardContent.lastVersion && (
              <CardDetailContentAddCategory
                card={card}
                setCard={setCard}
                currentCardContent={currentCardContent}
                cardContents={cardContents}
                setCardContents={setCardContents}
                refreshCard={refreshCard}
                disabled={isTrashed}
              />
            )}
          </MuiToolbar>
          {(card.categories.length &&
            card.categories.length > 0 &&
            card.categories.map((cat) => (
              <CardDetailContentCategory
                card={card}
                setCard={setCard}
                key={cat.id}
                category={cat}
                currentCardContent={currentCardContent}
                setCardContents={setCardContents}
                disabled={isTrashed}
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
            card={card}
            setCard={setCard}
            setStructuredNote={setStructuredNote}
            note={structuredNote ? structuredNote.data : null}
            currentCardContent={currentCardContent}
            setCardContents={setCardContents}
            disabled={isTrashed}
          />
          <MuiToolbar disableGutters={true}>
            <MuiTypography style={{ marginTop: "15px" }} variant="h6">
              Štítky
            </MuiTypography>
            {currentCardContent.lastVersion && (
              <CardDetailContentAddLabel
                card={card}
                setCard={setCard}
                currentCardContent={currentCardContent}
                cardContents={cardContents}
                setCardContents={setCardContents}
                disabled={isTrashed}
              />
            )}
          </MuiToolbar>
          <div
            className={classNames(classesLayout.flex, classesLayout.flexWrap)}
          >
            {(card.labels.length &&
              card.labels.length > 0 &&
              card.labels.map((label) => (
                <CardDetailContentLabel
                  card={card}
                  setCard={setCard}
                  key={label.id}
                  label={label}
                  currentCardContent={currentCardContent}
                  setCardContents={setCardContents}
                  disabled={isTrashed}
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
            {currentCardContent.lastVersion && (
              <CardDetailContentAddRecord
                card={card}
                setCard={setCard}
                currentCardContent={currentCardContent}
                setCardContents={setCardContents}
                disabled={isTrashed}
              />
            )}
          </MuiToolbar>
          <div
            className={classNames(classesLayout.flex, classesLayout.flexWrap)}
          >
            {(card.records.length &&
              card.records.length > 0 &&
              card.records.map((record) => (
                <CardDetailContentRecord
                  card={card}
                  setCard={setCard}
                  key={record.id}
                  record={record}
                  currentCardContent={currentCardContent}
                  setCardContents={setCardContents}
                  disabled={isTrashed}
                />
              ))) || (
              <MuiTypography variant="subtitle2">
                Tato karta nemá citace
              </MuiTypography>
            )}
          </div>
        </>
      )}
      <MuiToolbar disableGutters={true}>
        <MuiTypography style={{ marginTop: "15px" }} variant="h6">
          Atributy
        </MuiTypography>
        {currentCardContent.lastVersion && (
          <CardDetailContentAddAttribute
            card={card}
            setCard={setCard}
            currentCardContent={currentCardContent}
            cardContents={cardContents}
            setCardContents={setCardContents}
            disabled={isTrashed}
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
        {(currentCardContent.attributes &&
          currentCardContent.attributes.length &&
          currentCardContent.attributes.length > 0 &&
          currentCardContent.attributes.map((attribute, index) => {
            return (
              <CardDetailContentAttribute
                card={card}
                setCard={setCard}
                key={attribute.id}
                currentCardContent={currentCardContent}
                setCardContents={setCardContents}
                attribute={attribute}
                attributeIndex={index}
                attributes={currentCardContent.attributes}
                disabled={isTrashed}
              />
            );
          })) || (
          <MuiTypography variant="subtitle2">
            Tato karta nemá atributy
          </MuiTypography>
        )}
      </div>
      {isLatestVersion && (
        <>
          <MuiToolbar disableGutters={true}>
            <MuiTypography style={{ marginTop: "15px" }} variant="h6">
              Soubory
            </MuiTypography>
            {currentCardContent.lastVersion && (
              <CardDetailContentAddFile
                card={card}
                setCard={setCard}
                currentCardContent={currentCardContent}
                setCardContents={setCardContents}
                refreshCard={refreshCard}
                disabled={isTrashed}
              />
            )}
          </MuiToolbar>
          <div>
            <CardDetailContentFile
              card={card}
              setCard={setCard}
              currentCardContent={currentCardContent}
              setCardContents={setCardContents}
              disabled={isTrashed}
            />
          </div>

          <MuiToolbar disableGutters={true}>
            <MuiTypography style={{ marginTop: "15px" }} variant="h6">
              Propojené karty
            </MuiTypography>
          </MuiToolbar>

          <div className={classes.columnsWrapper}>
            {currentCardContent.lastVersion && (
              <CardDetailContentCard
                card={card}
                setCard={setCard}
                disabled={isTrashed}
                currentCardContent={currentCardContent}
                setCardContents={setCardContents}
              />
            )}
            {connectedCards.map((connectedCard) => (
              <CardTile
                key={connectedCard.id}
                card={connectedCard}
                onSelect={onSelect}
                onRemove={() => {
                  const removedCards = card.linkedCards.filter(
                    (lc) => lc.id !== connectedCard.id
                  );
                  onEditCard(
                    "linkedCards",
                    removedCards,
                    card,
                    setCard,
                    currentCardContent,
                    setCardContents
                  );
                }}
              />
            ))}
          </div>
        </>
      )}
    </React.Fragment>
  );
};
