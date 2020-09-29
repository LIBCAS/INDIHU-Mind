import React, { useState, useEffect } from "react";
import { get } from "lodash";
import { Button, Typography } from "@material-ui/core";
import classNames from "classnames";

import { LinkedCardProps, CardProps } from "../../types/card";
import { api } from "../../utils/api";

import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { Field, FieldProps } from "formik";
import { OptionType } from "../../components/select/_types";
import { Select } from "../../components/form/Select";
import { getCards } from "./_utils";
import { AsyncSelect } from "../../components/asyncSelect";

interface CardsLinkProps {
  onSelect: (newLinkedCards: any[]) => void;
}

export const CardsLink: React.FC<CardsLinkProps> = ({ onSelect }) => {
  const classesSpacing = useSpacingStyles();
  const [cards, setCards] = useState<string[]>([]);

  return (
    <>
      <Field
        name="linkedCards"
        render={({ field, form }: FieldProps<any>) => (
          <AsyncSelect
            value={cards}
            loadOptions={async text => get(await getCards(text, 0), "items")}
            onChange={value => {
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
