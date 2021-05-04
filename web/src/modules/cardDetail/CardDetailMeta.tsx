import { Theme } from "@material-ui/core/styles/createMuiTheme";
import Typography from "@material-ui/core/Typography";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import { useTheme } from "@material-ui/styles";
import classNames from "classnames";
import React from "react";
import { useStyles as useEffectStyles } from "../../theme/styles/effectStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { CardContentProps } from "../../types/card";
import { formatDateTime } from "../../utils";

interface CardDetailMetaProps {
  currentCardContent: CardContentProps;
  setCurrentCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps | undefined>
  >;
  cardContents: CardContentProps[];
}

export const CardDetailMeta: React.FC<CardDetailMetaProps> = ({
  currentCardContent,
  setCurrentCardContent,
  cardContents,
}) => {
  const classesSpacing = useSpacingStyles();
  const classesEffect = useEffectStyles();
  const theme: Theme = useTheme();
  const matchesLg = useMediaQuery(theme.breakpoints.up("lg"));
  const changeVersion = (id: string) => {
    setCurrentCardContent(() => {
      const res = cardContents.filter((c) => c.id === id);
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
      {cardContents.map((c) => (
        <Typography
          onClick={() => changeVersion(c.id)}
          className={classNames(classesEffect.hoverPrimary, {
            [classesEffect.active]: c.id === currentCardContent.id,
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
