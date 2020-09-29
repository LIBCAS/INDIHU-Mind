import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    editorWrapper: {
      width: "100%"
    },
    editor: {
      border: "1px solid #F1F1F1",
      paddingLeft: 8,
      paddingRight: 8,
      "*": {
        fontFamily: "Raleway,sans-serif !important",
        fontSize: "16px !important"
      }
    },
    hidden: {
      display: "none"
    }
  };
});
