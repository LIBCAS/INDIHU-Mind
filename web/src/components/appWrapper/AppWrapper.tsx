import Fade from "@material-ui/core/Fade";
import Grid from "@material-ui/core/Grid";
import classNames from "classnames";
import React, { useContext, useState } from "react";
import { GlobalContext } from "../../context/Context";
import { LeftPanel } from "../leftPanel/LeftPanel";
import { Navbar } from "../navbar/Navbar";
import { TableSearchWatcher } from "./TableSearchWatcher";
import { useStyles } from "./_appWrapperStyles";
import { Modal } from "../../components/login/modal/Modal";
import { useUserToken } from "../../hooks/authHooks";
import { isAdmin } from "../../utils/token";

export const AppWrapper: React.FC = ({ children }) => {
  const context: any = useContext(GlobalContext);
  const { categoryActive, categories } = context.state.category;
  const { labelActive } = context.state.label;
  const dispatch: Function = context.dispatch;
  const classes = useStyles();

  const [leftPanelOpen, setLeftPanelOpen] = useState(false);
  const [openModalInfo, setOpenModalInfo] = React.useState(false);

  const enableCards = /^\/card/.test(window.location.pathname);

  const token = useUserToken();

  const admin = isAdmin(token);

  return (
    <Fade style={{ height: "100%" }} in>
      <div className={classes.base}>
        <div className={classes.appTopMargin} />
        <TableSearchWatcher
          dispatch={dispatch}
          categories={categories}
          categoryActive={categoryActive}
          labelActive={labelActive}
        />
        <Navbar
          setOpenModalInfo={setOpenModalInfo}
          setLeftPanelOpen={setLeftPanelOpen}
        />
        <Modal
          openModalInfo={openModalInfo}
          setOpenModalInfo={setOpenModalInfo}
        />
        <Grid
          container
          spacing={0}
          style={{ flexWrap: "nowrap", height: "calc(100% - 64px)" }}
        >
          <Grid item>
            <LeftPanel
              setOpenModalInfo={setOpenModalInfo}
              leftPanelOpen={leftPanelOpen}
              setLeftPanelOpen={setLeftPanelOpen}
              enableCards={enableCards}
            />
          </Grid>
          <Grid
            item
            className={classNames({
              [classes.gridRight]: true,
              [classes.gridRightCompact]: enableCards && !admin,
              [classes.gridRightCompactAdmin]: enableCards && admin,
            })}
          >
            {children}
          </Grid>
        </Grid>
      </div>
    </Fade>
  );
};
