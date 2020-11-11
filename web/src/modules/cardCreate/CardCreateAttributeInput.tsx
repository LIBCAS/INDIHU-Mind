import React, { useEffect } from "react";

import { AttributeProps } from "../../types/attribute";
import { InputText } from "../../components/form/InputText";
import { Switch } from "../../components/form/Switch";
import { DateTimePicker } from "../../components/form/DateTimePicker";
import { GPSPicker } from "../../components/form/GPSPicker";
import { CardCreateAttributeLabel } from "./CardCreateAttributeLabel";
import { AttributeType } from "../../enums";

interface CardCreateAttributeInputProps {
  transformedField: any;
  form: any;
  formikBag: any;
  attribute: AttributeProps;
  setPopoverOpen: Function;
  formValue: any;
}

export const CardCreateAttributeInput: React.FC<CardCreateAttributeInputProps> = ({
  transformedField,
  form,
  formikBag,
  attribute,
  setPopoverOpen,
  formValue,
}) => {
  const { type } = attribute;
  useEffect(() => {
    form.setFieldValue(transformedField.name, formValue);
  }, [formValue, transformedField.name]); // eslint-disable-line
  return (
    <>
      {(() => {
        switch (type) {
          case AttributeType.BOOLEAN:
            return (
              <Switch
                field={transformedField}
                form={form}
                title={
                  <CardCreateAttributeLabel
                    setPopoverOpen={setPopoverOpen}
                    attribute={attribute}
                    formikBag={formikBag}
                  />
                }
                label={formValue ? "Ano" : "Ne"}
              />
            );
          case AttributeType.DATE:
          case AttributeType.DATETIME:
            return (
              <DateTimePicker
                field={transformedField}
                form={form}
                onChange={transformedField.onChange}
                label={
                  <CardCreateAttributeLabel
                    setPopoverOpen={setPopoverOpen}
                    attribute={attribute}
                    formikBag={formikBag}
                  />
                }
                dateOnly={type === AttributeType.DATE}
              />
            );
          case AttributeType.GEOLOCATION:
            return (
              <GPSPicker
                field={transformedField}
                form={form}
                label={
                  <CardCreateAttributeLabel
                    setPopoverOpen={setPopoverOpen}
                    attribute={attribute}
                    formikBag={formikBag}
                  />
                }
              />
            );
          default:
            return (
              <InputText
                field={transformedField}
                form={form}
                label={
                  <CardCreateAttributeLabel
                    setPopoverOpen={setPopoverOpen}
                    attribute={attribute}
                    formikBag={formikBag}
                  />
                }
                type={type === AttributeType.DOUBLE ? "number" : "text"}
                multiline={type === AttributeType.STRING}
                inputProps={{
                  rows: type === AttributeType.STRING ? 4 : undefined,
                }}
              />
            );
        }
      })()}
    </>
  );
};
