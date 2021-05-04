import { Tooltip } from "@material-ui/core";
import AddCircleIcon from "@material-ui/icons/AddCircle";
import InfoIcon from "@material-ui/icons/Info";
import classNames from "classnames";
import { FormikProps } from "formik";
import { find } from "lodash";
import React from "react";
import {
  AuthorsAndJoiner,
  AuthorsSeparator,
  FirstNameFormat,
  MultipleAuthorsFormat,
  OrderFormat,
} from "../_enums";
import { createMarcLabel } from "../_utils";
import { useStyles } from "./_dndStyles";
import { Item } from "./_types";

interface DnDSelectionItemProps {
  id: string;
  index: number;
  text: string;
  initCards: Item[];
  cards: Item[];
  setCards: React.Dispatch<React.SetStateAction<Item[]>>;
  formikBag: FormikProps<any>;
  tag?: string;
  code?: string;
  indicator1?: string;
  indicator2?: string;
  __hidden__?: boolean;
}

export const DnDSelectionItem: React.FC<DnDSelectionItemProps> = ({
  id,
  text,
  initCards,
  cards,
  setCards,
  formikBag,
  tag,
  code,
  indicator1,
  indicator2,
  __hidden__,
}) => {
  const classes = useStyles();

  const isAuthor = id === "AUTHOR";
  const isAuthorAdded = isAuthor && find(cards, ({ id }) => id === "AUTHOR");

  const handleAdd = () => {
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

      const createName = (path: string) => id + count + path;

      formikBag.setFieldValue(createName("customizations"), []);

      if (isAuthor) {
        formikBag.setFieldValue(
          createName("firstNameFormat"),
          FirstNameFormat.FULL
        );
        formikBag.setFieldValue(
          createName("multipleAuthorsFormat"),
          MultipleAuthorsFormat.FULL
        );
        formikBag.setFieldValue(
          createName("orderFormat"),
          OrderFormat.FIRSTNAME_FIRST
        );
        formikBag.setFieldValue(
          createName("separator"),
          AuthorsSeparator.COMMA
        );
        formikBag.setFieldValue(
          createName("andJoiner"),
          AuthorsAndJoiner.CZECH_AND
        );
      }

      cardsAdded.push(newCard);
    }

    setCards(cardsAdded);
  };

  return (
    <div
      className={classNames(
        classes.cardSelection,
        isAuthorAdded && classes.cardSelectionDisabled
      )}
    >
      <div
        className={classNames(
          classes.cardSelectionText,
          tag && classes.cardSelectionTextShort
        )}
      >
        {text}
      </div>
      <div className={classes.cardSelectionIcons}>
        {tag && !__hidden__ && (
          <Tooltip
            title={createMarcLabel({ tag, code, indicator1, indicator2 })}
          >
            <InfoIcon />
          </Tooltip>
        )}
        {!isAuthorAdded ? (
          <div className={classes.cardSelectionAdd}>
            <AddCircleIcon onClick={handleAdd} />
          </div>
        ) : (
          <div />
        )}
      </div>
    </div>
  );
};
