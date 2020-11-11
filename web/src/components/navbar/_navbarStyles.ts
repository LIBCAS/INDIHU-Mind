import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    root: {
      left: "50%",
      transform: "translateX(-50%)",
    },
    toolbar: {
      position: "relative",
      display: "flex",
      // flexWrap: "wrap"
    },
    menuIconWrapper: {
      borderRadius: "0",
      justifyContent: "flex-start",
      flex: "1",
      [theme.breakpoints.up("lg")]: {
        display: "none",
      },
    },
    menuIconWrapperMobile: {
      flex: "0",
    },
    titleWrapper: {
      display: "flex",
      alignItems: "center",
      flexBasis: "auto",
      [theme.breakpoints.up("lg")]: {
        flexBasis: 250,
      },
    },
    titleWrapperChild: {
      display: "flex",
      alignItems: "center",
      cursor: "pointer",
    },
    logo: {
      // marginRight: theme.spacing(1)
    },
    title: {},
    searchWrapper: {
      display: "flex",
      alignItems: "center",
      flex: "1",
      justifyContent: "end",
      [theme.breakpoints.up("lg")]: {
        marginLeft: "auto",
        flex: "0",
      },
    },
    searchWrapperFullWidth: {
      position: "absolute",
      width: "95%",
    },
    search: {
      borderRadius: "350px",
      backgroundColor: theme.palette.common.white,
      "&:hover": {
        backgroundColor: theme.palette.common.white,
      },
      marginLeft: "0",
      width: "100%",
      [theme.breakpoints.up("lg")]: {
        marginLeft: theme.spacing(1),
        width: "auto",
      },
    },
    searchIconWrapper: {
      position: "absolute",
      right: "0",
      width: "45px",
      height: "100%",
      display: "flex",
      alignItems: "center",
      justifyContent: "flex-end",
      cursor: "pointer",
      flex: "1",
      [theme.breakpoints.up("lg")]: {
        display: "none",
        marginRight: theme.spacing(2),
      },
    },
    inputRoot: {
      color: `${theme.palette.grey}`,
      width: "100%",
    },
    inputInput: {
      paddingTop: theme.spacing(1.5),
      paddingRight: theme.spacing(5),
      paddingBottom: theme.spacing(1.5),
      paddingLeft: theme.spacing(3),
      width: "100%",
      [theme.breakpoints.up("lg")]: {
        paddingRight: theme.spacing(3),
        transition: theme.transitions.create("width"),
        width: 120,
      },
      [theme.breakpoints.up("xl")]: {
        "&:focus": {
          width: 200,
        },
      },
    },
    navItems: {
      flex: "1",
    },
    searchIconWrapperHide: {
      opacity: 0,
    },
    searchInput: {
      width: `calc(100% - 8px)`,
    },
  };
});
