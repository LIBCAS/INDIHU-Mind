import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    recordWrapper: {
      border: `1px solid ${theme.palette.grey[300]}`,
      padding: "0 10px"
    }
  };
});
