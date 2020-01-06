import React, { useRef, useState } from "react";
import { useDrag, useDrop, DropTargetMonitor } from "react-dnd";
import { ItemTypes, Item } from "./_types";
import { XYCoord } from "dnd-core";
import { useStyles } from "./_dndStyles";
import { TextField, ClickAwayListener } from "@material-ui/core";
import classNames from "classnames";
import { get } from "lodash";
import Input from "@material-ui/core/Input";
import InputLabel from "@material-ui/core/InputLabel";
import MenuItem from "@material-ui/core/MenuItem";
import MaterialSelect from "@material-ui/core/Select";
import Typography from "@material-ui/core/Typography";

import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";
import { useStyles as useFormStyles } from "../../../components/form/_formStyles";

import { Field, FieldProps } from "formik";
import { notEmpty } from "../../../utils/form/validate";

export interface CardProps {
  id: any;
  text: string;
  index: number;
  moveCard: (dragIndex: number, hoverIndex: number, item: DragItem) => void;
  setCards: React.Dispatch<React.SetStateAction<Item[]>>;
  formikBag: any;
  count: number;
  recordTemplate: any;
}

export interface DragItem {
  index: number;
  id: string;
  type: string;
}
// CONCAT_COMMA, CONCAT_SPACE
const options = [
  {
    value: "BOLD",
    label: "Tučně"
  },
  {
    value: "ITALIC",
    label: "Kurzíva"
  },
  {
    value: "UPPERCASE",
    label: "Velká písmena"
  }
];

