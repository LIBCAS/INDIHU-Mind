import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    button: {
      display: "flex",
      alignItems: "center",
      opacity: 0,
      transition: ".3s ease opacity"
    },
    card: {
      display: "flex",
      alignItems: "center",
      justifyContent: "flex-start",
      flexWrap: "wrap",
      width: "100%",
      marginBottom: theme.spacing(2),
      transition: ".3s ease all",
      border: `1px solid ${theme.greyLight}`,
      borderRadius: "3px",
      padding: "10px 20px",
      cursor: "pointer",
      "&:hover": {
        background: theme.purpleLight
      },
      "&:hover $button": {
        opacity: 1
      },
      "& em": {
        fontWeight: 800,
        fontStyle: "normal"
      }
    },
    highlight: {
      width: "100%"
    }
  };
});
