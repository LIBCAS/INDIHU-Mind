import React, { useRef, useState, useEffect } from "react";
import { Field } from "formik";

import { AttributeProps } from "../../types/attribute";
import { Popover } from "../../components/portal/Popover";

import { CardCreateAddAttribute } from "./CardCreateAddAttribute";
import { CardCreateAttributeInput } from "./CardCreateAttributeInput";
import { AttributeType } from "../../enums";
import { validateAttributeType } from "../../utils/attribute";

interface CardCreateAttributeProps {
  attribute: AttributeProps;
  formikBag: any;
}

export const CardCreateAttribute: React.FC<CardCreateAttributeProps> = ({
  attribute,
  formikBag,
}) => {
  const anchorRef = useRef(null);
  const [popoverOpen, setPopoverOpen] = useState(false);
  const [formValue, setFormValue] = useState<any>(null);
  const { id, type } = attribute;

  useEffect(() => {
    const parentAttribute = formikBag.values.attributes.find(
      (att: AttributeProps) => att.id === id
    );
    setFormValue(parentAttribute.value as string | boolean | number);
  }, [formikBag.values.attributes, id]);

  // change formik array field attributes
  const onChange = (e: any, field: any) => {
    const { value, checked } = e.target;
    field.onChange(e);
    const formValues = formikBag.values.attributes.map(
      (att: AttributeProps) => {
        if (att.id === id) {
          switch (type) {
            case AttributeType.BOOLEAN:
              att.value = Boolean(checked);
              break;
            case AttributeType.DOUBLE:
              att.value = Number(value);
              break;
            default:
              att.value = value;
          }
        }
        return att;
      }
    );
    formikBag.setFieldValue("attributes", formValues);
  };

  return (
    <>
      <div ref={anchorRef}>
        <Field
          name={id}
          value={formValue}
          validate={!formValue && validateAttributeType(type)}
          render={({ field, form }: any) => {
            const transformedField = {
              ...field,
              onChange: (e: any) =>
                type === AttributeType.DATE || type === AttributeType.DATETIME
                  ? onChange({ target: { value: e.toDate() } }, field)
                  : type === AttributeType.GEOLOCATION
                  ? onChange({ target: { value: e } }, field)
                  : onChange(e, field),
              value: formValue,
              checked: formValue,
            };
            return (
              <CardCreateAttributeInput
                transformedField={transformedField}
                form={form}
                formikBag={formikBag}
                attribute={attribute}
                setPopoverOpen={setPopoverOpen}
                formValue={formValue}
              />
            );
          }}
        />
      </div>
      <Popover
        open={popoverOpen}
        setOpen={setPopoverOpen}
        anchorEl={anchorRef.current}
        overflowVisible={true}
        content={
          <CardCreateAddAttribute
            formikBagParent={formikBag}
            setOpen={setPopoverOpen}
            previousAttribute={attribute}
          />
        }
      />
    </>
  );
};
