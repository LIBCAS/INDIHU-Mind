import React from "react";
import classNames from "classnames";
import Typography from "@material-ui/core/Typography";
import Pagination from "@material-ui/lab/Pagination";

import { useStyles } from "./_styles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { TilesComponentProps } from "./_types";
import { Select } from "../../components/select";
import { pageSizeOptions } from "./_enums";

const createSelectValue = (value: number) => {
  const valueString = `${value}`;
  return { value: valueString, label: valueString };
};

export const TilesComponent: React.FC<TilesComponentProps> = ({
  items,
  count,
  params,
  updateParams,
  TileComponent,
  loading,
  refresh,
}) => {
  const classes = useStyles();
  const classesLayout = useLayoutStyles();
  const classesSpacing = useSpacingStyles();

  const { page, pageSize } = params;

  const handlePagination = (_: any, page: number) => {
    updateParams({ page: page - 1 });
  };

  return (
    <>
      <div className={classes.cardsWrapper}>
        {TileComponent ? (
          items.map((item) => (
            <TileComponent key={item.id} item={item} refresh={refresh} />
          ))
        ) : (
          <></>
        )}
      </div>
      <div
        className={classNames(
          classes.tilesPagination,
          classesLayout.flex,
          classesLayout.alignCenter,
          classesLayout.justifyCenter,
          classesSpacing.mt3
        )}
      >
        {!loading && items && !count ? (
          <Typography
            style={{ marginBottom: "1em" }}
            variant="h5"
            color="textSecondary"
          >
            Nebyly nalezeny žádné položky.
          </Typography>
        ) : count ? (
          <>
            <div className={classes.pageSizeSelect}>
              <Select
                value={createSelectValue(pageSize)}
                onChange={(value) =>
                  value ? updateParams({ pageSize: Number(value) }) : null
                }
                options={pageSizeOptions.map(createSelectValue)}
                isClearable={false}
              />
            </div>
            <Pagination
              count={Math.ceil(count / pageSize)}
              page={page + 1}
              onChange={handlePagination}
              disabled={loading}
            />
          </>
        ) : (
          <></>
        )}
      </div>
    </>
  );
};
