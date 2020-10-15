import React, { useState, useEffect } from "react";
import { RouteComponentProps } from "react-router-dom";
import Typography from "@material-ui/core/Typography";
import classNames from "classnames";
import Fade from "@material-ui/core/Fade";

import { SearchCardProps } from "../../types/search";
import { api } from "../../utils/api";
import { Loader } from "../../components/loader/Loader";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";

import { SearchItem } from "./SearchItem";

let controller = new AbortController();

export const Search: React.FC<RouteComponentProps> = ({
  history,
  location
}) => {
  const classesSpacing = useSpacingStyles();
  const searchParams = new URLSearchParams(location.search);
  const searchText = searchParams.get("q");
  const [loading, setLoading] = useState(false);
  const [cards, setCards] = useState<SearchCardProps[]>([]);

  useEffect(() => {
    if (!controller.signal.aborted && loading) controller.abort();
    controller = new AbortController();
    setLoading(true);
    api()
      .get(`card/search?page=0&pageSize=50&q=${searchText}`, {
        signal: controller.signal
      })
      .json()
      .then((res: any) => {
        setLoading(false);
        setCards(res.items);
      });
    // TODO
    // .catch()
  }, [searchText]);
  return (
    <Fade in>
      <div>
        <Typography
          className={classNames(classesSpacing.mb2, classesSpacing.mt2)}
          variant="h5"
        >
          Vyhledávání karet
        </Typography>
        <div style={{ position: "relative" }}>
          <Loader loading={loading} local />
        </div>
        {cards.map((card, i) => (
          <SearchItem key={i} searchCard={card} history={history} />
        ))}
      </div>
    </Fade>
  );
};

export { Search as default };
