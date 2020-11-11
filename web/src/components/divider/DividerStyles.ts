import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    divider: {
      width: "100%",
      height: "1px",
      background: theme.greyLight,
    },
  };
});
