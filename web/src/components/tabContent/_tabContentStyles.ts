import { Theme } from "@material-ui/core/styles/createMuiTheme";
import { makeStyles } from "@material-ui/styles";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    tabContentWrapper: {
      display: "flex",
      height: "100%",
      justifyContent: "start",
      [theme.breakpoints.up("md")]: {
        maxHeight: "calc(100vh - 196px)",
        justifyContent: "space-between",
      },
      flexDirection: "column",
      padding: theme.spacing(2),
    },
    createButton: {
      marginBottom: theme.spacing(1),
    },
    categoriesContainer: {
      overflow: "auto",
      "&::-webkit-scrollbar": {
        width: "6px",
        backgroundColor: "#F5F5F5",
      },
      "&::-webkit-scrollbar-track": {
        "-webkit-box-shadow": "inset 0 0 6px rgba(0,0,0,0.3)",
        backgroundColor: "#F5F5F5",
      },
      "&::-webkit-scrollbar-thumb": {
        backgroundColor: "#093d77",
      },
    },
    category: {
      width: "100%",
      display: "flex",
      alignItems: "center",
      cursor: "pointer",
      padding: "4px 0 4px 8px",
      borderBottom: `1px solid transparent`,
      "&:hover": {
        "& p": {
          color: theme.palette.grey[100],
        },
        backgroundColor: theme.palette.primary.dark,
      },
    },
    categorySub: {
      fontSize: "14px",
    },
    categoryActive: {
      // fontSize: "18px",
      borderBottom: `1px solid #fff`,
    },
    subcategory: {
      display: "block",
      paddingLeft: theme.spacing(2),
    },
    categoryWrapper: {
      display: "flex",
      flexDirection: "column",
      padding: theme.spacing(4),
      [theme.breakpoints.up("md")]: {
        padding: theme.spacing(3),
      },
    },
    label: {
      marginRight: theme.spacing(1),
    },
    wrapper: {
      display: "flex",
      alignItems: "center",
      cursor: "pointer",
      padding: `4px`,
      marginBottom: theme.spacing(1),
    },
    wrapperActive: {
      borderRadius: "350px",
      background: "#313840",
    },
    labelLabel: {
      color: "#A0A1A3",
      paddingLeft: theme.spacing(1),
    },
    labelActive: {
      color: "#fff",
    },
    dot: {
      display: "inline-block",
      width: 5,
      height: 5,
      borderRadius: "50%",
      marginLeft: theme.spacing(1),
    },
    cardOpened: {
      whiteSpace: "nowrap",
      overflow: "hidden",
      textOverflow: "ellipsis",
      cursor: "pointer",
      marginTop: theme.spacing(1),
    },
    item: {
      display: "flex",
      width: "100%",
    },
    itemArrows: {
      display: "flex",
      flexDirection: "column",
      justifyContent: "space-around",
      padding: "4px 0",
      "& > *": {
        maxWidth: "16px",
        maxHeight: "16px",
        cursor: "pointer",
        "&:hover": {
          background: theme.palette.primary.main,
        },
      },
    },
    arrowDisabled: {
      cursor: "not-allowed",
      color: theme.palette.grey[500],
      "&: hover": {
        background: "transparent",
      },
    },
  };
});
