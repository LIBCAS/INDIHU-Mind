import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    hoverPrimary: {
      cursor: "pointer",
      transition: ".3s ease color",
      "&:hover": {
        color: theme.palette.primary.main,
      },
    },
    hoverSecondary: {
      cursor: "pointer",
      transition: ".3s ease color",
      "&:hover": {
        color: theme.palette.secondary.main,
      },
    },
    active: {
      color: theme.palette.primary.main,
    },
  };
});
