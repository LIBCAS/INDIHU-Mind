import Fade from "@material-ui/core/Fade";
import Grid from "@material-ui/core/Grid";
import classNames from "classnames";
import { isEmpty, uniqBy } from "lodash";
import React, { useCallback, useEffect, useState } from "react";
import { RouteComponentProps } from "react-router-dom";
import { Loader } from "../../components/loader/Loader";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { CardContentProps, CardProps } from "../../types/card";
import { api } from "../../utils/api";
import { get, set } from "../../utils/store";
import { CardDetailActions } from "./CardDetailActions";
import { CardDetailComments } from "./CardDetailComments";
import { CardDetailContent } from "./CardDetailContent";
import { CardDetailMeta } from "./CardDetailMeta";
import { useStyles } from "./_cardDetailStyles";

export const CardDetail: React.FC<RouteComponentProps> = ({
  history,
  match,
}) => {
  const classes = useStyles();
  const classesSpacing = useSpacingStyles();
  // @ts-ignore
  const cardId = match.params.id;
  const [loading, setLoading] = useState<boolean>(false);
  const [cardContents, setCardContents] = useState<
    CardContentProps[] | undefined
  >(undefined);
  const [currentCardContent, setCurrentCardContent] = useState<
    CardContentProps | undefined
  >(undefined);
  const [card, setCard] = useState<CardProps>();

  const loadCard = useCallback(() => {
    setLoading(true);
    api()
      .get(`card/${cardId}/contents`)
      .json()
      .then((res: any) => {
        if (res.length > 0 && res[0].card) {
          setCardContents(res);
          // setCard({ ...res[0].card, attributes: res[0].attributes });
          setCurrentCardContent({ ...res[0] });
          let openedCards = get("cardsOpened", undefined);
          if (openedCards) {
            openedCards = [
              { id: cardId, name: res[0].card.name },
              ...openedCards,
            ];
            openedCards = uniqBy(openedCards, "id");
            openedCards = openedCards.slice(0, 5);
            set("cardsOpened", openedCards);
          } else {
            set("cardsOpened", [{ id: cardId, name: res[0].card.name }]);
          }
        }
        return;
      })
      .catch(() => {
        setLoading(false);
        // TODO error
      });
    api()
      .get(`card/${cardId}`)
      .json<CardProps>()
      .then((res: CardProps) => {
        setLoading(false);
        setCard(res);
      })
      .catch(() => {
        setLoading(false);
      });
  }, [cardId]);

  useEffect(() => {
    setCurrentCardContent((card) => {
      if (cardContents && card) {
        const res = cardContents.filter((c) => c.id === card.id);
        return { ...res[0] };
      } else {
        return undefined;
      }
    });
  }, [cardContents]);

  useEffect(() => {
    loadCard();
  }, [loadCard]);

  const isTrashed =
    currentCardContent &&
    currentCardContent.card &&
    currentCardContent.card.status === "TRASHED";

  return (
    <Fade in>
      <div>
        <Loader loading={loading} />
        {!currentCardContent && !loading && <div>Žádná karta</div>}
        {!loading &&
          currentCardContent &&
          cardContents &&
          card &&
          !isEmpty(currentCardContent) &&
          !isEmpty(cardContents) && (
            <div className={classNames(classesSpacing.ml1, classesSpacing.mr1)}>
              <Grid container>
                <Grid item xs={12} lg={9}>
                  <CardDetailActions
                    card={card}
                    cardContent={currentCardContent}
                    history={history}
                    loadCard={loadCard}
                    isTrashed={isTrashed}
                  />
                  {isTrashed && (
                    <div
                      style={{
                        display: "flex",
                        justifyContent: "center",
                      }}
                    >
                      <span
                        style={{
                          fontSize: "2rem",
                          border: "1px solid gray",
                          borderRadius: 8,
                          padding: 8,
                        }}
                      >
                        Karta je v koši.
                      </span>
                    </div>
                  )}
                  <div className={isTrashed ? classes.cardDetailTrashed : ""}>
                    <CardDetailContent
                      card={card}
                      setCard={setCard}
                      currentCardContent={currentCardContent}
                      history={history}
                      cardContents={cardContents}
                      setCardContents={setCardContents}
                      refreshCard={loadCard}
                      isTrashed={Boolean(isTrashed)}
                    />
                    <CardDetailComments
                      card={card}
                      disabled={isTrashed || !currentCardContent.lastVersion}
                      currentCardContent={currentCardContent}
                    />
                  </div>
                </Grid>
                <Grid item xs={12} lg={3}>
                  <CardDetailMeta
                    currentCardContent={currentCardContent}
                    setCurrentCardContent={setCurrentCardContent}
                    cardContents={cardContents}
                  />
                </Grid>
              </Grid>
            </div>
          )}
      </div>
    </Fade>
  );
};

export { CardDetail as default };
