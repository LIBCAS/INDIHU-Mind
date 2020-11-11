import React, { useState } from "react";
import { get } from "lodash";

import { Field } from "formik";
import { getCards } from "./_utils";
import { AsyncSelect } from "../../components/asyncSelect";

interface CardsLinkProps {
  onSelect: (newLinkedCards: any[]) => void;
}

export const CardsLink: React.FC<CardsLinkProps> = ({ onSelect }) => {
  const [cards, setCards] = useState<string[]>([]);

  return (
    <>
      <Field
        name="linkedCards"
        render={() => (
          <AsyncSelect
            value={cards}
            loadOptions={async (text) => get(await getCards(text), "items")}
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
