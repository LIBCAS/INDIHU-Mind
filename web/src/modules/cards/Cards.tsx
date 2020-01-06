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
import { useStyles as useEffectStyles } from "../../theme/styles/effectStyles";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";

import { CardsTile } from "./CardsTile";

import { onDeleteCard } from "../../utils/card";
import { getPathToCategory } from "./_utils";
import { columns } from "./_utils";

const baseUrl = "card/parametrized";

export const Cards: React.FC<RouteComponentProps> = () => {
  const theme: Theme = useTheme();
  const matchesMd = useMediaQuery(theme.breakpoints.up("md"));
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

  const [view, setView] = useState<"tile" | "table">("table");
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
  return (
    <Fade in>
      <Grid container spacing={0}>
        <Grid
          item
          style={{
            flexGrow: 1,
            maxWidth: "100%",
            width: selectedCard ? (matchesMd ? "55%" : "auto") : "auto"
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
              <Tooltip title="Dlaždice">
                <IconButton
                  className={classNames({
                    [classesText.textGrey]: view === "tile",
                    [classesText.textGreyLight]: view !== "tile"
                  })}
                  onClick={() => {
                    setSelectedCard(undefined);
                    setView("tile");
                  }}
                >
                  <ViewModule fontSize="large" color="inherit" />
                </IconButton>
              </Tooltip>
              <Tooltip title="Tabulka">
                <IconButton
                  onClick={() => {
                    setView("table");
                  }}
                  className={classNames({
                    [classesText.textGrey]: view === "table",
                    [classesText.textGreyLight]: view !== "table"
                  })}
                >
                  <ViewHeadline fontSize="large" color="inherit" />
                </IconButton>
              </Tooltip>
            </div>
          </div>
          {view === "table" && (
            <Table
              baseUrl={baseUrl}
              query={query}
              columns={columns}
              selectedRow={selectedCard}
              setSelectedRow={setSelectedCard}
              handleDelete={handleDelete}
              Menu={CardCreateRoot}
            />
          )}
          {view === "tile" && <CardsTile />}
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
