import IconButton from "@material-ui/core/IconButton";
import Typography from "@material-ui/core/Typography";
import ArrowLeft from "@material-ui/icons/ArrowLeft";
import ArrowRight from "@material-ui/icons/ArrowRight";
import { Field, FormikProps } from "formik";
import React, { useRef, useState } from "react";
import { Popover } from "../../components/portal/Popover";
import { CardTemplateAttribute } from "../../types/cardTemplate";
import { getAttributeTypeLabel } from "../../utils/attribute";
import { TemplatesAddAttribute } from "./TemplatesAddAttribute";
import { TemplatesAttributeLabel } from "./TemplatesAttributeLabel";
import { TemplatesFormValues } from "./TemplatesForm";
import { useStyles } from "./_templatesStyles";

interface TemplatesAttributeProps {
  attribute: CardTemplateAttribute;
  formikBag: FormikProps<TemplatesFormValues>;
  index: number;
}

export const TemplatesAttribute: React.FC<TemplatesAttributeProps> = ({
  attribute,
  formikBag,
  index,
}) => {
  const classes = useStyles();
  const anchorRef = useRef(null);
  const [popoverOpen, setPopoverOpen] = useState(false);

  const { id, type } = attribute;

  const moveAttribute = (offset: number) => {
    const currentOrdinalNumber = attribute.ordinalNumber;
    const modifiedAttribute = {
      ...attribute,
      ordinalNumber: currentOrdinalNumber + offset,
    };
    const neighbor = {
      ...formikBag.values.attributeTemplates[index + offset],
      ordinalNumber: currentOrdinalNumber,
    };

    const newAttributeTemplates = formikBag.values.attributeTemplates;
    newAttributeTemplates[index] = neighbor;
    newAttributeTemplates[index + offset] = modifiedAttribute;

    formikBag.setFieldValue("attributeTemplates", newAttributeTemplates);
  };

  return (
    <>
      <div ref={anchorRef} className={classes.atributeFieldwrapper}>
        <Field
          name={id}
          render={() => {
            return (
              <>
                <TemplatesAttributeLabel
                  setPopoverOpen={setPopoverOpen}
                  attribute={attribute}
                  formikBag={formikBag}
                />
                <div className={classes.attributeLowerHalfWrapper}>
                  <Typography>Typ: {getAttributeTypeLabel(type)}</Typography>
                  <div>
                    <IconButton
                      size="small"
                      onClick={() => moveAttribute(-1)}
                      disabled={index === 0}
                    >
                      <ArrowLeft />
                    </IconButton>
                    <IconButton
                      size="small"
                      onClick={() => moveAttribute(1)}
                      disabled={
                        index === formikBag.values.attributeTemplates.length - 1
                      }
                    >
                      <ArrowRight />
                    </IconButton>
                  </div>
                </div>
              </>
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
          <TemplatesAddAttribute
            formikBagParent={formikBag}
            setOpen={setPopoverOpen}
            previousAttribute={attribute}
          />
        }
      />
    </>
  );
};
