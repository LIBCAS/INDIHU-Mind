import React, { useState, useContext } from "react";
import AddCircle from "@material-ui/icons/AddCircle";
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
import { CardCreateRoot } from "../cardCreate/CardCreateRoot";

import { TemplatesForm } from "./TemplatesForm";
import { useStyles } from "./_templatesStyles";
import { onDeleteTemplate } from "./_utils";
import Paper from "@material-ui/core/Paper";

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
  const [cardCreateOpen, setCardCreateOpen] = useState(false);
  const { owner, attributeTemplates } = template;
  const handleDelete = (e: React.MouseEvent<HTMLButtonElement, MouseEvent>) => {
    e.stopPropagation();
    onDeleteTemplate(template.id, templateGet, dispatch);
  };
  const handleEdit = () => {
    if (owner) {
      setOpen(true);
    }
  };
  const handleNewCard = () => setCardCreateOpen(true);
  return (
    <>
      <Paper className={classes.templateItem}>
        <div
          className={classNames(
            classes.templateItemName,
            classesSpacing.mrAuto
          )}
        >
          {template.name}
        </div>
        <Tooltip title="Vytvořit kartu">
          <IconButton
            onClick={handleNewCard}
            className={classNames(
              classesEffect.hoverPrimary,
              classes.templateItemIcon,
              classesSpacing.p0,
              classesSpacing.ml1
            )}
          >
            <AddCircle color="inherit" />
          </IconButton>
        </Tooltip>
        {owner && (
          <>
            <Tooltip title="Editovat">
              <IconButton
                onClick={handleEdit}
                className={classNames(
                  classesEffect.hoverPrimary,
                  classes.templateItemIcon,
                  classesSpacing.p0,
                  classesSpacing.ml1
                )}
              >
                <Edit color="inherit" />
              </IconButton>
            </Tooltip>
            <Popconfirm
              confirmText="Opravdu chcete smazat tuto šablonu?"
              onConfirmClick={handleDelete}
              Button={
                <Tooltip title="Smazat">
                  <IconButton
                    className={classNames(
                      classesEffect.hoverSecondary,
                      classes.templateItemIcon,
                      classesSpacing.p0,
                      classesSpacing.ml1,
                      classesSpacing.mr1
                    )}
                  >
                    <Delete />
                  </IconButton>
                </Tooltip>
              }
            />
          </>
        )}
      </Paper>
      {cardCreateOpen && (
        <CardCreateRoot
          showModal={true}
          setShowModal={setCardCreateOpen}
          attributeTemplates={attributeTemplates}
        />
      )}
      <Modal
        open={open}
        setOpen={setOpen}
        fullSize={true}
        content={<TemplatesForm setShowModal={setOpen} template={template} />}
      />
    </>
  );
};
