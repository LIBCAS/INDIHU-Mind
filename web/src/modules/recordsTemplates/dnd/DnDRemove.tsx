import React, { useRef } from "react";
import { DragItem } from "./DndCard";
import { ItemTypes, Item } from "./_types";
import { useDrop } from "react-dnd";

import { useStyles } from "./_dndStyles";
import { FormikProps } from "formik";

interface DnDRemoveProps {
  cards: Item[];
  setCards: React.Dispatch<React.SetStateAction<Item[]>>;
  formikBag: FormikProps<any>;
}

export const DnDRemove: React.FC<DnDRemoveProps> = ({
  cards,
  setCards,
  formikBag
}) => {
  const classes = useStyles();
  const refRemove = useRef<HTMLDivElement>(null);
  const [, dropLast] = useDrop({
    accept: ItemTypes.CARD,
    drop(item: DragItem) {
      if (!refRemove.current || item.index === 9999) {
        return;
      }
      setCards(c => {
        let cardsRemoved = c.slice();
        const cardFound = cardsRemoved.find((c, i) => i === item.index);
        cardsRemoved.splice(item.index, 1);
        let count = -1;
        cardsRemoved = cardsRemoved.map(c => {
          if (c.id === item.id) {
            count += 1;
            return {
              ...c,
              count
            };
          } else {
            return c;
          }
        });
        if (cardFound) {
          const { id, count } = cardFound;
          if (id === "customizations") {
            formikBag.setFieldValue(id + count, "");
          } else {
            formikBag.setFieldValue(id + count + "code", "");
            formikBag.setFieldValue(id + count + "customizations", []);
          }
        }

        return cardsRemoved;
      });
    }
  });
  dropLast(refRemove);
  return (
    <div ref={refRemove} className={classes.container}>
      Odebrat tag
    </div>
  );
};
