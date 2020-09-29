import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    form: {
      marginBottom: 10
    },
    field: {
      marginBottom: 15
    },
    buttons: {
      marginTop: 15,
      display: "flex",
      alignItems: "center"
    },
    link: {
      marginLeft: 15,
      textDecoration: "underline",
      cursor: "pointer"
    }
  };
});
