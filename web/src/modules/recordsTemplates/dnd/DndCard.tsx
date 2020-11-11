import React, { useRef } from "react";
import { useDrag, useDrop, DropTargetMonitor } from "react-dnd";
import { XYCoord } from "dnd-core";
import {
  Tooltip,
  // ClickAwayListener
} from "@material-ui/core";
import classNames from "classnames";
import { get, filter } from "lodash";
import FormatBoldIcon from "@material-ui/icons/FormatBold";
import FormatItalicIcon from "@material-ui/icons/FormatItalic";
import FormatSizeIcon from "@material-ui/icons/FormatSize";
import DeleteIcon from "@material-ui/icons/Delete";
import { FieldProps, Field } from "formik";

import { createStyle } from "../_utils";
import { Select } from "../../../components/form/Select";
import { useStyles } from "./_dndStyles";
import { ItemTypes } from "./_types";
import { FirstNameFormat, MultipleAuthorsFormat, OrderFormat } from "../_enums";

export interface CardProps {
  id: any;
  text: string;
  index: number;
  moveCard: (dragIndex: number, hoverIndex: number, item: DragItem) => void;
  formikBag: any;
  count: number;
  removeCard: () => void;
  active: boolean;
  toggleActive: () => void;
}

export interface DragItem {
  index: number;
  id: string;
  type: string;
}

const createCardId = (id: string, index: number) =>
  `records-templates-dnd-card-${id}-${index}`;

let recordsTemplateDndCardTimestamp: number;

