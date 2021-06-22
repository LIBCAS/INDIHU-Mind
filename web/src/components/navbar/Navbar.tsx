import AppBar from "@material-ui/core/AppBar";
import Fade from "@material-ui/core/Fade";
import IconButton from "@material-ui/core/IconButton";
import InputBase from "@material-ui/core/InputBase";
import { Theme } from "@material-ui/core/styles/createMuiTheme";
import Toolbar from "@material-ui/core/Toolbar";
import Typography from "@material-ui/core/Typography";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import Cancel from "@material-ui/icons/Cancel";
import MenuIcon from "@material-ui/icons/Menu";
import SearchIcon from "@material-ui/icons/Search";
import { useTheme } from "@material-ui/styles";
import classNames from "classnames";
import React, { useEffect, useRef, useState } from "react";
import { RouteComponentProps, withRouter } from "react-router-dom";
import { useUserToken } from "../../hooks/authHooks";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { isAdmin } from "../../utils/token";
import { DeleteCards } from "../deleteCards/DeleteCards";
import { Logo } from "../icons/Logo";
import { NavbarItems } from "./NavbarItems";
import { NavbarUser } from "./NavbarUser";
import { useStyles } from "./_navbarStyles";

interface NavbarProps {
  setLeftPanelOpen: any;
  setOpenModalInfo?: any;
}

const NavbarView: React.FC<NavbarProps & RouteComponentProps> = ({
  setLeftPanelOpen,
  setOpenModalInfo,
  history,
}) => {
  const classes = useStyles();
  const classesText = useTextStyles();
  const theme: Theme = useTheme();

  const token = useUserToken();

  const admin = isAdmin(token);

  const matchesSmall = useMediaQuery("(max-width:400px)");
  const matchesMd = useMediaQuery(theme.breakpoints.up("md"));
  const matchesBigScreen = useMediaQuery(
    theme.breakpoints.up(admin ? ("xxl" as any) : "xl")
  );
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
          className={classNames({
            [classes.menuIconWrapper]: !admin,
            [classes.menuIconWrapperAdmin]: admin,
            [classes.menuIconWrapperMobile]: matchesSmall,
          })}
          onClick={() => setLeftPanelOpen((prev: boolean) => !prev)}
        >
          <IconButton color="inherit" aria-label="Menu">
            <MenuIcon />
          </IconButton>
        </div>
        <div
          className={classNames({
            [classes.titleWrapper]: !admin,
            [classes.titleWrapperAdmin]: admin,
          })}
        >
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
              <Logo />
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
        <NavbarItems
          visible={matchesBigScreen}
          setOpenModalInfo={setOpenModalInfo}
        />
        <div
          className={classNames({
            [classes.searchWrapper]: !admin,
            [classes.searchWrapperAdmin]: admin,
            [classes.searchWrapperFullWidth]: showSearch && !matchesBigScreen,
          })}
        >
          <div
            className={classNames({
              [classes.searchIconWrapper]: !admin,
              [classes.searchIconWrapperAdmin]: admin,
            })}
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
          <Fade in={showSearch || matchesBigScreen} mountOnEnter unmountOnExit>
            <div
              className={classNames({
                [classes.search]: !admin,
                [classes.searchAdmin]: admin,
                [classesText.textGrey]: true,
              })}
            >
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
                    input: admin ? classes.inputInputAdmin : classes.inputInput,
                  }}
                />
                {matchesBigScreen && (
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
          {!(showSearch && !matchesBigScreen) && (
            <NavbarUser matchesMd={matchesBigScreen} />
          )}
          {matchesBigScreen && <DeleteCards />}
        </div>
      </Toolbar>
    </AppBar>
  );
};

export const Navbar = withRouter(NavbarView);
