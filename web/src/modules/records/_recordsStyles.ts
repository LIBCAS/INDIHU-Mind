import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    recordWrapper: {
      border: `1px solid ${theme.palette.grey[300]}`,
      padding: "0 10px"
    },
    recordForm: {
      width: "85vw",
      height: "85vh"
    },
    mainPanel: {
      height: "calc(100% - 60px - 1px)", // 1px divider
      width: "100%",
      overflowY: "auto"
    },
    buttonsPanel: {
      height: 60
    },
    nameField: {
      padding: theme.spacing(2)
    }
  };
});
