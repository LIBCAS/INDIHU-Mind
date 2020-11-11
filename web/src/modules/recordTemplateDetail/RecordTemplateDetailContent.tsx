import React, { useState, useEffect, useContext } from "react";
import classNames from "classnames";
import { RecordTemplateProps } from "../../types/recordTemplate";
import { get } from "lodash";

import { GlobalContext, StateProps } from "../../context/Context";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { parseTemplate, createStyle } from "../recordsTemplates/_utils";

interface RecordTemplateDetailContentProps {
  item: RecordTemplateProps;
}

export const RecordTemplateDetailContent: React.FC<RecordTemplateDetailContentProps> = ({
  item,
}) => {
  const classesLayout = useLayoutStyles();
  const classesSpacing = useSpacingStyles();

  const context: any = useContext(GlobalContext);
  const state: StateProps = context.state;
  const { marc } = state.record;

  const [values, setValues] = useState<any>(null);
  const [cards, setCards] = useState<any[]>([]);

  useEffect(() => {
    if (item) {
      const { cardsInit, initValuesParsed } = parseTemplate(item, marc);
      setValues(initValuesParsed);
      setCards(cardsInit);
    }
  }, [item, marc]);
  return (
    <div
      className={classNames(
        classesLayout.flex,
        classesLayout.flexWrap,
        classesSpacing.mt2
      )}
    >
      {cards.map(({ id, count, text }) => {
        return (
          <div
            key={id + count}
            style={createStyle(get(values, `${id + count}customizations`, []), {
              border: "1px dashed gray",
              padding: "0.5rem 1rem",
              marginBottom: ".5rem",
              marginRight: ".5rem",
              backgroundColor: "white",
            })}
          >
            {text}
          </div>
        );
      })}
    </div>
  );
};
