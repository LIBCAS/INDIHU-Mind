import React, { useState, useEffect, useContext, useCallback } from "react";
import { RouteComponentProps } from "react-router-dom";
import Typography from "@material-ui/core/Typography";
import Button from "@material-ui/core/Button";
import classNames from "classnames";
import Fade from "@material-ui/core/Fade";
import Grid from "@material-ui/core/Grid";

import { GlobalContext } from "../../context/Context";
import {
  STATUS_ERROR_COUNT_CHANGE,
  STATUS_ERROR_TEXT_SET
} from "../../context/reducers/status";
import { plural } from "../../utils/lang";
import { CardProps } from "../../types/card";
import { api } from "../../utils/api";
import { Popconfirm } from "../../components/portal/Popconfirm";
import { Loader } from "../../components/loader/Loader";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";

import { CardTile } from "../../components/card/CardTile";

let controller = new AbortController();

export const Bin: React.FC<RouteComponentProps> = () => {
  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;
  const classesSpacing = useSpacingStyles();
  const classesLayout = useLayoutStyles();
  const [loading, setLoading] = useState(false);
  const [cards, setCards] = useState<CardProps[]>([]);

  const loadCards = useCallback(() => {
    if (!controller.signal.aborted && loading) controller.abort();
    controller = new AbortController();
    setLoading(true);
    api()
      .post(`card/deleted`, {
        signal: controller.signal,
        json: { page: 0, pageSize: 30 }
      })
      .json()
      .then((res: any) => {
        setLoading(false);
        setCards(res.items);
      })
      .catch(() => {
        dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
        setLoading(false);
      });
  }, []);

  const onRestore = (card: any) => {
    api().post(`card/set-softdelete`, {
      json: {
        ids: [card.id],
        value: false
      }
    });
    setCards(prevCards => prevCards.filter(c => c.id !== card.id));
    dispatch({
      type: STATUS_ERROR_TEXT_SET,
      payload: `Karta ${card.name} byla obnovena`
    });
    dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
  };

  const onRemove = (card: any) => {
    api()
      .delete(`card/${card.id}`)
      .then(() => {
        loadCards();
      });
    setCards(prevCards => prevCards.filter(c => c.id !== card.id));
    dispatch({
      type: STATUS_ERROR_TEXT_SET,
      payload: `Karta ${card.name} byla vysypána z koše`
    });
    dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
  };

  const onRemoveAll = () => {
    setLoading(true);
    api()
      .delete(`card/soft-deleted`)
      .json<number>()
      .then((res: any) => {
        setLoading(false);
        setCards([]);
        dispatch({
          type: STATUS_ERROR_TEXT_SET,
          payload: `${res} ${plural(res, [
            "karta byla vysypána",
            "karty byly vysypány",
            "karet bylo vysypáno"
          ])} 
          z koše`
        });
        dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
      })
      .catch(() => {
        setLoading(false);
        dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
      });
  };

  useEffect(() => {
    loadCards();
  }, []);
  return (
    <Fade in>
      <div>
        <div
          className={classNames(classesLayout.flex, classesLayout.alignCenter)}
        >
          <Typography
            display="inline"
            className={classNames(classesSpacing.mb2, classesSpacing.mt2)}
            variant="h5"
          >
            <p className={classNames(classesSpacing.mr2)}>Koš</p>
          </Typography>
          <Popconfirm
            confirmText="Vysypat koš?"
            acceptText="Ano"
            onConfirmClick={() => onRemoveAll()}
            Button={
              <Button color="secondary" variant="contained">
                Vysypat koš
              </Button>
            }
          />
        </div>
        <div style={{ position: "relative" }}>
          <Loader loading={loading} local />
        </div>
        {!loading && cards.length === 0 && (
          <Typography>Žádné karty v koši</Typography>
        )}
        <Grid container spacing={3}>
          {cards.map(c => (
            <Grid key={c.id} item xs={12} md={6} lg={4}>
              <CardTile
                card={{
                  id: c.id,
                  name: c.name,
                  note: c.note as string
                }}
                onRestore={onRestore}
                onRemove={onRemove}
                onRemoveText="Vysypat kartu z koše"
                topMargin={0}
              />
            </Grid>
          ))}
        </Grid>
      </div>
    </Fade>
  );
};

export { Bin as default };
