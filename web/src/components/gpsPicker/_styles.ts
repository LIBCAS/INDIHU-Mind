import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    button: {
      marginLeft: 5
    },
    modal: {
      width: "100%",
      height: "100%"
    },
    buttons: {
      height: 72,
      padding: "0 16px"
    }
  };
});
