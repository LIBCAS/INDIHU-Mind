import React, { useState, useRef, useEffect } from "react";
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import IconButton from "@material-ui/core/IconButton";
import Typography from "@material-ui/core/Typography";
import MenuIcon from "@material-ui/icons/Menu";
import Highlight from "@material-ui/icons/Highlight";
import { Theme } from "@material-ui/core/styles/createMuiTheme";
import { useTheme } from "@material-ui/styles";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import { withRouter, RouteComponentProps } from "react-router-dom";

import InputBase from "@material-ui/core/InputBase";
import SearchIcon from "@material-ui/icons/Search";
import Cancel from "@material-ui/icons/Cancel";
import Fade from "@material-ui/core/Fade";
import classNames from "classnames";

import { DeleteCards } from "../deleteCards/DeleteCards";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";

import { useStyles } from "./_navbarStyles";
import { NavbarItems } from "./NavbarItems";
import { NavbarUser } from "./NavbarUser";

interface NavbarProps {
  setLeftPanelOpen: any;
}

const NavbarView: React.FC<NavbarProps & RouteComponentProps> = ({
  setLeftPanelOpen,
  history,
}) => {
  const classes = useStyles();
  const classesText = useTextStyles();
  const theme: Theme = useTheme();
  const matchesSmall = useMediaQuery("(max-width:400px)");
  const matchesMd = useMediaQuery(theme.breakpoints.up("md"));
  const matchesLg = useMediaQuery(theme.breakpoints.up("lg"));
  const searchRef = useRef<HTMLInputElement | null>(null);
  const [showSearch, setShowSearch] = useState<boolean>(false);
  const [search, setSearch] = useState<string>("");
  const [showSearchIcon, setShowSearchIcon] = useState<boolean>(true);
  useEffect(() => {
    if (showSearch && searchRef.current !== null) {
      searchRef.current.focus();
    }
  }, [showSearch]);

  const onSubmit = (e: any) => {
    e.preventDefault();
    if (search === "") return false;
    const searchEncoded = encodeURI(search);
    history.push("/search?q=" + searchEncoded);
  };
  return (
    <AppBar className={classes.root} position="fixed">
      <Toolbar className={classes.toolbar}>
        <div
          className={classNames(classes.menuIconWrapper, {
            [classes.menuIconWrapperMobile]: matchesSmall,
          })}
          onClick={() => setLeftPanelOpen((prev: boolean) => !prev)}
        >
          <IconButton color="inherit" aria-label="Menu">
            <MenuIcon />
          </IconButton>
        </div>
        <div className={classes.titleWrapper}>
          <div
            className={classes.titleWrapperChild}
            onClick={() => {
              history.push("/cards");
            }}
          >
            <IconButton
              className={classes.logo}
              color="inherit"
              aria-label="logo"
            >
              <Highlight />
            </IconButton>
            <Typography
              className={classes.title}
              variant="h4"
              color="inherit"
              noWrap
              style={{ marginRight: "25px" }}
            >
              {matchesMd ? "indihu-mind" : "IM"}
            </Typography>
          </div>
        </div>
        <NavbarItems matchesMd={matchesLg} />
        <div
          className={classNames({
            [classes.searchWrapper]: true,
            [classes.searchWrapperFullWidth]: showSearch && !matchesLg,
          })}
        >
          <div
            className={classes.searchIconWrapper}
            style={
              showSearch ? { marginRight: 0, right: "0" } : { right: "80px" }
            }
            onClick={() => {
              setShowSearch((prev) => !prev);
            }}
          >
            {showSearch ? (
              <Fade in={showSearch} timeout={500}>
                <Cancel
                  color="secondary"
                  style={{
                    position: "absolute",
                    left: 0,
                    cursor: "pointer",
                    zIndex: 1,
                  }}
                />
              </Fade>
            ) : (
              <SearchIcon
                style={{ position: "absolute", right: 0, cursor: "pointer" }}
              />
            )}
          </div>
          <Fade in={showSearch || matchesLg} mountOnEnter unmountOnExit>
            <div className={classNames(classes.search, classesText.textGrey)}>
              <form style={{ position: "relative" }} onSubmit={onSubmit}>
                <InputBase
                  className={classes.searchInput}
                  onFocus={() => setShowSearchIcon(true)}
                  onBlur={() => setShowSearchIcon(true)}
                  inputRef={searchRef}
                  value={search}
                  onChange={(e: any) => setSearch(e.target.value)}
                  placeholder="Vyhledat karty"
                  classes={{
                    root: classes.inputRoot,
                    input: classes.inputInput,
                  }}
                />
                {matchesLg && (
                  <SearchIcon
                    color="inherit"
                    className={classNames({
                      [classes.searchIconWrapperHide]: !showSearchIcon,
                    })}
                    style={{
                      position: "absolute",
                      top: "50%",
                      transition: ".3s ease opacity",
                      transform: "translate(0, -50%)",
                      right: "10px",
                      cursor: "pointer",
                    }}
                    onClick={onSubmit}
                  />
                )}
              </form>
            </div>
          </Fade>
          {!(showSearch && !matchesLg) && <NavbarUser matchesMd={matchesLg} />}
          {matchesLg && <DeleteCards />}
        </div>
      </Toolbar>
    </AppBar>
  );
};

export const Navbar = withRouter(NavbarView);
