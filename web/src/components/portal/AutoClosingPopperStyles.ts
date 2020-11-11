import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    autoClosingPopper: {
      borderRadius: "5px",
      zIndex: 1400,
    },
    root: {
      overflow: "auto",
    },
    paper: {
      border: `1px solid ${theme.greyLight}`,
      [theme.breakpoints.up("sm")]: {
        minWidth: "300px",
      },
    },
  };
});
