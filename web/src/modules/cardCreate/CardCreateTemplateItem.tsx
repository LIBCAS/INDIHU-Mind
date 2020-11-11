import React, { useState } from "react";
import Button from "@material-ui/core/Button";
import Edit from "@material-ui/icons/Edit";
import Typography from "@material-ui/core/Typography";
import Tooltip from "@material-ui/core/Tooltip";
import IconButton from "@material-ui/core/IconButton";
import classNames from "classnames";

import { Modal } from "../../components/portal/Modal";
import { TemplatesForm } from "../templates/TemplatesForm";
import { CardTemplateProps } from "../../types/cardTemplate";

import { Divider } from "../../components/divider/Divider";

import { useStyles as useEffectStyles } from "../../theme/styles/effectStyles";
import { useStyles } from "./_cardCreateStyles";

interface CardCreateTemplateItemProps {
  template: CardTemplateProps;
  Icon: any;
  selectTemplate: Function;
}

export const CardCreateTemplateItem: React.FC<CardCreateTemplateItemProps> = ({
  template,
  Icon,
  selectTemplate,
}) => {
  const classes = useStyles();
  const classesEffect = useEffectStyles();
  const [open, setOpen] = useState(false);
  return (
    <div key={template.id} className={classes.templateItem}>
      <div className={classes.templateItemContent}>
        <div className={classes.templateItemIcon}>{Icon}</div>
        <div className={classes.templateItemText}>
          <Typography variant="subtitle1">{template.name}</Typography>
        </div>
      </div>
      <Divider />
      <div className={classes.templateItemActions}>
        <Button
          className={classes.templateItemSelect}
          color="primary"
          size="small"
          fullWidth
          onClick={() => selectTemplate(template)}
        >
          Vybrat šablonu
        </Button>
        {template.owner !== null && (
          <>
            <Tooltip title="Editovat">
              <IconButton
                onClick={() => setOpen(true)}
                className={classNames(
                  classesEffect.hoverPrimary,
                  classes.templateItemMenu
                )}
              >
                <Edit />
              </IconButton>
            </Tooltip>
            <Modal
              open={open}
              setOpen={setOpen}
              content={<TemplatesForm setShowModal={setOpen} item={template} />}
            />
          </>
        )}
      </div>
    </div>
  );
};
