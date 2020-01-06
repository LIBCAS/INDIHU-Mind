import React from "react";
import Edit from "@material-ui/icons/Edit";
import Delete from "@material-ui/icons/Delete";
import classNames from "classnames";
import Tooltip from "@material-ui/core/Tooltip";
import IconButton from "@material-ui/core/IconButton";

import { AttributeProps } from "../../types/attribute";
import { Popconfirm } from "../../components/portal/Popconfirm";

import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useEffectStyles } from "../../theme/styles/effectStyles";

import { deleteAttribute } from "./_utils";

interface CardCreateAttributeProps {
  attribute: AttributeProps;
  formikBag: any;
  setPopoverOpen: Function;
}

export const CardCreateAttributeLabel: React.FC<CardCreateAttributeProps> = ({
  attribute,
  formikBag,
  setPopoverOpen
}) => {
  const classesSpacing = useSpacingStyles();
  const classesLayout = useLayoutStyles();
  const classesEffect = useEffectStyles();

  const onDelete = () => {
    deleteAttribute(formikBag, attribute);
  };

  return (
    <div className={classNames(classesLayout.flex, classesLayout.alignCenter)}>
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
