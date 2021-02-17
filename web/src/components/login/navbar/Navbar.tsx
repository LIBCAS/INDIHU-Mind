import { AppBar, IconButton, Toolbar, Typography } from "@material-ui/core";
import { Theme } from "@material-ui/core/styles/createMuiTheme";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import MenuIcon from "@material-ui/icons/Menu";
import { useTheme } from "@material-ui/styles";
import * as React from "react";
import { useHistory } from "react-router-dom";
import { Logo } from "../../icons/Logo";
import DesktopMenu from "./DesktopMenu";
import { useStyles } from "./_navbarStyles";

export interface NavbarProps {
  setLeftPanelOpen?: any;
  setOpenModalInfo?: any;
}

const Navbar: React.SFC<NavbarProps> = ({
  setLeftPanelOpen,
  setOpenModalInfo,
}) => {
  const theme: Theme = useTheme();
  const classes = useStyles();
  const matchesLg = useMediaQuery(theme.breakpoints.up("lg"));
  const history = useHistory();

  return (
    <AppBar position="sticky" className={classes.header}>
      <Toolbar>
        <div className={classes.titleWrapper}>
          <div
            className={classes.titleWrapperChild}
            onClick={() => history.push("/")}
          >
            <IconButton
              className={classes.logo}
              color="inherit"
              aria-label="logo"
            >
              <Logo />
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
        {setOpenModalInfo && (
          <div className={classes.desktopMenu}>
            <DesktopMenu setOpenModalInfo={setOpenModalInfo} />
          </div>
        )}
        {setLeftPanelOpen && (
          <div
            className={classes.mobileMenuToogle}
            onClick={() => setLeftPanelOpen((prev: boolean) => !prev)}
          >
            <IconButton color="inherit" aria-label="Menu">
              <MenuIcon />
            </IconButton>
          </div>
        )}
      </Toolbar>
    </AppBar>
  );
};

export default Navbar;
