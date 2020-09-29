import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => ({
  recordPopperWrapper: {
    width: 300,
    padding: theme.spacing(3)
  },
  recordPopperExistingStage: {
    padding: `0 ${theme.spacing(3)}px`,
    paddingBottom: theme.spacing(1),
    width: "auto",
    maxWidth: "90vw",
    [theme.breakpoints.up("md")]: {
      maxWidth: "45vw"
    }
  }
}));
