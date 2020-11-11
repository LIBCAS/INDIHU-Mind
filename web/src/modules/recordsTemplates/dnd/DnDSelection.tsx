import React from "react";
import classNames from "classnames";
import { FormikProps } from "formik";

import { useStyles as useLayoutStyles } from "../../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";

import { Item } from "./_types";
import { DnDSelectionItem } from "./DnDSelectionItem";
import { Typography } from "@material-ui/core";

interface DnDSelectionProps {
  initCards: Item[];
  label?: string;
  cards: Item[];
  setCards: React.Dispatch<React.SetStateAction<Item[]>>;
  formikBag: FormikProps<any>;
}

export const DnDSelection: React.FC<DnDSelectionProps> = ({
  label,
  ...rest
}) => {
  const classesLayout = useLayoutStyles();
  const classesSpacing = useSpacingStyles();

  return (
    <div>
      {label && (
        <Typography variant="subtitle1" gutterBottom>
          {label}
        </Typography>
      )}
      <div
        className={classNames(
          classesLayout.flex,
          classesLayout.directionColumn,
          classesSpacing.mb2
        )}
        style={{ paddingRight: "-.5rem" }}
      >
        {rest.initCards.map((card, i) => {
          return (
            <DnDSelectionItem
              key={card.id + i}
              index={9999}
              {...card}
              {...rest}
            />
          );
        })}
      </div>
    </div>
  );
};
