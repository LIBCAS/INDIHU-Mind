import React, { useState, useEffect } from "react";
import { FormikProps, Field, FieldProps } from "formik";
import Button from "@material-ui/core/Button";

import { CardTemplateAttribute } from "../../types/cardTemplate";
import { AttributeTypeEnum } from "../../enums";
import { notEmpty } from "../../utils/form/validate";

import { Formik } from "../../components/form/Formik";
import { Select } from "../../components/form/Select";
import { InputText } from "../../components/form/InputText";

import { useStyles } from "./_templatesStyles";

import { onSubmitAttribute } from "./_utils";
import classNames from "classnames";

interface TemplatesAddAttributeProps {
  formikBagParent: any;
  setOpen: Function;
  previousAttribute?: CardTemplateAttribute;
}

export const TemplatesAddAttribute: React.FC<TemplatesAddAttributeProps> = ({
  formikBagParent,
  setOpen,
  previousAttribute,
}) => {
  const classes = useStyles();

  const [initValues, setInitValues] = useState({
    id: "",
    name: "",
    type: "" as any,
    ordinalNumber: 0,
  });
  useEffect(() => {
    previousAttribute && setInitValues(previousAttribute);
  }, [previousAttribute]);
  return (
    <Formik
      initialValues={initValues}
      enableReinitialize
      onSubmit={(values) =>
        onSubmitAttribute(values, formikBagParent, setOpen, previousAttribute)
      }
      render={(formikBag: FormikProps<CardTemplateAttribute>) => (
        <form
          onReset={formikBag.handleReset}
          onSubmit={(e: any) => {
            e.preventDefault();
            e.stopPropagation();
            if (formikBag.isSubmitting) return false;
            formikBag.submitForm();
          }}
        >
          <div className={classNames(classes.attributeWrapper)}>
            <Field
              name="name"
              validate={notEmpty}
              render={({ field, form }: FieldProps<CardTemplateAttribute>) => (
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
              render={({ field, form }: FieldProps<CardTemplateAttribute>) => (
                <Select
                  field={field}
                  form={form}
                  label="Typ"
                  options={AttributeTypeEnum}
                />
              )}
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
