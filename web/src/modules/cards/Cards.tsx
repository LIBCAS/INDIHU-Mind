import React, { useState, useEffect, useContext } from "react";
import { RouteComponentProps } from "react-router-dom";
import { Theme } from "@material-ui/core/styles/createMuiTheme";
import { useTheme } from "@material-ui/styles";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import Grid from "@material-ui/core/Grid";
import Fade from "@material-ui/core/Fade";
import Typography from "@material-ui/core/Typography";
import classNames from "classnames";
import Tooltip from "@material-ui/core/Tooltip";
import IconButton from "@material-ui/core/IconButton";
import ViewHeadline from "@material-ui/icons/ViewHeadline";
import ViewModule from "@material-ui/icons/ViewModule";
import { isEmpty } from "lodash";

import { GlobalContext, StateProps } from "../../context/Context";
import { categoryActiveSet } from "../../context/actions/category";
import { CardProps } from "../../types/card";
import { Table } from "../../components/tableCard/Table";
import { CardsTableDetail } from "../cardsTableDetail/CardsTableDetail";

import { CardCreateRoot } from "../cardCreate/CardCreateRoot";
import { CardCreateButton } from "../cardCreate/CardCreateButton";
import { useStyles as useEffectStyles } from "../../theme/styles/effectStyles";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";

import { CardsTile } from "./CardsTile";

import { onDeleteCard } from "../../utils/card";
import { getPathToCategory } from "./_utils";
import { columns } from "./_utils";
import * as store from "../../utils/store";
import _ from "lodash";

const baseUrl = "card/parametrized";
const storeViewPath = "cards-view";

enum ViewType {
  TILE = "TILE",
  TABLE = "TABLE"
}

export const Cards: React.FC<RouteComponentProps> = () => {
  const theme: Theme = useTheme();

  const matchesLg = useMediaQuery(theme.breakpoints.up("lg"));

  const matches1400 = useMediaQuery("(min-width:1400px)");

  const classesSpacing = useSpacingStyles();

  const classesText = useTextStyles();

  const classesEffect = useEffectStyles();

  const classesLayout = useLayoutStyles();

  const [selectedCard, setSelectedCard] = useState<CardProps | undefined>(
    undefined
  );

  const context: any = useContext(GlobalContext);

  const state: StateProps = context.state;

  const dispatch: Function = context.dispatch;

  const storedView = store.get(storeViewPath);

  const [view, setView] = useState<ViewType>(
    storedView === ViewType.TILE || storedView === ViewType.TABLE
      ? storedView
      : ViewType.TABLE
  );

  const changeView = (value: ViewType) => {
    store.set(storeViewPath, value);
    setView(value);
  };

  const [query, setQuery] = useState<any>({});

  const onCancel = () => setSelectedCard(undefined);

  const handleDelete = (id: string, afterEdit: Function) => {
    onDeleteCard(id, dispatch, afterEdit);
  };

  useEffect(() => {
    let query = {};
    if (!isEmpty(state.search.category)) {
      query = state.search.category;
    }
    if (!isEmpty(state.search.label)) {
      query = state.search.label;
    }
    setQuery(query);
  }, [state.search.category, state.search.label]);

  const isTable = view === ViewType.TABLE;
  const Icon = isTable ? ViewModule : ViewHeadline;

  return (
    <Fade in>
      <Grid container spacing={0}>
        <Grid
          item
          style={{
            flexGrow: 1,
            maxWidth: "100%",
            width: selectedCard ? (matchesLg ? "55%" : "auto") : "auto",
            overflowX: "hidden"
          }}
        >
          <div
            className={classNames(
              classesLayout.flex,
              classesLayout.spaceBetween,
              classesSpacing.mt1,
              classesSpacing.mb2,
              classesLayout.directionColumnMobile,
              classesLayout.alignCenter
            )}
          >
            <div className={classesSpacing.mr1}>
              {state.search.categoryName !== "" &&
                state.category.categoryActive && (
                  <Typography variant="h5">
                    {getPathToCategory(
                      state.category.categoryActive,
                      state.category.categories
                    ).map((cat, i, arr) => {
                      return (
                        <span
                          key={cat.id}
                          onClick={() => {
                            categoryActiveSet(dispatch, cat);
                          }}
                          className={classNames({
                            [classesText.text600]:
                              i + 1 === arr.length && arr.length > 1,
                            [(classesText.cursor, classesEffect.hoverPrimary)]:
                              i + 1 !== arr.length
                          })}
                        >
                          {cat.name}
                          {i + 1 !== arr.length && " > "}
                        </span>
                      );
                    })}
                  </Typography>
                )}
              {state.search.labelName !== "" && (
                <Typography variant="h5">{state.search.labelName}</Typography>
              )}
            </div>
            <div
              className={classNames(
                classesLayout.flex,
                classesLayout.alignCenter,
                classesSpacing.mlAuto,
                classesText.textGrey
              )}
            >
              <Tooltip title={isTable ? "Dlaždice" : "Tabulka"}>
                <IconButton
                  style={{ color: theme.blackIconColor }}
                  onClick={() => {
                    changeView(isTable ? ViewType.TILE : ViewType.TABLE);
                  }}
                >
                  <Icon fontSize="large" color="inherit" />
                </IconButton>
              </Tooltip>
              <div className={classesSpacing.ml1} />
              <CardCreateButton />
              <div className={classesSpacing.ml1} />
            </div>
          </div>
          {isTable ? (
            <Table
              baseUrl={baseUrl}
              query={query}
              columns={columns}
              selectedRow={selectedCard}
              setSelectedRow={setSelectedCard}
              handleDelete={handleDelete}
              Menu={CardCreateRoot}
            />
          ) : (
            <CardsTile query={query} />
          )}
        </Grid>
        {selectedCard && matches1400 && (
          <Grid
            item
            style={{
              width: "45%",
              marginTop: "3px",
              paddingLeft: "15px"
            }}
          >
            <CardsTableDetail selectedCard={selectedCard} onCancel={onCancel} />
          </Grid>
        )}
      </Grid>
    </Fade>
  );
};

export { Cards as default };
