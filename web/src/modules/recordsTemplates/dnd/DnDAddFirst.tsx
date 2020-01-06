import React, { useRef } from "react";

import { useDrop } from "react-dnd";

import { DragItem } from "./DndCard";
import { useStyles } from "./_dndStyles";
import { ItemTypes, Item } from "./_types";

interface DnDAddFirstProps {
  moveCard: (dragIndex: number, hoverIndex: number, item: DragItem) => void;
}

export const DnDAddFirst: React.FC<DnDAddFirstProps> = ({ moveCard }) => {
  const classes = useStyles();
  const ref = useRef<HTMLDivElement>(null);
  const [, drop] = useDrop({
    accept: ItemTypes.CARD,
    drop(item: DragItem) {
      if (!ref.current) {
        return;
      }
      moveCard(item.index, 0, item);
      item.index = 0;
    }
  });
  drop(ref);
  return (
    <div className={classes.container} ref={ref}>
      Přidat tag
    </div>
  );
};
