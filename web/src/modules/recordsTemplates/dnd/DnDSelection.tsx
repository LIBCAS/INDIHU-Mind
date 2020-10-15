import React, { useRef } from "react";
import classNames from "classnames";
import { get } from "lodash";

import { useStyles } from "./_dndStyles";
import { useStyles as useLayoutStyles } from "../../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";

import { ItemTypes, Item } from "./_types";
import { DnDSelectionItem } from "./DnDSelectionItem";
import { Typography } from "@material-ui/core";
import { useDrop } from "react-dnd";
import { DragItem } from "./DndCard";

interface DnDSelectionProps {
  initCards: Item[];
  cards: Item[];
  setCards: React.Dispatch<React.SetStateAction<Item[]>>;
}

export const DnDSelection: React.FC<DnDSelectionProps> = ({
  cards,
  initCards,
  setCards
}) => {
  const classes = useStyles();
  const classesLayout = useLayoutStyles();
  const classesSpacing = useSpacingStyles();

  return (
    <div>
      <Typography variant="subtitle1" gutterBottom>
        Výběr MARC tagů
      </Typography>
      <div
        className={classNames(classesLayout.flex, classesLayout.flexWrap)}
        style={{ paddingRight: "-.5rem" }}
      >
        {Object.keys(initCards).map((key, i) => {
          const { id, text }: Item = get(initCards, key);
          return (
            <DnDSelectionItem
              key={id + i}
              id={id}
              tag={id === "customizations" ? text : id}
              index={9999}
            />
          );
        })}
      </div>
    </div>
  );
};
