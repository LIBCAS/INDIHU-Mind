import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    textField: {
      color: theme.palette.grey[900],
    },
    button: {
      marginLeft: 5,
      minWidth: 115,
    },
    modal: {
      width: "100%",
      height: "100%",
    },
    buttons: {
      height: 72,
      padding: "0 20px",
      borderBottom: `1px solid ${theme.greyLight}`,
    },
    search: {},
    searchDropdown: {
      position: "absolute",
      zIndex: 2,
      backgroundColor: theme.palette.background.paper,
    },
    progressBar: {
      position: "absolute",
      width: 192,
      marginTop: -3,
    },
  };
});
