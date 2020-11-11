import React, { useState, useEffect, useContext } from "react";
import { flatten } from "lodash";

import { api } from "../../utils/api";
import { CardProps } from "../../types/card";
import { GlobalContext } from "../../context/Context";
import { CategoryProps } from "../../types/category";

import { Loader } from "../../components/loader/Loader";

import { useStyles } from "./_cardsStyles";
import { CardTile } from "../../components/card/CardTile";

interface ApiProps {
  count: number;
  items: CardProps[];
}

interface CardsTileProps {
  query: any;
}
export const CardsTile: React.FC<CardsTileProps> = ({ query }) => {
  const classes = useStyles();

  const [loading, setLoading] = useState(true);

  const [cards, setCards] = useState<
    { id: string; name: string; note: string }[]
  >([]);

  const context: any = useContext(GlobalContext);

  const { categoryActive } = context.state.category;
  const { labelActive } = context.state.label;

  useEffect(() => {
    // TODO: Implement pagination or infinite loading for card tiles display
    api()
      .post("card/parametrized", {
        json: {
          page: 0,
          pageSize: 999,
          ...(categoryActive || labelActive
            ? {
                filter: query,
              }
            : {}),
        },
      })
      .json<ApiProps>()
      .then((result: any) => {
        setLoading(false);
        setCards(result.items);
      });
  }, [query]);
  return (
    <>
      <Loader loading={loading} />
      <div className={classes.tileWrapper}>
        {cards.map((card) => (
          <CardTile key={card.id} card={card} showLabels={true} />
        ))}
      </div>
    </>
  );
};
