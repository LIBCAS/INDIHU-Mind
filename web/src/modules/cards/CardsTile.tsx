import React, { useState, useEffect } from "react";

import { api } from "../../utils/api";
import { CardProps } from "../../types/card";

import { Loader } from "../../components/loader/Loader";

import { CardTileItem } from "./CardsTileItem";
import { useStyles } from "./_cardsStyles";

interface ApiProps {
  count: number;
  items: CardProps[];
}

export const CardsTile: React.FC = () => {
  const classes = useStyles();

  const [loading, setLoading] = useState(true);

  const [cards, setCards] = useState<CardProps[]>([]);

  useEffect(() => {
    // TODO: Implement pagination or infinite loading for card tiles display
    api()
      .post("card/parametrized", {
        json: {
          page: 0,
          pageSize: 999
        }
      })
      .json<ApiProps>()
      .then(result => {
        setLoading(false);
        setCards(result.items);
      });
  }, []);
  return (
    <>
      <Loader loading={loading} />
      <div className={classes.tileWrapper}>
        {cards.map(card => (
          <CardTileItem key={card.id} card={card} />
        ))}
      </div>
    </>
  );
};
