import * as React from "react";
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  IconButton
} from "@material-ui/core";
import Highlight from "@material-ui/icons/Highlight";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import { useStyles } from "./_navbarStyles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";
import { useTheme } from "@material-ui/styles";
import DesktopMenu from "./DesktopMenu";
import MenuIcon from "@material-ui/icons/Menu";

export interface NavbarProps {
  setLeftPanelOpen: any;
  setOpenModalInfo: any;
}

const Navbar: React.SFC<NavbarProps> = ({
  setLeftPanelOpen,
  setOpenModalInfo
}) => {
  const theme: Theme = useTheme();
  const classes = useStyles();
  const matchesLg = useMediaQuery(theme.breakpoints.up("lg"));

  return (
    <AppBar position="static" className={classes.header}>
      <Toolbar>
        <div className={classes.titleWrapper}>
          <div className={classes.titleWrapperChild}>
            <IconButton
              className={classes.logo}
              color="inherit"
              aria-label="logo"
            >
              <Highlight />
            </IconButton>
            <Typography
              className={classes.title}
              variant="h5"
              color="inherit"
              noWrap
              style={{ marginRight: "25px" }}
            >
              {matchesLg ? "INDIHU Mind" : "IM"}
            </Typography>
          </div>
        </div>
        <div className={classes.desktopMenu}>
          <DesktopMenu setOpenModalInfo={setOpenModalInfo} />
        </div>
        <div
          className={classes.mobileMenuToogle}
          onClick={() => setLeftPanelOpen((prev: boolean) => !prev)}
        >
          <IconButton color="inherit" aria-label="Menu">
            <MenuIcon />
          </IconButton>
        </div>
      </Toolbar>
    </AppBar>
  );
};

export default Navbar;
