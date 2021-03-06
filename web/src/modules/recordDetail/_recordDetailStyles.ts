import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    actionsWrapper: {
      display: "flex",
      width: "100%",
      alignItems: "center",
    },
    actionsIcon: {
      marginLeft: theme.spacing(1),
      transition: ".3s ease color",
      cursor: "pointer",
      "&:hover": {
        color: theme.palette.primary.main,
      },
    },
    actionsBack: {
      marginLeft: 0,
      marginRight: "auto",
    },
    iconSecondary: {
      transition: ".3s ease color",
      cursor: "pointer",
      color: theme.blackIconColor,
      "&:hover": {
        color: theme.palette.secondary.main,
      },
    },
    columnsWrapper: {
      display: "flex",
      flexWrap: "wrap",
      justifyContent: "space-between",
      "& > div": {
        width: "100%",
        [theme.breakpoints.up("md")]: {
          width: "calc(50% - 8px)",
        },
        [theme.breakpoints.up("lg")]: {
          width: "calc(33% - 15px)",
          marginRight: "15px",
        },
      },
      [theme.breakpoints.up("lg")]: {
        justifyContent: "flex-start",
        marginRight: "-15px",
      },
    },
    label: {
      width: 300,
      color: theme.greyText,
      fontWeight: 800,
      textTransform: "uppercase",
      fontSize: 16,
      textAlign: "right",
      padding: "0 .5em",
    },
    compactLabel: { width: "auto" },
  };
});