const Card: React.FC<CardProps> = ({
  id,
  text,
  index,
  moveCard,
  setCards,
  formikBag,
  count,
  recordTemplate
}) => {
  const classes = useStyles();
  const classesForm = useFormStyles();
  const classesSpacing = useSpacingStyles();
  const [active, setActive] = useState(false);
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

      // Determine rectangle on screen
      const hoverBoundingRect = ref.current
        ? ref.current.getBoundingClientRect()
        : { bottom: 0, top: 0, left: 0, right: 0 };

      // Get top
      const hoverTopY = hoverBoundingRect.top;
      // Get left
      const hoverLeftX = hoverBoundingRect.left;

      // Determine mouse position
      const clientOffset = monitor.getClientOffset();

      // Get pixels to the top
      const hoverClientY = (clientOffset as XYCoord).y - hoverBoundingRect.top;
      // Get pixels to the left
      const hoverClientX = (clientOffset as XYCoord).x - hoverBoundingRect.left;

      // Only perform the move when the mouse has crossed half of the items height
      // When dragging downwards, only move when the cursor is below 50%
      // When dragging upwards, only move when the cursor is above 50%

      // Dragging downwards
      if (
        dragIndex < hoverIndex &&
        hoverClientY < hoverTopY &&
        hoverClientX >= hoverLeftX
      ) {
        return;
      }

      // Dragging upwards
      if (
        dragIndex > hoverIndex &&
        hoverClientY > hoverTopY &&
        hoverClientX <= hoverLeftX
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
    }
  });

  const [{ isDragging }, drag] = useDrag({
    item: { type: ItemTypes.CARD, id, index },
    collect: (monitor: any) => ({
      isDragging: monitor.isDragging()
    })
  });

  const opacity = isDragging ? 0 : 1;
  drag(drop(ref));

  const handleClickAway = () => {
    // setActive(false);
  };
  return (
    <>
      {id === "customizations" ? (
        <Field
          name={id + count}
          validate={notEmpty}
          render={({ field, form }: FieldProps<any>) => (
            <>
              <div
                ref={ref}
                style={{
                  width:
                    get(field, "value.length") > 3
                      ? `${get(field, "value.length") / 1.3}rem`
                      : "3rem",
                  marginRight: ".5rem",
                  marginBottom: ".5rem",
                  opacity
                }}
              >
                <TextField
                  type="text"
                  InputProps={{
                    autoComplete: "off",
                    disableUnderline: true
                  }}
                  autoFocus={recordTemplate ? false : true}
                  inputProps={{
                    className: classNames(classesForm.default, {
                      [classesForm.errorBorder]:
                        form.touched[field.name] && form.errors[field.name]
                    })
                  }}
                  {...field}
                />
              </div>
              {form.touched[field.name] && form.errors[field.name] && (
                <Typography
                  style={{
                    display: "flex",
                    alignItems: "center",
                    marginBottom: "0.5rem",
                    marginRight: "0.5rem"
                  }}
                  color="error"
                >
                  {form.touched[field.name] &&
                    form.errors[field.name] &&
                    form.errors[field.name]}
                </Typography>
              )}
            </>
          )}
        />
      ) : (
        <ClickAwayListener onClickAway={handleClickAway}>
          <div style={{ display: "flex" }}>
            <div
              ref={ref}
              className={classes.card}
              style={{
                opacity,
                ...(active && { border: `2px solid grey` }),
                ...(get(
                  formikBag,
                  `values.${id + count}customizations`,
                  []
                ).includes("BOLD") && {
                  fontWeight: 600
                }),
                ...(get(
                  formikBag,
                  `values.${id + count}customizations`,
                  []
                ).includes("ITALIC") && {
                  fontStyle: "italic"
                }),
                ...(get(
                  formikBag,
                  `values.${id + count}customizations`,
                  []
                ).includes("UPPERCASE") && {
                  textTransform: "uppercase"
                })
              }}
              onClick={() => {
                setActive(prev => !prev);
              }}
            >
              {`${text} ${get(formikBag, `values.${id + count}code`, "")}`}
            </div>
            <div
              style={{
                display:
                  active ||
                  (get(formikBag, `errors.${id + count}code`, false) &&
                    get(formikBag, `touched.${id + count}code`, false))
                    ? "flex"
                    : "none",
                alignItems: "center"
              }}
            >
              <Field
                name={id + count + "code"}
                validate={notEmpty}
                render={({ field, form }: FieldProps<any>) => (
                  <>
                    <TextField
                      type="text"
                      InputProps={{
                        autoComplete: "off",
                        disableUnderline: true
                      }}
                      style={{
                        marginRight: ".5rem",
                        marginBottom: "0.5rem",
                        width: "3.5rem"
                      }}
                      autoFocus={!formikBag.initialValues}
                      placeholder="Code"
                      inputProps={{
                        className: classNames(
                          classesForm.default,
                          classesForm.active,
                          {
                            [classesForm.errorBorder]:
                              form.touched[field.name] &&
                              form.errors[field.name]
                          }
                        )
                      }}
                      {...field}
                    />
                    {form.touched[field.name] && form.errors[field.name] && (
                      <Typography
                        style={{
                          marginRight: ".5rem",
                          marginBottom: "0.5rem"
                        }}
                        color="error"
                      >
                        {form.touched[field.name] &&
                          form.errors[field.name] &&
                          form.errors[field.name]}
                      </Typography>
                    )}
                  </>
                )}
              />
              <Field
                name={id + count + "customizations"}
                render={({ field, form }: FieldProps<any>) => (
                  <MaterialSelect
                    displayEmpty
                    multiple
                    {...field}
                    input={
                      <Input
                        disableUnderline
                        style={{
                          marginRight: ".5rem",
                          marginBottom: "0.5rem"
                        }}
                        placeholder="Vyberte"
                        inputProps={{
                          className: classNames(
                            classesForm.default,
                            classesForm.select,
                            classesForm.active
                          )
                        }}
                      />
                    }
                  >
                    {options.map((opt, i) => (
                      <MenuItem key={i} value={opt.value}>
                        {opt.label}
                      </MenuItem>
                    ))}
                  </MaterialSelect>
                )}
              />
            </div>
          </div>
        </ClickAwayListener>
      )}
    </>
  );
};

export default Card;
