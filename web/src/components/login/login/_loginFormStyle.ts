import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    form: {
      marginBottom: "10px"
    },
    field: {
      marginBottom: "15px"
    },
    submit: {
      marginTop: "15px",
      borderRadius: "0"
    }
  };
});
