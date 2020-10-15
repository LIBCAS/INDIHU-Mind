import React, { useState } from "react";
import Typography from "@material-ui/core/Typography";
import { FieldProps, Field, Form } from "formik";
import Button from "@material-ui/core/Button";
import classNames from "classnames";

import { Popconfirm } from "../../../components/portal/Popconfirm";
import { Formik } from "../../../components/form/Formik";
import { InputText } from "../../../components/form/InputText";
import { Switch } from "../../../components/form/Switch";
import { DateTimePicker } from "../../../components/form/DateTimePicker";

import { notEmpty } from "../../../utils/form/validate";
import { CardContentProps } from "../../../types/card";
import { AttributeProps } from "../../../types/attribute";

import { parseAttribute } from "../../../utils/card";

import { onSubmitAttribute, onDeleteAttribute } from "./_utils";
import { useStyles as useTextStyles } from "../../../theme/styles/textStyles";
import { useStyles as useLayoutStyles } from "../../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";

interface CardDetailContentAttributeProps {
  card: CardContentProps;
  cardContent: CardContentProps[] | undefined;
  setCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
  attribute: AttributeProps;
}

interface FormValues {
  attributeValue: any;
}

export const CardDetailContentAttribute: React.FC<
  CardDetailContentAttributeProps
> = ({ attribute, card, setCardContent }) => {
  const classesText = useTextStyles();
  const classesLayout = useLayoutStyles();
  const classesSpacing = useSpacingStyles();
  const [editValue, setEditValue] = useState(false);
  const { type } = attribute;
  const onRemoveAttribute = () => {
    onDeleteAttribute(card, setCardContent, () => {}, attribute);
  };
  const onSubmitValue = (values: FormValues) => {
    const att: AttributeProps = {
      ...attribute,
      value: values.attributeValue
    };
    onSubmitAttribute(att, card, setCardContent, () => {}, attribute);
    setEditValue(false);
  };
  return (
    <div
      key={attribute.id}
      className={classNames(
        classesLayout.flex,
        classesSpacing.mb1,
        classesLayout.directionColumn,
        classesLayout.alignStart
      )}
    >
      <Typography
        className={classNames(classesText.textBold, classesSpacing.mr1, {
          [classesText.cursor]: !editValue && card.lastVersion
        })}
        component="span"
        onClick={() => (card.lastVersion ? setEditValue(true) : undefined)}
      >
        {attribute.name}
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
                    validate={(value: any) => {
                      let error;
                      const notEmptyTypes = ["STRING", "DOUBLE"];
                      if (notEmptyTypes.indexOf(type) !== -1) {
                        error = notEmpty(value);
                      }
                      return error;
                    }}
                    render={({ field, form }: FieldProps<AttributeProps>) => {
                      switch (type) {
                        case "STRING":
                        case "DOUBLE":
                          return (
                            <InputText
                              key={attribute.id}
                              field={field}
                              form={form}
                              type={type === "STRING" ? "text" : "number"}
                              multiline={type === "STRING"}
                              autoFocus
                              inputProps={{
                                rows: type === "STRING" ? 4 : undefined
                              }}
                            />
                          );
                        case "BOOLEAN":
                          return (
                            <Switch
                              key={attribute.id}
                              field={{
                                ...field,
                                checked: field.value
                              }}
                              form={form}
                              label={field.value ? "Ano" : "Ne"}
                              autoFocus
                            />
                          );
                        case "DATETIME":
                          return (
                            <div
                              key={attribute.id}
                              className={classNames(classesSpacing.mt2)}
                            >
                              <DateTimePicker
                                // onAccept={() => {
                                //   console.log("ACCEPT")
                                //   if (formikBag.isSubmitting) return;
                                //   formikBag.submitForm();
                                // }}
                                // onClose={() => {
                                //   console.log("CLOSED")
                                //   setEditValue(false);
                                // }}
                                autoFocus={true}
                                field={field}
                                form={form}
                              />
                            </div>
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
                      onClick={e => {
                        e.stopPropagation();
                        setEditValue(false);
                      }}
                      color="secondary"
                      variant="outlined"
                    >
                      Zrušit
                    </Button>
                    <Popconfirm
                      Button={() => (
                        <Button
                          type="button"
                          size="small"
                          className={classesSpacing.mt1}
                          color="secondary"
                          variant="contained"
                        >
                          Smazat atribut
                        </Button>
                      )}
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
            [classesText.cursor]: !editValue && card.lastVersion
          })}
          onClick={() => (card.lastVersion ? setEditValue(true) : undefined)}
          component="span"
        >
          {parseAttribute(attribute)}
        </Typography>
      )}
    </div>
  );
};
