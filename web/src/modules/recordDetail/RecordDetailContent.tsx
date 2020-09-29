import React, { useContext } from "react";
import { Typography } from "@material-ui/core";
import classNames from "classnames";
import { compact, flatten } from "lodash";

import { RecordProps, MarcEntity } from "../../types/record";
import { useStyles } from "./_recordDetailStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { CardTile } from "../../components/card/CardTile";
import { Editor } from "../../components/editor";
import { GlobalContext, StateProps } from "../../context/Context";
import { getMarcLabel, clearAuthorData } from "../recordsTemplates/_utils";

interface RecordDetailContentProps {
  record: RecordProps;
}

interface FieldProps {
  label: string;
  value?: string;
  compact?: boolean;
}

const Field: React.FC<FieldProps> = ({ label, value, compact }) => {
  const classes = useStyles();
  const classesLayout = useLayoutStyles();
  const classesSpacing = useSpacingStyles();
  const classesText = useTextStyles();
  return (
    <div
      className={classNames(
        classesLayout.flex,
        classesLayout.alignCenter,
        classesSpacing.mt1
      )}
    >
      <div
        className={classNames(classes.label, compact && classes.compactLabel)}
      >
        {label}:
      </div>
      <div className={classNames(classesText.normal, classesText.textBold)}>
        {value}
      </div>
    </div>
  );
};

export const RecordDetailContent: React.FC<RecordDetailContentProps> = ({
  record
}) => {
  const classes = useStyles();
  const classesSpacing = useSpacingStyles();

  const context: any = useContext(GlobalContext);
  const state: StateProps = context.state;
  const marc: MarcEntity[] = state.record.marc;

  const { document, content } = record;

  return (
    <>
      <div className={classesSpacing.mt2}>
        {compact([
          document
            ? {
                label: "Dokument",
                value: document.name
              }
            : null,
          ...(content
            ? []
            : flatten(
                (record.dataFields || []).map((d, i) =>
                  (d.subfields || []).map(subfield => ({
                    key: d.tag + subfield.code + i,
                    label: getMarcLabel({ ...d, ...subfield }, marc),
                    value:
                      d.tag === "100" || d.tag === "700"
                        ? clearAuthorData(subfield.data)
                        : subfield.data
                  }))
                )
              ))
        ]).map((field, i) => (
          <Field key={`${field.value}-${i}`} {...field} compact={!!content} />
        ))}
      </div>
      {content ? (
        <div className={classesSpacing.mt2}>
          <Editor value={content} readOnly={true} />
        </div>
      ) : (
        <></>
      )}
      {record.linkedCards && record.linkedCards.length > 0 && (
        <div className={classesSpacing.mt2}>
          <Typography variant="h6">Propojené karty</Typography>
          <div className={classes.columnsWrapper}>
            {record.linkedCards &&
              record.linkedCards.map(card => (
                <CardTile key={card.id} card={card} />
              ))}
          </div>
        </div>
      )}
    </>
  );
};
