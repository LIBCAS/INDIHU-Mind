import React, { useEffect, useCallback } from "react";
import { Field, FieldProps } from "formik";
import classNames from "classnames";

import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { InputText } from "../../components/form/InputText";
import { Switch } from "../../components/form/Switch";
import { DateTimePicker } from "../../components/form/DateTimePicker";
import { GPSPicker } from "../../components/form/GPSPicker";
import { AttributeProps } from "../../types/attribute";
import { AttributeType } from "../../enums";
import {
  getAttributeTypeLabel,
  validateAttributeType,
} from "../../utils/attribute";

interface CardCreateAddAttributeValueProps {
  formikBag: any;
  previousAttribute?: AttributeProps;
}

export const CardCreateAddAttributeValue: React.FC<CardCreateAddAttributeValueProps> = ({
  formikBag,
  previousAttribute,
}) => {
  const type = formikBag.values.type;
  const classesSpacing = useSpacingStyles();
  const setFieldMemo = useCallback(formikBag.setFieldValue, [
    formikBag.setFieldValue,
  ]);
  useEffect(() => {
    if (previousAttribute) {
      Object.keys(previousAttribute).forEach((key: any) => {
        setFieldMemo(key, previousAttribute[key]);
      });
    }
  }, [previousAttribute, setFieldMemo]);
  return (
    <>
      {type !== "" && (
        <Field
          name="value"
          validate={validateAttributeType(type)}
          render={({ field, form }: FieldProps<AttributeProps>) => {
            switch (type) {
              case AttributeType.BOOLEAN:
                return (
                  <Switch
                    field={{
                      ...field,
                      checked: field.value,
                    }}
                    form={form}
                    title="Hodnota"
                    label={field.value ? "Ano" : "Ne"}
                  />
                );
              case AttributeType.DATE:
              case AttributeType.DATETIME:
                return (
                  <div className={classNames(classesSpacing.mt2)}>
                    <DateTimePicker
                      field={field}
                      form={form}
                      label={getAttributeTypeLabel(type)}
                      dateOnly={type === AttributeType.DATE}
                    />
                  </div>
                );
              case AttributeType.GEOLOCATION:
                return <GPSPicker label="Hodnota" field={field} form={form} />;
              default:
                return (
                  <InputText
                    field={field}
                    form={form}
                    label="Hodnota"
                    type={type === AttributeType.DOUBLE ? "number" : "text"}
                    multiline={type === AttributeType.STRING}
                    inputProps={{
                      rows: type === AttributeType.STRING ? 4 : undefined,
                    }}
                  />
                );
            }
          }}
        />
      )}
    </>
  );
};