const Card: React.FC<CardProps> = ({
  id,
  text,
  index,
  moveCard,
  formikBag,
  count,
  removeCard,
  active,
  toggleActive,
}) => {
  const classes = useStyles();
  const ref = useRef<HTMLDivElement>(null);
  const [, drop] = useDrop({
    accept: ItemTypes.CARD,
    hover(item: DragItem, monitor: DropTargetMonitor) {
      if (!ref.current) {
        return;
      }

      const dragIndex = item.index;
      const hoverIndex = index;

      // Don't replace items with themselves
      if (dragIndex === hoverIndex) {
        return;
      }

      if (Math.abs(dragIndex - hoverIndex) === 1) {
        const currentTime = new Date().getTime();

        if (
          recordsTemplateDndCardTimestamp &&
          recordsTemplateDndCardTimestamp + 1000 > currentTime
        ) {
          return;
        }

        recordsTemplateDndCardTimestamp = currentTime;
      }

      // Determine rectangle on screen
      const hoverBoundingRect = ref.current
        ? ref.current.getBoundingClientRect()
        : { bottom: 0, top: 0, left: 0, right: 0, width: 0 };

      // Get top
      const hoverTop = hoverBoundingRect.top;
      // Get left
      const hoverLeft = hoverBoundingRect.left;

      const hoverWidth = hoverBoundingRect.width;

      const dragElement = document.getElementById(
        createCardId(item.id, item.index)
      );

      const dragElementRect = dragElement
        ? dragElement.getBoundingClientRect()
        : null;

      // Determine mouse position
      const clientOffset = monitor.getClientOffset();

      // Get pixels to the top
      const hoverClientTop =
        (clientOffset as XYCoord).y - hoverBoundingRect.top;
      // Get pixels to the left
      const hoverClientLeft =
        (clientOffset as XYCoord).x - hoverBoundingRect.left;

      const safeDistance = dragElementRect
        ? dragElementRect.left > hoverLeft &&
          dragElementRect.left <
            hoverLeft + Math.min(hoverWidth, dragElementRect.width) / 2
        : hoverClientLeft > 30 || hoverClientLeft < -30;

      // Only perform the move when the mouse has crossed half of the items height
      // When dragging downwards, only move when the cursor is below 50%
      // When dragging upwards, only move when the cursor is above 50%

      // Dragging downwards
      if (
        dragIndex < hoverIndex &&
        hoverClientTop < hoverTop &&
        hoverClientLeft >= hoverLeft &&
        safeDistance
      ) {
        return;
      }

      // Dragging upwards
      if (
        dragIndex > hoverIndex &&
        hoverClientTop > hoverTop &&
        hoverClientLeft <= hoverLeft &&
        safeDistance
      ) {
        return;
      }

      // Time to actually perform the action
      moveCard(dragIndex, hoverIndex, item);

      // Note: we're mutating the monitor item here!
      // Generally it's better to avoid mutations,
      // but it's good here for the sake of performance
      // to avoid expensive index searches.

      item.index = hoverIndex;
    },
  });

  const [{ isDragging }, drag] = useDrag({
    item: { type: ItemTypes.CARD, id, index },
    collect: (monitor: any) => ({
      isDragging: monitor.isDragging(),
    }),
  });

  const opacity = isDragging ? 0 : 1;
  drag(drop(ref));

  const isAuthor = id === "AUTHOR";

  // const handleClickAway = () => {
  //   if (!isAuthor) {
  //     toggleActive();
  //   }
  // };

  const customizations = get(
    formikBag,
    `values.${id + count}customizations`,
    []
  );

  const isBold = customizations.includes("BOLD");
  const isItalic = customizations.includes("ITALIC");
  const isUppercase = customizations.includes("UPPERCASE");

  const cardElement = document.getElementById(createCardId(id, index));

  return (
    // <ClickAwayListener onClickAway={handleClickAway}>
    <div className={classes.cardContainer} style={{ opacity }}>
      {active && (
        <div
          className={classNames(
            classes.cardMenu,
            cardElement &&
              cardElement.getBoundingClientRect().left >
                2 * (window.innerWidth / 3)
              ? classes.cardMenuRight
              : null,
            isAuthor && classes.cardMenuBottom
          )}
        >
          <div className={classes.cardMenuIcons}>
            {[
              {
                Icon: FormatBoldIcon,
                value: "BOLD",
                title: "Tučně",
                selected: isBold,
              },
              {
                Icon: FormatItalicIcon,
                value: "ITALIC",
                title: "Kurzíva",
                selected: isItalic,
              },
              {
                Icon: FormatSizeIcon,
                value: "UPPERCASE",
                title: "Velká písmena",
                selected: isUppercase,
              },
              {
                Icon: DeleteIcon,
                value: "DELETE",
                title: "Odstranit",
              },
            ].map(({ Icon, value, title, selected }) => (
              <Tooltip key={value} title={title}>
                <Icon
                  onClick={() => {
                    if (value === "DELETE") {
                      toggleActive();
                      removeCard();
                    } else {
                      formikBag.setFieldValue(
                        id + count + "customizations",
                        selected
                          ? filter(customizations, (c) => c !== value)
                          : [...customizations, value]
                      );
                    }
                  }}
                  className={classNames(selected && classes.iconSelected)}
                />
              </Tooltip>
            ))}
          </div>
          {isAuthor && (
            <div className={classes.creatorSelects}>
              {[
                {
                  name: "firstNameFormat",
                  placeholder: "Podoba jména",
                  options: [
                    { value: FirstNameFormat.FULL, label: "Celé jméno" },
                    { value: FirstNameFormat.INITIAL, label: "Zkrácené jméno" },
                  ],
                },
                {
                  name: "multipleAuthorsFormat",
                  placeholder: "Formát u více tvůrců",
                  options: [
                    { value: MultipleAuthorsFormat.FULL, label: "Úplný výpis" },
                    {
                      value: MultipleAuthorsFormat.ETAL,
                      label: "Zkrácený výpis (et al.)",
                    },
                  ],
                },
                {
                  name: "orderFormat",
                  placeholder: "Pořadí",
                  options: [
                    {
                      value: OrderFormat.FIRSTNAME_FIRST,
                      label: "Křestní jméno první",
                    },
                    {
                      value: OrderFormat.LASTNAME_FIRST,
                      label: "Příjmení první",
                    },
                  ],
                },
              ].map(({ name, ...rest }) => (
                <div
                  key={name}
                  onClick={(e) => e.stopPropagation()}
                  className={classes.creatorSelect}
                >
                  <Field
                    name={`${id}${count}${name}`}
                    render={({ field, form }: FieldProps<any>) => (
                      <Select {...rest} field={field} form={form} />
                    )}
                  />
                </div>
              ))}
            </div>
          )}
        </div>
      )}
      <div
        id={createCardId(id, index)}
        ref={ref}
        className={classes.card}
        style={createStyle(
          customizations,
          active ? { border: `1px solid grey` } : {}
        )}
        onClick={() => {
          toggleActive();
        }}
      >
        {text}
      </div>
    </div>
    // </ClickAwayListener>
  );
};

export default Card;
