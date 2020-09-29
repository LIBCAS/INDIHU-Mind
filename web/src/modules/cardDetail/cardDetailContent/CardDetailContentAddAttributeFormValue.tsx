import React, { useEffect } from "react";
import { Field, FieldProps } from "formik";
import classNames from "classnames";

import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";
import { InputText } from "../../../components/form/InputText";
import { Switch } from "../../../components/form/Switch";
import { DateTimePicker } from "../../../components/form/DateTimePicker";
import { GPSPicker } from "../../../components/form/GPSPicker";
import { AttributeProps } from "../../../types/attribute";
import {
  getAttributeTypeLabel,
  validateAttributeType
} from "../../../utils/attribute";
import { AttributeType } from "../../../enums";

interface CardDetailContentAddAttributeFormValueProps {
  formikBag: any;
  previousAttribute?: AttributeProps;
}

export const CardDetailContentAddAttributeFormValue: React.FC<CardDetailContentAddAttributeFormValueProps> = ({
  formikBag,
  previousAttribute
}) => {
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
          validate={validateAttributeType(type)}
          render={({ field, form }: FieldProps<AttributeProps>) => {
            switch (type) {
              case AttributeType.BOOLEAN:
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
                    type={type === AttributeType.STRING ? "number" : "text"}
                    multiline={type === AttributeType.STRING}
                    inputProps={{
                      rows: type === AttributeType.STRING ? 4 : undefined
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
