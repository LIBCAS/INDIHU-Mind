import React from "react";
import { FormikProps, Field, FieldProps } from "formik";
import Button from "@material-ui/core/Button";

import { AttributeProps, AttributeTypeProps } from "../../types/attribute";
import { notEmpty } from "../../utils/form/validate";

import { Formik } from "../../components/form/Formik";
import { Select } from "../../components/form/Select";
import { InputText } from "../../components/form/InputText";

import { useStyles } from "./_cardCreateStyles";
import { CardCreateAddAttributeValue } from "./CardCreateAddAttributeValue";
import { onSubmitAttribute, onChangeType } from "./_utils";

const initialValues = {
  id: "",
  name: "",
  type: "" as "",
  value: "",
  ordinalNumber: 0
};

const types: { value: AttributeTypeProps; label: string }[] = [
  { value: "STRING", label: "Text" },
  { value: "DOUBLE", label: "Číslo" },
  { value: "BOOLEAN", label: "Boolean" },
  { value: "DATETIME", label: "Datum" }
];

interface CardCreateAddAttributeProp {
  formikBagParent: any;
  setOpen: Function;
  previousAttribute?: AttributeProps;
}

export const CardCreateAddAttribute: React.FC<CardCreateAddAttributeProp> = ({
  formikBagParent,
  setOpen,
  previousAttribute
}) => {
  const classes = useStyles();
  return (
    <Formik
      initialValues={initialValues}
      onSubmit={values =>
        onSubmitAttribute(values, formikBagParent, setOpen, previousAttribute)
      }
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
              validate={notEmpty}
              render={({ field, form }: FieldProps<AttributeProps>) => (
                <InputText
                  field={field}
                  form={form}
                  label="Název"
                  inputProps={{ autoFocus: true }}
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
                  onChange={(e: any) => onChangeType(formikBag, e)}
                  label="Typ"
                  options={types}
                />
              )}
            />

            <CardCreateAddAttributeValue
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
