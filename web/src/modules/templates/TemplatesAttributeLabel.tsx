import React from "react";
import Edit from "@material-ui/icons/Edit";
import Delete from "@material-ui/icons/Delete";
import classNames from "classnames";
import Tooltip from "@material-ui/core/Tooltip";
import IconButton from "@material-ui/core/IconButton";

import { Popconfirm } from "../../components/portal/Popconfirm";
import { CardTemplateAttribute } from "../../types/cardTemplate";

import { useStyles as useTextStyles } from "../../theme/styles/textStyles";

import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useEffectStyles } from "../../theme/styles/effectStyles";

import { deleteAttribute } from "./_utils";

interface TemplatesAttributeLabelProps {
  attribute: CardTemplateAttribute;
  formikBag: any;
  setPopoverOpen: Function;
}

export const TemplatesAttributeLabel: React.FC<
  TemplatesAttributeLabelProps
> = ({ attribute, formikBag, setPopoverOpen }) => {
  const classesText = useTextStyles();
  const classesSpacing = useSpacingStyles();
  const classesLayout = useLayoutStyles();
  const classesEffect = useEffectStyles();

  const onDelete = () => {
    deleteAttribute(formikBag, attribute);
  };

  return (
    <div
      className={classNames(
        classesLayout.flex,
        classesLayout.alignCenter,
        classesText.textGrey,
        classesSpacing.mt1
      )}
    >
      {attribute.name}
      <div
        className={classNames(
          classesLayout.flex,
          classesLayout.alignCenter,
          classesSpacing.mlAuto
        )}
      >
        <Popconfirm
          confirmText="Opravdu chcete smazat tento atribut?"
          onConfirmClick={onDelete}
          Button={() => (
            <Tooltip title="Smazat">
              <IconButton className={classNames(classesEffect.hoverSecondary)}>
                <Delete />
              </IconButton>
            </Tooltip>
          )}
        />
        <Tooltip title="Editovat">
          <IconButton
            onClick={() => setPopoverOpen(true)}
            className={classNames(classesEffect.hoverPrimary)}
          >
            <Edit fontSize="small" />
          </IconButton>
        </Tooltip>
      </div>
    </div>
  );
};
