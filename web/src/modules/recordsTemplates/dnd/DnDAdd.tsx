import React, { useRef } from "react";

import { useDrop } from "react-dnd";

import { DragItem } from "./DndCard";
import { useStyles } from "./_dndStyles";
import { ItemTypes, Item } from "./_types";

interface DnDAddProps {
  cards: Item[];
  moveCard: (dragIndex: number, hoverIndex: number, item: DragItem) => void;
}

export const DnDAdd: React.FC<DnDAddProps> = ({ cards, moveCard }) => {
  const classes = useStyles();
  const refLast = useRef<HTMLDivElement>(null);
  const [, dropLast] = useDrop({
    accept: ItemTypes.CARD,
    drop(item: DragItem) {
      if (!refLast.current) {
        return;
      }
      const lastIndex = cards.length;
      moveCard(item.index, lastIndex, item);
      item.index = lastIndex;
    }
  });
  dropLast(refLast);
  return (
    <div className={classes.container} ref={refLast}>
      Přidat tag
    </div>
  );
};
