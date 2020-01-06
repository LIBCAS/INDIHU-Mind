import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    card: {
      border: "1px dashed gray",
      padding: "0.5rem 1rem",
      marginBottom: ".5rem",
      marginRight: ".5rem",
      backgroundColor: "white",
      cursor: "move"
    },
    cardSelection: {
      border: "1px solid gray",
      padding: "0.5rem 1rem",
      marginBottom: ".5rem",
      marginRight: ".5rem",
      backgroundColor: "white",
      cursor: "move"
    },
    container: {
      display: "flex",
      justifyContent: "center",
      alignItems: "center",
      border: "1px dashed gray",
      height: "3rem",
      width: "45%",
      marginBottom: "1rem"
    }
  };
});
