import { Field } from "formik";
import { get } from "lodash";
import React, { useState } from "react";
import { AsyncSelect } from "../../components/asyncSelect";
import { getCards } from "./_utils";

interface CardsLinkProps {
  onSelect: (newLinkedCards: any[]) => void;
  excludedCards: string[];
}

export const CardsLink: React.FC<CardsLinkProps> = ({
  onSelect,
  excludedCards,
}) => {
  const [cards, setCards] = useState<string[]>([]);

  return (
    <>
      <Field
        name="linkedCards"
        render={() => (
          <AsyncSelect
            value={cards}
            loadOptions={async (text) =>
              get(await getCards(text, 0, 10, excludedCards), "items")
            }
            onChange={(value) => {
              setCards(value);
              onSelect(value);
            }}
            isMulti={true}
          />
        )}
      />

      <div style={{ height: 140 }} />
    </>
  );
};
