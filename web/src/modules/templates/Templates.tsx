import React, { useEffect, useContext, useState } from "react";
import Typography from "@material-ui/core/Typography";
import Button from "@material-ui/core/Button";
import classNames from "classnames";

import { templateGet } from "../../context/actions/template";
import { GlobalContext, StateProps } from "../../context/Context";

import { Modal } from "../../components/portal/Modal";

import { useStyles } from "./_templatesStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";

import { TemplatesForm } from "./TemplatesForm";
import { TemplatesItem } from "./TemplatesItem";

export const Templates: React.FC = () => {
  const classes = useStyles();

  const classesSpacing = useSpacingStyles();

  const classesLayout = useLayoutStyles();

  const context: any = useContext(GlobalContext);

  const state: StateProps = context.state;

  const dispatch: Function = context.dispatch;

  const templates = state.template.templates;

  const [open, setOpen] = useState(false);

  useEffect(() => {
    templateGet(dispatch);
  }, []);
  return (
    <>
      <Modal
        open={open}
        setOpen={setOpen}
        fullSize={true}
        content={<TemplatesForm setShowModal={setOpen} />}
      />
      <div
        className={classNames(
          classesLayout.flex,
          classesLayout.flexWrap,
          classesLayout.alignCenter,
          classesLayout.spaceBetween,
          classesSpacing.mt1,
          classesSpacing.mb2
        )}
      >
        <Typography className={classNames()} variant="h5">
          Přehled šablon
        </Typography>
        <Button
          className={classes.createButton}
          variant="contained"
          color="primary"
          onClick={() => setOpen(true)}
        >
          Nová šablona
        </Button>
      </div>
      {templates.map(template => (
        <TemplatesItem key={template.id} template={template} />
      ))}
    </>
  );
};

export { Templates as default };
