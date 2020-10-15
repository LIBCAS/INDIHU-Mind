import React, { useEffect } from "react";
import { Field, FieldProps } from "formik";
import classNames from "classnames";

import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";
import { InputText } from "../../../components/form/InputText";
import { Switch } from "../../../components/form/Switch";
import { DateTimePicker } from "../../../components/form/DateTimePicker";
import { AttributeProps } from "../../../types/attribute";
import { notEmpty } from "../../../utils/form/validate";

interface CardDetailContentAddAttributeFormValueProps {
  formikBag: any;
  previousAttribute?: AttributeProps;
}

export const CardDetailContentAddAttributeFormValue: React.FC<
  CardDetailContentAddAttributeFormValueProps
> = ({ formikBag, previousAttribute }) => {
  const type = formikBag.values.type;
  const classesSpacing = useSpacingStyles();
  useEffect(() => {
    if (previousAttribute) {
      Object.keys(previousAttribute).forEach((key: any) => {
        formikBag.setFieldValue(key, previousAttribute[key]);
      });
    }
  }, [previousAttribute]);
  return (
    <>
      {type !== "" && (
        <Field
          name="value"
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
                    field={field}
                    form={form}
                    label="Hodnota"
                    type={type === "STRING" ? "text" : "number"}
                    multiline={type === "STRING"}
                    inputProps={{
                      rows: type === "STRING" ? 4 : undefined
                    }}
                  />
                );
              case "BOOLEAN":
                return (
                  <Switch
                    field={{
                      ...field,
                      checked: field.value
                    }}
                    form={form}
                    title="Hodnota"
                    label={field.value ? "Ano" : "Ne"}
                  />
                );
              case "DATETIME":
                return (
                  <div className={classNames(classesSpacing.mt2)}>
                    <DateTimePicker field={field} form={form} label="Datum" />
                  </div>
                );
            }
          }}
        />
      )}
    </>
  );
};
