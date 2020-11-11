import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    button: {
      color: theme.greyText,
      border: `1px solid ${theme.greyLight}`,
      borderRadius: "3px",
      textAlign: "left",
      justifyContent: "flex-start",
      "&:hover": {
        border: `1px solid ${theme.palette.primary.main}`,
        color: theme.palette.primary.main,
        background: "#fff",
      },
    },
    big: {
      display: "flex",
      justifyContent: "center",
      padding: "60px 45px",
    },
  };
});
