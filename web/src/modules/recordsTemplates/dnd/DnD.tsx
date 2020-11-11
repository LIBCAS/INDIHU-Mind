import React, { useCallback, useState } from "react";
import classNames from "classnames";
import { Typography } from "@material-ui/core";
import { FormikProps } from "formik";
import { filter } from "lodash";

import { Item } from "./_types";
import { useStyles as useLayoutStyles } from "../../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";

import Card, { DragItem } from "./DndCard";

export interface DnDProps {
  initCards: Item[];
  cards: Item[];
  setCards: React.Dispatch<React.SetStateAction<Item[]>>;
  formikBag: FormikProps<any>;
}

const DnD: React.FC<DnDProps> = ({ initCards, cards, setCards, formikBag }) => {
  const classesLayout = useLayoutStyles();
  const classesSpacing = useSpacingStyles();

  const [active, setActive] = useState<string | null>(null);

  const moveItem = <T extends unknown>(
    arr: T[],
    indexA: number,
    indexB: number
  ): T[] => {
    const lower = indexA < indexB;
    const lowerIndex = lower ? indexA : indexB;
    const higherIndex = lower ? indexB : indexA;

    return Math.abs(indexA - indexB) === 1
      ? [
          ...arr.slice(0, lowerIndex),
          arr[higherIndex],
          arr[lowerIndex],
          ...arr.slice(higherIndex + 1),
        ]
      : [
          ...arr.slice(0, lowerIndex),
          ...(lower ? [] : [arr[indexA]]),
          ...arr.slice(
            lowerIndex + (lower ? 1 : 0),
            higherIndex + (lower ? 1 : 0)
          ),
          ...(lower ? [arr[indexA]] : []),
          ...arr.slice(higherIndex + 1),
        ];
  };

  const moveCard = useCallback(
    (dragIndex: number, hoverIndex: number, item: DragItem) => {
      setCards((cards) => {
        const isLast = hoverIndex >= cards.length;
        if (dragIndex === 9999 || isLast) {
          const { id } = item;
          const findById = initCards.find((c) => c.id === id);
          let cardsAdded = cards.slice();
          if (findById) {
            let count = 0;
            for (const c of cards) {
              if (c.id === findById.id) {
                count += 1;
              }
            }
            const newCard = { ...findById, count };

            formikBag.setFieldValue(id + count + "customizations", []);

            if (isLast) {
              cardsAdded = [...cardsAdded, newCard];
            } else {
              cardsAdded.splice(hoverIndex, 0, newCard);
            }
          }
          return cardsAdded;
        } else {
          return moveItem(cards, dragIndex, hoverIndex);
        }
      });
    },
    [formikBag, initCards, setCards]
  );
  return (
    <div className={classesSpacing.pt2}>
      <Typography variant="subtitle1" gutterBottom>
        Vytvořená šablona
      </Typography>
      <div className={classesSpacing.mb2} />
      <div
        className={classNames(classesLayout.flex, classesLayout.flexWrap)}
        style={{ paddingRight: "-.5rem" }}
      >
        {cards.map(({ id, count, text }, index) => {
          const key = id + count;
          const isActive = active === key;
          return (
            <Card
              key={key}
              index={index}
              id={id}
              count={count}
              text={text}
              moveCard={moveCard}
              formikBag={formikBag}
              removeCard={() =>
                setCards(filter(cards, (c) => c.id !== id || c.count !== count))
              }
              active={isActive}
              toggleActive={() => setActive(isActive ? null : key)}
            />
          );
        })}
      </div>
    </div>
  );
};

export default DnD;
