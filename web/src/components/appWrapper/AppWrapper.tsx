import Fade from "@material-ui/core/Fade";
import Grid from "@material-ui/core/Grid";
import classNames from "classnames";
import React, { useContext, useState } from "react";
import { GlobalContext } from "../../context/Context";
import { LeftPanel } from "../leftPanel/LeftPanel";
import { Navbar } from "../navbar//Navbar";
import { TableSearchWatcher } from "./TableSearchWatcher";
import { useStyles } from "./_appWrapperStyles";

export const AppWrapper: React.FC = ({ children }) => {
  const context: any = useContext(GlobalContext);
  const { categoryActive, categories } = context.state.category;
  const { labelActive } = context.state.label;
  const dispatch: Function = context.dispatch;
  const classes = useStyles();
  const [leftPanelOpen, setLeftPanelOpen] = useState(false);
  const enableCards = /^\/card/.test(window.location.pathname);

  return (
    <Fade style={{ height: "100%" }} in>
      <div className={classes.base}>
        <TableSearchWatcher
          dispatch={dispatch}
          categories={categories}
          categoryActive={categoryActive}
          labelActive={labelActive}
        />
        <Navbar setLeftPanelOpen={setLeftPanelOpen} />
        <Grid
          container
          spacing={0}
          style={{ flexWrap: "nowrap", height: "calc(100% - 64px)" }}
        >
          <Grid item>
            <LeftPanel
              leftPanelOpen={leftPanelOpen}
              setLeftPanelOpen={setLeftPanelOpen}
              enableCards={enableCards}
            />
          </Grid>
          <Grid
            item
            className={classNames(
              classes.gridRight,
              enableCards && classes.gridRightCompact
            )}
          >
            {children}
          </Grid>
        </Grid>
      </div>
    </Fade>
  );
};
