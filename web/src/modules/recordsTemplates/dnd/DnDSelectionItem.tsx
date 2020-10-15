import React from "react";
import { useDrag } from "react-dnd";

import { ItemTypes } from "./_types";
import { useStyles } from "./_dndStyles";

interface DnDSelectionItemProps {
  id: string;
  index: number;
  tag: string;
}

export const DnDSelectionItem: React.FC<DnDSelectionItemProps> = ({
  id,
  index,
  tag
}) => {
  const classes = useStyles();
  const [{ isDragging }, drag] = useDrag({
    item: { type: ItemTypes.CARD, id, index },
    collect: (monitor: any) => ({
      isDragging: monitor.isDragging()
    })
  });
  const opacity = isDragging ? 0 : 1;
  return (
    <div style={{ opacity }} ref={drag} className={classes.cardSelection}>
      {tag}
    </div>
  );
};
