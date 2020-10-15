import React, { useState, useContext } from "react";
import Edit from "@material-ui/icons/Edit";
import Delete from "@material-ui/icons/Delete";
import Tooltip from "@material-ui/core/Tooltip";
import classNames from "classnames";
import IconButton from "@material-ui/core/IconButton";

import { GlobalContext } from "../../context/Context";
import { templateGet } from "../../context/actions/template";

import { useStyles as useEffectStyles } from "../../theme/styles/effectStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";

import { CardTemplateProps } from "../../types/cardTemplate";
import { Modal } from "../../components/portal/Modal";
import { Popconfirm } from "../../components/portal/Popconfirm";

import { TemplatesForm } from "./TemplatesForm";
import { useStyles } from "./_templatesStyles";
import { onDeleteTemplate } from "./_utils";

interface TemplatesItemProps {
  template: CardTemplateProps;
}

export const TemplatesItem: React.FC<TemplatesItemProps> = ({ template }) => {
  const classes = useStyles();
  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;
  const classesSpacing = useSpacingStyles();
  const classesEffect = useEffectStyles();
  const [open, setOpen] = useState(false);
  const { owner } = template;
  const handleDelete = (e: React.MouseEvent<HTMLButtonElement, MouseEvent>) => {
    e.stopPropagation();
    onDeleteTemplate(template.id, templateGet, dispatch);
  };
  const handleEdit = () => {
    if (owner) {
      setOpen(true);
    }
  };
  return (
    <>
      <div className={classes.templateItem}>
        <div
          className={classNames(
            classes.templateItemName,
            classesSpacing.mrAuto
          )}
        >
          {template.name}
        </div>
        {owner && (
          <>
            <Popconfirm
              confirmText="Opravdu chcete smazat tuto šablonu?"
              onConfirmClick={handleDelete}
              Button={() => (
                <Tooltip title="Smazat">
                  <IconButton
                    className={classNames(
                      classesEffect.hoverSecondary,
                      classes.templateItemIcon,
                      classesSpacing.p0,
                      classesSpacing.mr1
                    )}
                  >
                    <Delete />
                  </IconButton>
                </Tooltip>
              )}
            />
            <Tooltip title="Editovat">
              <IconButton
                onClick={handleEdit}
                className={classNames(
                  classesEffect.hoverPrimary,
                  classes.templateItemIcon,
                  classesSpacing.p0
                )}
              >
                <Edit color="inherit" />
              </IconButton>
            </Tooltip>
          </>
        )}
      </div>
      <Modal
        open={open}
        setOpen={setOpen}
        content={<TemplatesForm setShowModal={setOpen} template={template} />}
      />
    </>
  );
};
