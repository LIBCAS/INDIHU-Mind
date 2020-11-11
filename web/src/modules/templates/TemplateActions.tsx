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

interface TemplateActionsProps {
  item: CardTemplateProps;
  refresh: Function;
}

export const TemplateActions: React.FC<TemplateActionsProps> = ({
  item,
  refresh,
}) => {
  const classes = useStyles();
  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;
  const classesSpacing = useSpacingStyles();
  const classesEffect = useEffectStyles();
  const [open, setOpen] = useState(false);
  const [cardCreateOpen, setCardCreateOpen] = useState(false);
  const { owner, attributeTemplates } = item;
  const handleDelete = (e: React.MouseEvent<HTMLButtonElement, MouseEvent>) => {
    e.stopPropagation();
    onDeleteTemplate(item.id, templateGet, dispatch);
    refresh();
  };
  const handleEdit = () => {
    if (owner) {
      setOpen(true);
      refresh();
    }
  };
  const handleNewCard = (
    e: React.MouseEvent<HTMLButtonElement, MouseEvent>
  ) => {
    e.stopPropagation();
    setCardCreateOpen(true);
  };
  return (
    <>
      <div className={classes.templateActions}>
        <Tooltip title="Vytvořit kartu">
          <IconButton
            onClick={handleNewCard}
            className={classNames(
              classesEffect.hoverPrimary,
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
      </div>

      {cardCreateOpen && (
        <CardCreateRoot
          open={true}
          setOpen={setCardCreateOpen}
          attributeTemplates={attributeTemplates}
        />
      )}

      <Modal
        open={open}
        setOpen={setOpen}
        fullSize={true}
        content={
          <TemplatesForm refresh={refresh} setShowModal={setOpen} item={item} />
        }
      />
    </>
  );
};
