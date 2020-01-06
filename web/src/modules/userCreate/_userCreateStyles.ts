import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    createUserButton: {
      width: "100%",
      [theme.breakpoints.up("md")]: {
        width: "auto"
      }
    }
  };
});
