import React from "react";
import Drawer from "@material-ui/core/Drawer";
import SwipeableDrawer from "@material-ui/core/SwipeableDrawer";
import { Theme } from "@material-ui/core/styles/createMuiTheme";
import { useTheme } from "@material-ui/styles";
import useMediaQuery from "@material-ui/core/useMediaQuery";

import { useStyles } from "./_leftPanelStyles";
import { LeftPanelContent } from "./LeftPanelContent";
import { useUserToken } from "../../hooks/authHooks";
import { isAdmin } from "../../utils/token";

interface LeftPanelProps {
  leftPanelOpen: boolean;
  setLeftPanelOpen: any;
  enableCards: boolean;
  setOpenModalInfo: any;
}

export const LeftPanel: React.FC<LeftPanelProps> = ({
  leftPanelOpen,
  setLeftPanelOpen,
  enableCards,
  setOpenModalInfo,
}) => {
  const classes = useStyles();
  const theme: Theme = useTheme();

  const token = useUserToken();

  const admin = isAdmin(token);

  const matchesBigScreen = useMediaQuery(
    theme.breakpoints.up(admin ? ("xxl" as any) : "xl")
  );

  return (
    <>
      {matchesBigScreen && enableCards ? (
        <>
          <div className={classes.drawerPlaceholder} />
          <Drawer
            variant="permanent"
            open={leftPanelOpen}
            onClose={setLeftPanelOpen(false)}
            PaperProps={{ className: classes.paper }}
            classes={{ root: classes.drawerRoot }}
          >
            <LeftPanelContent setOpenModalInfo={setOpenModalInfo} />
          </Drawer>
        </>
      ) : (
        <SwipeableDrawer
          ModalProps={{
            keepMounted: true,
          }}
          open={leftPanelOpen}
          onClose={() => setLeftPanelOpen(false)}
          onOpen={() => setLeftPanelOpen(true)}
          PaperProps={{ className: classes.paper }}
          // classes={{root: classes.drawerRoot}}
        >
          <LeftPanelContent
            setOpenModalInfo={setOpenModalInfo}
            setLeftPanelOpen={setLeftPanelOpen}
            enableCards={enableCards}
          />
        </SwipeableDrawer>
      )}
    </>
  );
};
