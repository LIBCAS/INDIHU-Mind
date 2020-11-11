import React from "react";
import Typography from "@material-ui/core/Typography";
import classNames from "classnames";
import { Theme } from "@material-ui/core/styles/createMuiTheme";
import { useTheme } from "@material-ui/styles";
import useMediaQuery from "@material-ui/core/useMediaQuery";

import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useEffectStyles } from "../../theme/styles/effectStyles";

import { CardContentProps } from "../../types/card";
import { formatDateTime } from "../../utils";

interface CardDetailMetaProps {
  card: CardContentProps;
  setCard: React.Dispatch<React.SetStateAction<CardContentProps | undefined>>;
  cardContent: CardContentProps[];
}

export const CardDetailMeta: React.FC<CardDetailMetaProps> = ({
  card,
  setCard,
  cardContent,
}) => {
  const classesSpacing = useSpacingStyles();
  const classesEffect = useEffectStyles();
  const theme: Theme = useTheme();
  const matchesLg = useMediaQuery(theme.breakpoints.up("lg"));
  const changeVersion = (id: string) => {
    setCard(() => {
      const res = cardContent.filter((c) => c.id === id);
      return { ...res[0] };
    });
  };
  return (
    <div className={classNames({ [classesSpacing.ml3]: matchesLg })}>
      <Typography
        className={classNames(classesSpacing.mt2, classesSpacing.mb2)}
        variant="h6"
      >
        Historie verzí
      </Typography>
      {cardContent.map((c) => (
        <Typography
          onClick={() => changeVersion(c.id)}
          className={classNames(classesEffect.hoverPrimary, {
            [classesEffect.active]: c.id === card.id,
          })}
          gutterBottom
          key={c.id}
        >
          {formatDateTime(c.created)}
        </Typography>
      ))}
    </div>
  );
};
