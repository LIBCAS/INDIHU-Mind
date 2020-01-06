import React, { useEffect } from "react";

import { AttributeProps } from "../../types/attribute";
import { InputText } from "../../components/form/InputText";
import { Switch } from "../../components/form/Switch";
import { DateTimePicker } from "../../components/form/DateTimePicker";
import { CardCreateAttributeLabel } from "./CardCreateAttributeLabel";

interface CardCreateAttributeInputProps {
  transformedField: any;
  form: any;
  formikBag: any;
  attribute: AttributeProps;
  setPopoverOpen: Function;
  formValue: any;
}

export const CardCreateAttributeInput: React.FC<
  CardCreateAttributeInputProps
> = ({
  transformedField,
  form,
  formikBag,
  attribute,
  setPopoverOpen,
  formValue
}) => {
  const { type } = attribute;
  useEffect(() => {
    form.setFieldValue(transformedField.name, formValue);
  }, []);
  return (
    <>
      {(() => {
        switch (type) {
          case "STRING":
          case "DOUBLE":
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
          case "DATETIME":
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
              />
            );
        }
      })()}
    </>
  );
};
