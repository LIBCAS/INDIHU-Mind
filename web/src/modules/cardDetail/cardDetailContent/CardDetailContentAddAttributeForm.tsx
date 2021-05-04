import Button from "@material-ui/core/Button";
import { Field, FieldProps, FormikProps } from "formik";
import React from "react";
import { Formik } from "../../../components/form/Formik";
import { InputText } from "../../../components/form/InputText";
import { Select } from "../../../components/form/Select";
import { AttributeTypeEnum } from "../../../enums";
import { AttributeProps } from "../../../types/attribute";
import { CardContentProps, CardProps } from "../../../types/card";
import { notEmpty, notLongerThan255 } from "../../../utils/form/validate";
import { CardDetailContentAddAttributeFormValue } from "./CardDetailContentAddAttributeFormValue";
import { useStyles } from "./_cardStyles";
import { onChangeType, onSubmitAttribute } from "./_utils";

const initialValues = {
  id: "",
  name: "",
  type: "" as "",
  value: "",
  ordinalNumber: 0,
};

interface CardDetailContentAddAttributeFormProp {
  card: CardProps;
  setCard: React.Dispatch<React.SetStateAction<CardProps | undefined>>;
  currentCardContent: CardContentProps;
  setCardContents: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
  setOpen: Function;
  previousAttribute?: AttributeProps;
}

export const CardDetailContentAddAttributeForm: React.FC<CardDetailContentAddAttributeFormProp> = ({
  card,
  setCard,
  currentCardContent,
  setCardContents,
  setOpen,
  previousAttribute,
}) => {
  const classes = useStyles();
  return (
    <Formik
      initialValues={initialValues}
      onSubmit={(values) => {
        onSubmitAttribute(
          values,
          card,
          setCard,
          currentCardContent,
          setCardContents,
          setOpen,
          previousAttribute
        );
      }}
      render={(formikBag: FormikProps<AttributeProps>) => (
        <form
          onReset={formikBag.handleReset}
          onSubmit={(e: any) => {
            e.preventDefault();
            e.stopPropagation();
            if (formikBag.isSubmitting) return false;
            formikBag.submitForm();
          }}
        >
          <div className={classes.attributeWrapper}>
            <Field
              name="name"
              validate={(value: any) =>
                notLongerThan255(value) || notEmpty(value)
              }
              render={({ field, form }: FieldProps<AttributeProps>) => (
                <InputText
                  field={field}
                  form={form}
                  label="Název"
                  inputProps={{ autoFocus: false }}
                />
              )}
            />
            <Field
              name="type"
              validate={notEmpty}
              render={({ field, form }: FieldProps<AttributeProps>) => (
                <Select
                  field={field}
                  form={form}
                  onChange={(value: any) => onChangeType(formikBag, value)}
                  label="Typ"
                  options={AttributeTypeEnum}
                />
              )}
            />

            <CardDetailContentAddAttributeFormValue
              formikBag={formikBag}
              previousAttribute={previousAttribute}
            />
            <div className={classes.actionWrapper}>
              <Button size="small" color="primary" type="submit">
                {previousAttribute ? "Změnit atribut" : "Přidat atribut"}
              </Button>
              <Button
                size="small"
                color="inherit"
                onClick={() => setOpen(false)}
              >
                Zrušit
              </Button>
            </div>
          </div>
        </form>
      )}
    />
  );
};
