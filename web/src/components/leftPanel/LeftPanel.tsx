import React from "react";
import Drawer from "@material-ui/core/Drawer";
import SwipeableDrawer from "@material-ui/core/SwipeableDrawer";
import { Theme } from "@material-ui/core/styles/createMuiTheme";
import { useTheme } from "@material-ui/styles";
import useMediaQuery from "@material-ui/core/useMediaQuery";

import { useStyles } from "./_leftPanelStyles";
import { LeftPanelContent } from "./LeftPanelContent";

interface LeftPanelProps {
  leftPanelOpen: boolean;
  setLeftPanelOpen: any;
}

export const LeftPanel: React.FC<LeftPanelProps> = ({
  leftPanelOpen,
  setLeftPanelOpen
}) => {
  const classes = useStyles();
  const theme: Theme = useTheme();
  const matchesMd = useMediaQuery(theme.breakpoints.up("md"));
  return (
    <>
      {matchesMd ? (
        <>
          <div className={classes.drawerPlaceholder} />
          <Drawer
            variant={"permanent"}
            open={leftPanelOpen}
            onClose={() => setLeftPanelOpen(false)}
            PaperProps={{ className: classes.paper }}
            classes={{ root: classes.drawerRoot }}
          >
            <LeftPanelContent />
          </Drawer>
        </>
      ) : (
        <SwipeableDrawer
          ModalProps={{
            keepMounted: true
          }}
          open={leftPanelOpen}
          onClose={() => setLeftPanelOpen(false)}
          onOpen={() => setLeftPanelOpen(true)}
          PaperProps={{ className: classes.paper }}
          // classes={{root: classes.drawerRoot}}
        >
          <LeftPanelContent setLeftPanelOpen={setLeftPanelOpen} />
        </SwipeableDrawer>
      )}
    </>
  );
};
