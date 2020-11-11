import React from "react";

import classNames from "classnames";

import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";

import { CardTemplateProps } from "../../types/cardTemplate";

import { useStyles } from "./_templatesStyles";

import Paper from "@material-ui/core/Paper";
import { TemplateActions } from "./TemplateActions";

interface TemplatesItemProps {
  item: CardTemplateProps;
  refresh: Function;
}

export const TemplatesItem: React.FC<TemplatesItemProps> = ({
  item,
  refresh,
}) => {
  const classes = useStyles();
  const classesSpacing = useSpacingStyles();
  return (
    <>
      <Paper className={classes.templateItem}>
        <div
          className={classNames(
            classes.templateItemName,
            classesSpacing.mrAuto
          )}
        >
          {item.name}
        </div>
        <TemplateActions item={item} refresh={refresh} />
      </Paper>
    </>
  );
};
