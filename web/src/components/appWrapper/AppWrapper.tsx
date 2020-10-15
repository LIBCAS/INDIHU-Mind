import React, { useState, useContext } from "react";
import Grid from "@material-ui/core/Grid";
import Fade from "@material-ui/core/Fade";

import { GlobalContext } from "../../context/Context";

import { Navbar } from "../navbar//Navbar";
import { LeftPanel } from "../leftPanel/LeftPanel";
import { TableSearchWatcher } from "./TableSearchWatcher";

import { useStyles } from "./_appWrapperStyles";

export const AppWrapper: React.FC = ({ children }) => {
  const context: any = useContext(GlobalContext);
  const { categoryActive, categories } = context.state.category;
  const { labelActive } = context.state.label;
  const dispatch: Function = context.dispatch;
  const classes = useStyles();
  const [leftPanelOpen, setLeftPanelOpen] = useState(false);

  return (
    <Fade in>
      <div className={classes.base}>
        <TableSearchWatcher
          dispatch={dispatch}
          categories={categories}
          categoryActive={categoryActive}
          labelActive={labelActive}
        />
        <Navbar setLeftPanelOpen={setLeftPanelOpen} />
        <Grid container spacing={0} style={{ flexWrap: "nowrap" }}>
          <Grid item>
            <LeftPanel
              leftPanelOpen={leftPanelOpen}
              setLeftPanelOpen={setLeftPanelOpen}
            />
          </Grid>
          <Grid item className={classes.gridRight}>
            {children}
          </Grid>
        </Grid>
      </div>
    </Fade>
  );
};
