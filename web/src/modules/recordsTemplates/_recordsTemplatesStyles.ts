import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    createButton: {
      width: "100%",
      marginTop: theme.spacing(1),
      [theme.breakpoints.up("md")]: {
        marginTop: "0",
        width: "33%"
      }
    },
    recordWrapper: {
      border: `1px solid ${theme.palette.grey[300]}`,
      padding: "0 10px"
    },
    recordTemplateItemName: {
      fontWeight: 800
    },
    recordTemplateItemIcon: {
      color: theme.greyText,
      [theme.breakpoints.up("md")]: {
        opacity: 0
      }
    },
    recordTemplateItem: {
      display: "flex",
      alignItems: "center",
      padding: "20px 10px",
      border: `1px solid ${theme.greyLight}`,
      borderRadius: "3px",
      marginBottom: "20px",
      "&:hover": {
        background: theme.purpleLight
      },
      "&:hover $recordTemplateItemIcon": {
        opacity: 1
      }
    }
  };
});
