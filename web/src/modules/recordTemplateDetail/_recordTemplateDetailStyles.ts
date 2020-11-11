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
  };
});
