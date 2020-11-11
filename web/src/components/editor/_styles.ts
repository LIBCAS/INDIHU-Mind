import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    editorWrapper: {
      width: "100%",
    },
    editor: {},
    editorActive: {
      paddingLeft: 8,
      paddingRight: 8,
      background: "white",
      border: `1px solid ${theme.greyLight}`,
      borderRadius: 3,
    },
    hidden: {
      display: "none",
    },
    toolbar: {
      border: `1px solid ${theme.greyLight}`,
      borderRadius: 3,
    },
  };
});
