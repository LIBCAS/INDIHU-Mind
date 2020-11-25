import React, { useRef, useState } from "react";
import { Field } from "formik";
import Typography from "@material-ui/core/Typography";

import { CardTemplateAttribute } from "../../types/cardTemplate";
import { Popover } from "../../components/portal/Popover";
import { getAttributeTypeLabel } from "../../utils/attribute";

import { useStyles } from "./_templatesStyles";
import { TemplatesAddAttribute } from "./TemplatesAddAttribute";
import { TemplatesAttributeLabel } from "./TemplatesAttributeLabel";

interface TemplatesAttributeProps {
  attribute: CardTemplateAttribute;
  formikBag: any;
}

export const TemplatesAttribute: React.FC<TemplatesAttributeProps> = ({
  attribute,
  formikBag,
}) => {
  const classes = useStyles();
  const anchorRef = useRef(null);
  const [popoverOpen, setPopoverOpen] = useState(false);

  const { id, type } = attribute;

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
                <Typography>Typ: {getAttributeTypeLabel(type)}</Typography>
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
