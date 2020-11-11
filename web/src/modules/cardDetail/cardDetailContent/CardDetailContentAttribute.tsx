import React, { useState } from "react";
import Typography from "@material-ui/core/Typography";
import { FieldProps, Field, Form } from "formik";
import Button from "@material-ui/core/Button";
import MuiTooltip from "@material-ui/core/Tooltip";
import classNames from "classnames";

import { Popconfirm } from "../../../components/portal/Popconfirm";
import { Formik } from "../../../components/form/Formik";
import { InputText } from "../../../components/form/InputText";
import { Switch } from "../../../components/form/Switch";
import { DateTimePicker } from "../../../components/form/DateTimePicker";
import { GPSPicker } from "../../../components/form/GPSPicker";

import { CardContentProps } from "../../../types/card";
import { AttributeProps } from "../../../types/attribute";

import { parseAttribute } from "../../../utils/card";

import { onSubmitAttribute, onDeleteAttribute, onEditCard } from "./_utils";
import { useStyles } from "./_cardStyles";
import { useStyles as useTextStyles } from "../../../theme/styles/textStyles";
import { useStyles as useLayoutStyles } from "../../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";
import { useArrowStyles } from "./_cardStyles";
import { AttributeType } from "../../../enums";
import { validateAttributeType } from "../../../utils/attribute";

import ArrowUpward from "@material-ui/icons/ArrowUpward";
import ArrowDownward from "@material-ui/icons/ArrowDownward";
import { IconButton } from "@material-ui/core";

interface CardDetailContentAttributeProps {
  card: CardContentProps;
  cardContent: CardContentProps[] | undefined;
  setCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
  attribute: AttributeProps;
  attributeIndex: number;
  attributes: AttributeProps[];
}

interface FormValues {
  attributeValue: any;
}

