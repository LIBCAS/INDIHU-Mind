import React, { useEffect, useState } from "react";

import { CardProps } from "../../types/card";

import { api } from "../../utils/api";
import { Loader } from "../../components/loader/Loader";

import { CardsTableDetailContent } from "./CardsTableDetailContent";

interface CardsTableDetailProps {
  selectedCard: CardProps;
  onCancel(e: React.MouseEvent): void;
}

export const CardsTableDetail: React.FC<CardsTableDetailProps> = ({
  selectedCard,
  onCancel
}) => {
  const [loading, setLoading] = useState<boolean>(false);
  const [cardContent, setCardContent] = useState<CardProps | undefined>(
    undefined
  );
  useEffect(() => {
    setLoading(true);
    api()
      .get(`card/${selectedCard.id}/content`)
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
  }, [selectedCard]);
  return (
    <div style={{ position: "relative" }}>
      <Loader loading={loading} local />
      {cardContent && !loading && (
        <CardsTableDetailContent card={cardContent} onCancel={onCancel} />
      )}
    </div>
  );
};

export { CardsTableDetail as default };
