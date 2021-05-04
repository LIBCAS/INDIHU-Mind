import { IconButton } from "@material-ui/core";
import Button from "@material-ui/core/Button";
import MuiTooltip from "@material-ui/core/Tooltip";
import Typography from "@material-ui/core/Typography";
import ArrowDownward from "@material-ui/icons/ArrowDownward";
import ArrowUpward from "@material-ui/icons/ArrowUpward";
import classNames from "classnames";
import { Field, FieldProps, Form } from "formik";
import React, { useState } from "react";
import { DateTimePicker } from "../../../components/form/DateTimePicker";
import { Formik } from "../../../components/form/Formik";
import { GPSPicker } from "../../../components/form/GPSPicker";
import { InputText } from "../../../components/form/InputText";
import { Switch } from "../../../components/form/Switch";
import { Popconfirm } from "../../../components/portal/Popconfirm";
import { AttributeType } from "../../../enums";
import { useStyles as useLayoutStyles } from "../../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";
import { useStyles as useTextStyles } from "../../../theme/styles/textStyles";
import { AttributeProps } from "../../../types/attribute";
import { CardContentProps, CardProps } from "../../../types/card";
import { validateAttributeType } from "../../../utils/attribute";
import { parseAttribute } from "../../../utils/card";
import { useArrowStyles, useStyles } from "./_cardStyles";
import { onDeleteAttribute, onEditCard, onSubmitAttribute } from "./_utils";

interface CardDetailContentAttributeProps {
  card: CardProps;
  setCard: React.Dispatch<React.SetStateAction<CardProps | undefined>>;
  currentCardContent: CardContentProps;
  setCardContents: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
  attribute: AttributeProps;
  attributeIndex: number;
  attributes: AttributeProps[];
  disabled?: boolean;
}

interface FormValues {
  attributeValue: any;
}

export const CardDetailContentAttribute: React.FC<CardDetailContentAttributeProps> = ({
  attribute,
  card,
  setCard,
  currentCardContent,
  setCardContents,
  attributeIndex,
  attributes,
  disabled,
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
    onDeleteAttribute(
      card,
      setCard,
      currentCardContent,
      setCardContents,
      () => {},
      attribute
    );
  };

  const onSubmitValue = (values: FormValues) => {
    const att: AttributeProps = {
      ...attribute,
      value: values.attributeValue,
    };
    onSubmitAttribute(
      att,
      card,
      setCard,
      currentCardContent,
      setCardContents,
      () => {},
      attribute
    );
    setEditValue(false);
  };

  const onMoveAttribute = (offset: number) => {
    let attributesNewOrder = attributes;
    const attributeToSwapWith = attributesNewOrder[attributeIndex + offset];

    //swap items for FE
    attributesNewOrder[attributeIndex + offset] = attribute;
    attributesNewOrder[attributeIndex] = attributeToSwapWith;
    //swap ordinalNumbers for BE
    attributesNewOrder[attributeIndex + offset].ordinalNumber =
      attributeToSwapWith.ordinalNumber;
    attributesNewOrder[attributeIndex].ordinalNumber = attribute.ordinalNumber;

    onEditCard(
      "attributes",
      attributesNewOrder,
      card,
      setCard,
      currentCardContent,
      setCardContents
    );
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
        open={!disabled && tooltipOpen && !editValue}
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
          onClick={() =>
            !disabled && currentCardContent.lastVersion
              ? setEditValue(true)
              : undefined
          }
        >
          <Typography
            className={classNames(classesSpacing.mr1, {
              [classesText.cursor]:
                !editValue && currentCardContent.lastVersion,
            })}
            component="span"
            variant="subtitle1"
            onClick={() =>
              !disabled && currentCardContent.lastVersion
                ? setEditValue(true)
                : undefined
            }
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
                [classesText.cursor]:
                  !editValue && currentCardContent.lastVersion,
              })}
              variant="body2"
              component="span"
            >
              {parseAttribute(attribute, () =>
                !disabled && currentCardContent.lastVersion
                  ? setEditValue(true)
                  : undefined
              )}
            </Typography>
          )}
        </div>
      </MuiTooltip>
    </div>
  );
};
