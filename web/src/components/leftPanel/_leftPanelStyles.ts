import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    drawerPlaceholder: {
      minHeight: "100%",
      width: 250
    },
    drawerRoot: {
      position: "fixed",
      top: 64,
      height: "100%"
    },
    buttonWrapper: {
      display: "flex",
      flexDirection: "column",
      marginTop: theme.spacing(5),
      marginBottom: theme.spacing(4),
      marginLeft: "auto",
      marginRight: "auto"
    },
    buttonRegister: {
      display: "flex",
      justifyContent: "center",
      marginBottom: theme.spacing(4)
    },
    tabsWrapper: {
      display: "flex",
      justifyContent: "center",
      borderBottom: "1px solid #fff"
    },
    tab: {
      cursor: "pointer",
      padding: `${theme.spacing(1)}px ${theme.spacing(2)}px`,
      color: "#fff",
      background: "transparent",
      borderTopLeftRadius: "3px",
      borderTopRightRadius: "3px"
    },
    activeTab: {
      color: "#212121",
      background: "#fff"
    },
    paper: {
      height: "100%",
      width: 250,
      display: "flex",
      zIndex: 1200,
      outline: "none",
      overflowY: "auto",
      flexDirection: "column",
      background: "#212121",
      color: "#fff",
      [theme.breakpoints.up("md")]: {
        minHeight: "calc(100vh - 64px)"
      }
    },
    wrapper: {
      display: "flex",
      flexDirection: "column",
      textAlign: "center"
    },
    link: {
      width: "100%",
      color: "#fff",
      fontSize: 16,
      paddingBottom: theme.spacing(0.5),
      "&:hover": {
        textDecoration: "none"
      },
      "&:not(:last-child)": {
        marginBottom: theme.spacing(1.5)
      }
    },
    linkActive: {
      borderBottom: "1px solid #fff"
    }
  };
});
