import React, { useEffect, useState } from "react";

import { CardProps } from "../../types/card";

import { api } from "../../utils/api";
import { Loader } from "../../components/loader/Loader";

import { CardsTableDetailContent } from "./CardsTableDetailContent";

interface CardsTableDetailProps {
  item: CardProps;
}

export const CardsTableDetail: React.FC<CardsTableDetailProps> = ({ item }) => {
  const [loading, setLoading] = useState<boolean>(false);
  const [cardContent, setCardContent] = useState<CardProps | undefined>(
    undefined
  );
  useEffect(() => {
    setLoading(true);
    api()
      .get(`card/${item.id}/content`)
      .json()
      .then((res: any) => {
        setLoading(false);
        if (res.length > 0 && res[0].card) {
          setCardContent({ ...res[0].card, attributes: res[0].attributes });
        }
        return;
      })
      .catch(() => {
        setLoading(false);
        // TODO error
      });
  }, [item]);
  return (
    <div style={{ position: "relative" }}>
      <Loader loading={loading} local />
      {cardContent && !loading && (
        <CardsTableDetailContent card={cardContent} />
      )}
    </div>
  );
};

export { CardsTableDetail as default };
