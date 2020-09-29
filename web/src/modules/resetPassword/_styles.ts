import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    header: {
      position: "fixed",
      width: "100%"
    },
    resetPassword: {
      maxWidth: "calc(100% - 20px)",
      width: 500
    },
    resetPasswordContainer: {
      width: "100%",
      height: "100%",
      display: "flex",
      alignItems: "center",
      justifyContent: "center"
    },
    resetPasswordEmail: {
      padding: 20
    },
    form: {
      marginBottom: 10
    },
    field: {
      marginBottom: 15
    }
  };
});
