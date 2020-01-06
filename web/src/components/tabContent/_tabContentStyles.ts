import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    tabContentWrapper: {
      display: "flex",
      height: "100%",
      // justifyContent: "center",
      flexDirection: "column",
      padding: theme.spacing(2)
    },
    createButton: {
      marginBottom: theme.spacing(1)
    },
    category: {
      display: "flex",
      alignItems: "center",
      cursor: "pointer",
      marginBottom: theme.spacing(1),
      borderBottom: `1px solid transparent`
    },
    categorySub: {
      fontSize: "14px"
    },
    categoryActive: {
      // fontSize: "18px",
      borderBottom: `1px solid #fff`
    },
    subcategory: {
      display: "block",
      paddingLeft: theme.spacing(2)
    },
    categoryWrapper: {
      display: "flex",
      flexDirection: "column",
      padding: theme.spacing(4),
      [theme.breakpoints.up("md")]: {
        padding: theme.spacing(2)
      }
    },
    label: {
      marginRight: theme.spacing(1)
    },
    wrapper: {
      display: "flex",
      alignItems: "center",
      cursor: "pointer",
      padding: `4px`,
      marginBottom: theme.spacing(1)
    },
    wrapperActive: {
      borderRadius: "350px",
      background: "#313840"
    },
    labelLabel: {
      color: "#A0A1A3",
      paddingLeft: theme.spacing(1)
    },
    labelActive: {
      color: "#fff"
    },
    dot: {
      display: "inline-block",
      width: 5,
      height: 5,
      borderRadius: "50%",
      marginLeft: theme.spacing(1)
    },
    cardOpened: {
      whiteSpace: "nowrap",
      overflow: "hidden",
      textOverflow: "ellipsis",
      cursor: "pointer",
      marginTop: theme.spacing(1)
    }
  };
});
