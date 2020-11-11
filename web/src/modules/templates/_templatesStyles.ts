import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    createButton: {
      width: "100%",
      marginTop: theme.spacing(1),
      [theme.breakpoints.up("md")]: {
        marginTop: "0",
        width: "33%",
      },
    },
    templateItemName: {
      fontWeight: 800,
    },
    templateItemIcon: {
      color: theme.blackIconColor,
      [theme.breakpoints.up("md")]: {
        opacity: 0,
      },
    },
    templateItem: {
      display: "flex",
      alignItems: "center",
      padding: "20px 10px",
      borderRadius: "3px",
      marginBottom: "20px",
      "&:hover": {
        background: theme.purpleLight,
      },
      "&:hover $templateItemIcon": {
        opacity: 1,
      },
    },
    addWrapper: {
      position: "relative",
      marginTop: theme.spacing(1.5),
    },
    attributeWrapper: {
      display: "flex",
      flexDirection: "column",
      width: "100%",
      padding: theme.spacing(3),
      paddingBottom: theme.spacing(2),
      paddingTop: theme.spacing(1),
    },
    atributeFieldwrapper: {
      fontSize: "16px",
      maxWidth: "240px",
      minWidth: "240px",
      border: `1px solid ${theme.greyLight}`,
      borderRadius: 5,
      padding: theme.spacing(1.5),
      paddingTop: theme.spacing(0.5),
      marginRight: theme.spacing(1),
      marginBottom: theme.spacing(1),
    },
    actionWrapper: {
      display: "flex",
      flexDirection: "row-reverse",
      justifyContent: "space-between",
      marginTop: theme.spacing(2),
    },
    templateFormWrapper: {
      height: "85vh",
    },
    templateActions: {
      display: "flex",
      alignItems: "center",
    },
  };
});
