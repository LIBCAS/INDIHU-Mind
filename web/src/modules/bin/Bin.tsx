import Button from "@material-ui/core/Button";
import Fade from "@material-ui/core/Fade";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import classNames from "classnames";
import React, { useCallback, useContext, useEffect, useState } from "react";
import { RouteComponentProps } from "react-router-dom";
import { CardTile } from "../../components/card/CardTile";
import { Loader } from "../../components/loader/Loader";
import { Popconfirm } from "../../components/portal/Popconfirm";
import { GlobalContext } from "../../context/Context";
import {
  STATUS_ERROR_COUNT_CHANGE,
  STATUS_ERROR_TEXT_SET,
} from "../../context/reducers/status";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { CardProps } from "../../types/card";
import { api } from "../../utils/api";
import { plural } from "../../utils/lang";

let controller = new AbortController();

export const Bin: React.FC<RouteComponentProps> = () => {
  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;
  const classesSpacing = useSpacingStyles();
  const classesLayout = useLayoutStyles();
  const [loading, setLoading] = useState(false);
  const [cards, setCards] = useState<CardProps[]>([]);

  const loadCards = useCallback(() => {
    if (!controller.signal.aborted) controller.abort();
    controller = new AbortController();
    setLoading(true);
    api()
      .post(`card/trash-bin`, {
        signal: controller.signal,
        json: { page: 0, pageSize: 30, order: "DESC" },
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
  }, [dispatch]);

  const onRestore = (card: any) => {
    api().post(`card/status`, {
      json: {
        ids: [card.id],
      },
    });
    setCards((prevCards) => prevCards.filter((c) => c.id !== card.id));
    dispatch({
      type: STATUS_ERROR_TEXT_SET,
      payload: `Karta ${card.name} byla obnovena`,
    });
    dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
  };

  const onRemove = (card: any) => {
    api()
      .delete(`card/${card.id}`)
      .then(() => {
        loadCards();
      });
    setCards((prevCards) => prevCards.filter((c) => c.id !== card.id));
    dispatch({
      type: STATUS_ERROR_TEXT_SET,
      payload: `Karta ${card.name} byla vysypána z koše`,
    });
    dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
  };

  const onRemoveAll = () => {
    setLoading(true);
    api()
      .delete(`card/trash-bin`)
      .json()
      .then((res: any) => {
        setLoading(false);
        setCards([]);
        dispatch({
          type: STATUS_ERROR_TEXT_SET,
          payload: `${res.count} ${plural(res.count, [
            "karta byla vysypána",
            "karty byly vysypány",
            "karet bylo vysypáno",
          ])} 
          z koše`,
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
  }, [loadCards]);
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
        <Grid style={{ maxWidth: "100%", margin: 0 }} container spacing={3}>
          {cards.map((c) => (
            <Grid key={c.id} item xs={12} md={6} lg={4}>
              <CardTile
                card={{
                  id: c.id,
                  name: c.name,
                  rawNote: c.rawNote as string,
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
