import React, { useState, useEffect, useCallback } from "react";
import { RouteComponentProps } from "react-router-dom";
import Fade from "@material-ui/core/Fade";
import Grid from "@material-ui/core/Grid";
import classNames from "classnames";
import { uniqBy, isEmpty } from "lodash";

import { set, get } from "../../utils/store";
import { Loader } from "../../components/loader/Loader";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { CardContentProps } from "../../types/card";
import { api } from "../../utils/api";

import { CardDetailActions } from "./CardDetailActions";
import { CardDetailContent } from "./CardDetailContent";
import { CardDetailComments } from "./CardDetailComments";
import { CardDetailMeta } from "./CardDetailMeta";

export const CardDetail: React.FC<RouteComponentProps> = ({
  history,
  match,
}) => {
  const classesSpacing = useSpacingStyles();
  // @ts-ignore
  const cardId = match.params.id;
  const [loading, setLoading] = useState<boolean>(false);
  const [cardContent, setCardContent] = useState<
    CardContentProps[] | undefined
  >(undefined);
  const [card, setCard] = useState<CardContentProps | undefined>(undefined);
  const loadCard = useCallback(() => {
    setLoading(true);
    api()
      .get(`card/${cardId}/content`)
      .json()
      .then((res: any) => {
        setLoading(false);
        if (res.length > 0 && res[0].card) {
          setCardContent(res);
          // setCard({ ...res[0].card, attributes: res[0].attributes });
          setCard({ ...res[0] });
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
  }, [cardId]);
  useEffect(() => {
    setCard((card) => {
      if (cardContent && card) {
        const res = cardContent.filter((c) => c.id === card.id);
        return { ...res[0] };
      } else {
        return undefined;
      }
    });
  }, [cardContent]);
  useEffect(() => {
    loadCard();
    }, []); // eslint-disable-line

  return (
    <Fade in>
      <div>
        <Loader loading={loading} />
        {!card && !loading && <div>Žádná karta</div>}
        {!loading &&
          card &&
          cardContent &&
          !isEmpty(card) &&
          !isEmpty(cardContent) && (
            <div className={classNames(classesSpacing.ml1, classesSpacing.mr1)}>
              <Grid container>
                <Grid item xs={12} lg={9}>
                  <CardDetailActions
                    card={card}
                    history={history}
                    loadCard={loadCard}
                  />
                  <CardDetailContent
                    card={card}
                    history={history}
                    cardContent={cardContent}
                    setCardContent={setCardContent}
                    refreshCard={loadCard}
                  />
                  <CardDetailComments card={card} />
                </Grid>
                <Grid item xs={12} lg={3}>
                  <CardDetailMeta
                    card={card}
                    setCard={setCard}
                    cardContent={cardContent}
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
