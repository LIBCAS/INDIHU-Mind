import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    root: {
      overflow: "auto",
    },
    popoverPaper: {
      overfloxX: "visible",
      overflowY: "auto",
    },
    paper: {
      boxShadow: "none",
      [theme.breakpoints.up("sm")]: {
        minWidth: "300px",
      },
    },
    autoWidth: {
      minWidth: "auto",
    },
    overflowVisible: {
      overflow: "visible",
    },
  };
});
