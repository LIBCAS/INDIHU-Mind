import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    fileIcons: {
      height: "24px",
      cursor: "pointer",
      opacity: 1,
      transition: ".3s ease opacity"
    },
    fileWrapper: {
      display: "flex",
      marginTop: theme.spacing(1),
      alignItems: "center",
      border: `1px solid ${theme.greyLight}`,
      borderRadius: "3px",
      padding: "10px 5px",
      transition: ".3s ease box-shadow",
      "&:hover": {
        boxShadow: "0 0 0 2px rgba(0,0,0,.1)",
        "& $fileIcons": {
          opacity: 1
        }
      }
    }
  };
});
