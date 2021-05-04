import React, { useEffect, useState } from "react";
import { Loader } from "../../components/loader/Loader";
import { CardProps } from "../../types/card";
import { api } from "../../utils/api";
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
      .get(`card/${item.id}/contents`)
      .json()
      .then((res: any) => {
        if (res.length > 0 && res[0].card) {
          api()
            .get(`card/${item.id}`)
            .json<CardProps>()
            .then((card: CardProps) => {
              setCardContent({ ...card, attributes: res[0].attributes });
              setLoading(false);
            });
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
