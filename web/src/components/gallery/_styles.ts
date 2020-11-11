import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    gallery: {
      display: "block",
      width: "100%",
      minWidth: "100%",
      minHeight: 1,
      overflow: "auto",
    },
    loader: {
      position: "relative",
      display: "flex",
      justifyContent: "center",
      border: `1px solid ${theme.palette.grey[300]}`,
      borderRadius: 16,
    },
    loaderText: {
      padding: "24px 16px 16px",
      fontWeight: "bold",
      fontSize: 16,
    },
  };
});
