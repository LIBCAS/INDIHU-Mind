import React from "react";
import { Typography } from "@material-ui/core";
import classNames from "classnames";
import { RecordProps } from "../../types/record";

import { useStyles } from "./_recordDetailStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { CardTile } from "../../components/card/CardTile";

interface RecordDetailContentProps {
  record: RecordProps;
}

export const RecordDetailContent: React.FC<RecordDetailContentProps> = ({
  record
}) => {
  const classes = useStyles();
  const classesLayout = useLayoutStyles();
  const classesSpacing = useSpacingStyles();
  const classesText = useTextStyles();
  return (
    <>
      <Typography variant="h6" className={classNames(classesSpacing.mb1)}>
        Tagy
      </Typography>
      <div className={classNames(classesLayout.flex, classesLayout.flexWrap)}>
        {record.dataFields &&
          record.dataFields.map((d, i) => (
            <div
              className={classNames(classesText.subtitle, classesSpacing.mr1)}
              key={d.tag + i}
            >
              {d.tag}
            </div>
          ))}
      </div>
      {record.linkedCards && record.linkedCards.length > 0 && (
        <Typography variant="h6" className={classNames(classesSpacing.mt1)}>
          Propojené karty
        </Typography>
      )}
      <div className={classes.columnsWrapper}>
        {record.linkedCards &&
          record.linkedCards.map(card => (
            <CardTile key={card.id} card={card} />
          ))}
      </div>
    </>
  );
};
