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
    api()
      .get("card")
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
