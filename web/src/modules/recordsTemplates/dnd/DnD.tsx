import React, { useCallback, useRef } from "react";
import Card from "./DndCard";
import classNames from "classnames";
import { useDrop } from "react-dnd";
import { ItemTypes, Item } from "./_types";

import { useStyles } from "./_dndStyles";
import { useStyles as useLayoutStyles } from "../../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";

import { DragItem } from "./DndCard";
import { Typography } from "@material-ui/core";
import { DnDAdd } from "./DnDAdd";
import { DnDRemove } from "./DnDRemove";
import { DnDAddFirst } from "./DnDAddFirst";
import { Formik, FormikProps, Form } from "formik";

export interface DnDProps {
  initCards: Item[];
  cards: Item[];
  setCards: React.Dispatch<React.SetStateAction<Item[]>>;
  formikBag: FormikProps<any>;
  recordTemplate: any;
}

const DnD: React.FC<DnDProps> = ({
  initCards,
  cards,
  setCards,
  formikBag,
  recordTemplate
}) => {
  const classes = useStyles();
  const classesLayout = useLayoutStyles();
  const classesSpacing = useSpacingStyles();

  const swapItems = <T extends unknown>(
    arr: T[],
    indexA: number,
    indexB: number
  ): T[] => {
    let arraySwapped = arr.slice();
    let temp = arraySwapped[indexA];
    arraySwapped[indexA] = arraySwapped[indexB];
    arraySwapped[indexB] = temp;
    return arraySwapped;
  };

  const moveCard = useCallback(
    (dragIndex: number, hoverIndex: number, item: DragItem) => {
      setCards(cards => {
        const isLast = hoverIndex >= cards.length;
        if (dragIndex === 9999 || isLast) {
          const { id } = item;
          const findById = initCards.find(c => c.id === id);
          let cardsAdded = cards.slice();
          if (findById) {
            let count = 0;
            for (const c of cards) {
              if (c.id === findById.id) {
                count += 1;
              }
            }
            const newCard = { ...findById, count };
            if (findById.id === "customizations") {
              formikBag.setFieldValue(id + count, "");
            } else {
              formikBag.setFieldValue(id + count + "customizations", []);
              formikBag.setFieldValue(id + count + "code", "");
            }

            if (isLast) {
              cardsAdded = [...cardsAdded, newCard];
            } else {
              cardsAdded.splice(hoverIndex, 0, newCard);
            }
          }
          return cardsAdded;
        } else {
          const swappedItems = swapItems(cards, dragIndex, hoverIndex);
          return swappedItems;
        }
      });
    },
    [cards]
  );
  return (
    <div>
      <Typography variant="subtitle1" gutterBottom>
        Vytvořená šablona
      </Typography>
      <div
        className={classNames(classesLayout.flex, classesLayout.spaceBetween)}
      >
        {cards.length === 0 && <DnDAddFirst moveCard={moveCard} />}
        {cards.length !== 0 && <DnDAdd cards={cards} moveCard={moveCard} />}
        <DnDRemove cards={cards} setCards={setCards} formikBag={formikBag} />
      </div>
      <div
        className={classNames(classesLayout.flex, classesLayout.flexWrap)}
        style={{ paddingRight: "-.5rem" }}
      >
        {cards.map(({ id, count, text }, index) => {
          return (
            <Card
              key={id + count}
              index={index}
              id={id}
              count={count}
              text={text}
              moveCard={moveCard}
              setCards={setCards}
              formikBag={formikBag}
              recordTemplate={recordTemplate}
            />
          );
        })}
      </div>
    </div>
  );
};

export default DnD;
