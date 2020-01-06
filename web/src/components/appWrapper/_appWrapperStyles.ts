import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    base: {
      margin: "56px auto auto auto",
      [theme.breakpoints.up("sm")]: {
        marginTop: "64px"
      }
    },
    gridRight: {
      width: "100%",
      padding: theme.spacing(1),
      [theme.breakpoints.up("md")]: {
        width: "calc(100% - 250px)"
      }
    }
  };
});
