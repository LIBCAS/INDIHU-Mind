import React, { useState, useEffect } from "react";
import { Typography } from "@material-ui/core";
import classNames from "classnames";
import { RecordTemplateProps } from "../../types/recordTemplate";
import { get } from "lodash";

import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { parseTemplate } from "../recordsTemplates/_utils";

interface RecordTemplateDetailContentProps {
  recordTemplate: RecordTemplateProps;
}

export const RecordTemplateDetailContent: React.FC<
  RecordTemplateDetailContentProps
> = ({ recordTemplate }) => {
  const classesLayout = useLayoutStyles();
  const classesSpacing = useSpacingStyles();
  const classesText = useTextStyles();
  const [values, setValues] = useState<any>(null);
  const [cards, setCards] = useState<any[]>([]);
  useEffect(() => {
    if (recordTemplate) {
      const { cardsInit, initValuesParsed } = parseTemplate(recordTemplate);
      setValues(initValuesParsed);
      setCards(cardsInit);
    }
  }, [recordTemplate]);
  return (
    <>
      <Typography variant="h6" className={classNames(classesSpacing.mb1)}>
        Tagy
      </Typography>
      <div className={classNames(classesLayout.flex, classesLayout.flexWrap)}>
        {cards.map(({ id, count, text }) => {
          return (
            <div
              key={id + count}
              style={{
                border: "1px dashed gray",
                padding: "0.5rem 1rem",
                marginBottom: ".5rem",
                marginRight: ".5rem",
                backgroundColor: "white",
                ...(get(values, `${id + count}customizations`, []).includes(
                  "BOLD"
                ) && {
                  fontWeight: 600
                }),
                ...(get(values, `${id + count}customizations`, []).includes(
                  "ITALIC"
                ) && {
                  fontStyle: "italic"
                }),
                ...(get(values, `${id + count}customizations`, []).includes(
                  "UPPERCASE"
                ) && {
                  textTransform: "uppercase"
                })
              }}
            >
              {`${text} ${get(values, `${id + count}code`, "")}`}
            </div>
          );
        })}
      </div>
    </>
  );
};
