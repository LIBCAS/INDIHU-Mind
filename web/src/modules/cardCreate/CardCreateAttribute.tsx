import React, { useRef, useState, useEffect } from "react";
import { Field } from "formik";

import { AttributeProps } from "../../types/attribute";
import { notEmpty } from "../../utils/form/validate";
import { Popover } from "../../components/portal/Popover";

import { useStyles } from "./_cardCreateStyles";
import { CardCreateAddAttribute } from "./CardCreateAddAttribute";
import { CardCreateAttributeInput } from "./CardCreateAttributeInput";

interface CardCreateAttributeProps {
  attribute: AttributeProps;
  formikBag: any;
}

export const CardCreateAttribute: React.FC<CardCreateAttributeProps> = ({
  attribute,
  formikBag
}) => {
  const classes = useStyles();
  const anchorRef = useRef(null);
  const [popoverOpen, setPopoverOpen] = useState(false);
  const [formValue, setFormValue] = useState<any>(null);
  const { id, type } = attribute;

  useEffect(() => {
    const parentAttribute = formikBag.values.attributes.find(
      (att: AttributeProps) => att.id === id
    );
    setFormValue(parentAttribute.value as string | boolean | number);
  }, [formikBag.values.attributes]);

  // change formik array field attributes
  const onChange = (e: any, field: any) => {
    const { value, checked } = e.target;
    field.onChange(e);
    const formValues = formikBag.values.attributes.map(
      (att: AttributeProps) => {
        if (att.id === id) {
          switch (type) {
            case "BOOLEAN":
              att.value = Boolean(checked);
              break;
            case "DOUBLE":
              att.value = Number(value);
              break;
            case "DATETIME":
              att.value = value;
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
      <div ref={anchorRef} className={classes.atributeFieldwrapper}>
        <Field
          name={id}
          value={formValue}
          validate={(value: any) => {
            let error;
            const notEmptyTypes = ["STRING", "DOUBLE"];
            if (notEmptyTypes.indexOf(type) !== -1) {
              error = notEmpty(value);
            }
            return error;
          }}
          render={({ field, form }: any) => {
            const transformedField = {
              ...field,
              onChange: (e: any) =>
                type === "DATETIME"
                  ? onChange({ target: { value: e.toDate() } }, field)
                  : onChange(e, field),
              value: formValue,
              checked: formValue
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
