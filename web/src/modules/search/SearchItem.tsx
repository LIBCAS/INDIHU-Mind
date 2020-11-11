import React from "react";
import Typography from "@material-ui/core/Typography";
import KeyboardArrowRight from "@material-ui/icons/KeyboardArrowRight";

import { SearchCardProps } from "../../types/search";

import { translate } from "../../utils/card";

import { useStyles } from "./_searchStyles";

interface SearchItemProps {
  searchCard: SearchCardProps;
  history: any;
}

export const SearchItem: React.FC<SearchItemProps> = ({
  searchCard,
  history,
}) => {
  const classes = useStyles();
  const onClick = () => {
    history.push(`/card/${searchCard.card.id}`);
  };
  return (
    <div className={classes.card} onClick={onClick}>
      <Typography variant="h6" display="inline">
        {searchCard.card.name}
      </Typography>
      <div className={classes.button}>
        <KeyboardArrowRight color="primary" />
      </div>

      {Object.keys(searchCard.highlightMap).map((key) => (
        <div
          key={key}
          className={classes.highlight}
          dangerouslySetInnerHTML={{
            __html: `${translate(key)}: ${searchCard.highlightMap[key]}`,
          }}
        />
      ))}
      {searchCard.highlightedAttributes.map((att) => (
        <div
          key={att.id}
          className={classes.highlight}
          dangerouslySetInnerHTML={{
            __html: `${att.name}: ${att.highlight}`,
          }}
        />
      ))}
    </div>
  );
};
