import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useLabelStyles = makeStyles((theme: Theme) => {
  return {
    label: {
      paddingLeft: "3px",
      paddingRight: "3px",
      margin: "0 3px",
      fontWeight: "bold",
      "&:hover": {
        color: theme.palette.primary.main,
      },
    },
    deleteLabelIcon: {
      color: "transparent",
      transition: ".3s ease color",
      "&:hover": {
        color: theme.red,
      },
    },
  };
});