export const CardDetailContentAttribute: React.FC<CardDetailContentAttributeProps> = ({
  attribute,
  card,
  setCardContent,
  attributeIndex,
  attributes,
}) => {
  const classesText = useTextStyles();

  const classesLayout = useLayoutStyles();

  const classesSpacing = useSpacingStyles();

  const classes = useStyles();

  const arrowClasses = useArrowStyles();

  const [editValue, setEditValue] = useState(false);
  const [tooltipOpen, setTooltipOpen] = useState(false);

  const { type } = attribute;

  const onRemoveAttribute = () => {
    onDeleteAttribute(card, setCardContent, () => {}, attribute);
  };

  const onSubmitValue = (values: FormValues) => {
    const att: AttributeProps = {
      ...attribute,
      value: values.attributeValue,
    };
    onSubmitAttribute(att, card, setCardContent, () => {}, attribute);
    setEditValue(false);
  };

  const onMoveAttribute = (offset: number) => {
    let attributesNewOrder = attributes;
    const attributeToSwapWith = attributesNewOrder[attributeIndex + offset];

    attributesNewOrder[attributeIndex + offset] = attribute;
    attributesNewOrder[attributeIndex] = attributeToSwapWith;

    onEditCard("attributes", attributesNewOrder, card, setCardContent);
  };

  return (
    <div key={attribute.id} className={classesLayout.flex}>
      <div
        className={classNames(
          classesLayout.flex,
          classesLayout.directionColumn,
          classesLayout.justifyCenter,
          classes.arrowsWrapper
        )}
      >
        <IconButton
          size="small"
          disabled={attributeIndex === 0}
          classes={arrowClasses}
          onClick={() => onMoveAttribute(-1)}
        >
          <ArrowUpward />
        </IconButton>
        <IconButton
          size="small"
          disabled={attributeIndex + 1 === attributes.length}
          classes={arrowClasses}
          onClick={() => onMoveAttribute(1)}
        >
          <ArrowDownward />
        </IconButton>
      </div>
      <MuiTooltip
        title="Kliknutím můžete editovat atribut."
        enterDelay={500}
        enterNextDelay={500}
        leaveDelay={250}
        arrow={true}
        placement="right"
        open={tooltipOpen && !editValue}
        onOpen={() => setTooltipOpen(!editValue)}
        onClose={() => setTooltipOpen(false)}
      >
        <div
          className={classNames(
            classesLayout.flex,
            classesSpacing.mb1,
            classesLayout.directionColumn,
            classesLayout.alignStart,
            classes.attributeItemWrapper,
            !editValue && classes.attributeItemNotEditing
          )}
          onClick={() => (card.lastVersion ? setEditValue(true) : undefined)}
        >
          <Typography
            className={classNames(classesSpacing.mr1, {
              [classesText.cursor]: !editValue && card.lastVersion,
            })}
            component="span"
            variant="subtitle1"
            onClick={() => (card.lastVersion ? setEditValue(true) : undefined)}
          >
            <strong>{attribute.name}</strong>
          </Typography>
          {editValue ? (
            <Formik
              initialValues={{ attributeValue: attribute.value }}
              enableReinitialize
              onSubmit={onSubmitValue}
              render={() => {
                return (
                  <Form>
                    <div className={classesSpacing.mb1}>
                      <Field
                        name="attributeValue"
                        validate={validateAttributeType(type)}
                        render={({
                          field,
                          form,
                        }: FieldProps<AttributeProps>) => {
                          switch (type) {
                            case AttributeType.BOOLEAN:
                              return (
                                <Switch
                                  key={attribute.id}
                                  field={{
                                    ...field,
                                    checked: field.value,
                                  }}
                                  form={form}
                                  label={field.value ? "Ano" : "Ne"}
                                  autoFocus={false}
                                />
                              );
                            case AttributeType.DATE:
                            case AttributeType.DATETIME:
                              return (
                                <div
                                  key={attribute.id}
                                  className={classNames(classesSpacing.mt2)}
                                >
                                  <DateTimePicker
                                    autoFocus={false}
                                    field={field}
                                    form={form}
                                    dateOnly={type === AttributeType.DATE}
                                  />
                                </div>
                              );
                            case AttributeType.GEOLOCATION:
                              return (
                                <GPSPicker
                                  key={attribute.id}
                                  field={field}
                                  form={form}
                                />
                              );
                            default:
                              return (
                                <InputText
                                  key={attribute.id}
                                  field={field}
                                  form={form}
                                  type={
                                    type === AttributeType.DOUBLE
                                      ? "number"
                                      : "text"
                                  }
                                  multiline={type === AttributeType.STRING}
                                  autoFocus={false}
                                  inputProps={{
                                    rows:
                                      type === AttributeType.STRING
                                        ? 4
                                        : undefined,
                                  }}
                                />
                              );
                          }
                        }}
                      />
                      <div className={classesLayout.flex}>
                        <Button
                          className={classNames(
                            classesSpacing.mr2,
                            classesSpacing.mt1
                          )}
                          size="small"
                          color="primary"
                          variant="contained"
                          type="submit"
                        >
                          OK
                        </Button>
                        <Button
                          type="button"
                          size="small"
                          className={classNames(
                            classesSpacing.mr3,
                            classesSpacing.mt1
                          )}
                          onClick={(e) => {
                            e.stopPropagation();
                            setEditValue(false);
                          }}
                          color="secondary"
                          variant="outlined"
                        >
                          Zrušit
                        </Button>
                        <Popconfirm
                          Button={
                            <Button
                              type="button"
                              size="small"
                              className={classesSpacing.mt1}
                              color="secondary"
                              variant="contained"
                            >
                              Smazat atribut
                            </Button>
                          }
                          confirmText="Smazat atribut?"
                          onConfirmClick={(e: any) => {
                            e.stopPropagation();
                            onRemoveAttribute();
                          }}
                        />
                      </div>
                    </div>
                  </Form>
                );
              }}
            />
          ) : (
            <Typography
              className={classNames({
                [classesText.cursor]: !editValue && card.lastVersion,
              })}
              variant="body2"
              component="span"
            >
              {parseAttribute(attribute, () =>
                card.lastVersion ? setEditValue(true) : undefined
              )}
            </Typography>
          )}
        </div>
      </MuiTooltip>
    </div>
  );
};
