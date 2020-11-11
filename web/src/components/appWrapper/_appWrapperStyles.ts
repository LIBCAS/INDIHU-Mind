import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    base: {
      margin: "56px auto auto auto",
      [theme.breakpoints.up("sm")]: {
        marginTop: "64px",
      },
    },
    gridRight: {
      width: "100%",
      height: "100%",
      overflow: "auto",
      "&::-webkit-scrollbar": {
        width: "6px",
        backgroundColor: "#F5F5F5",
      },
      "&::-webkit-scrollbar-track": {
        "-webkit-box-shadow": "inset 0 0 6px rgba(0,0,0,0.3)",
        backgroundColor: "#F5F5F5",
      },
      "&::-webkit-scrollbar-thumb": {
        backgroundColor: "#093d77",
      },
      padding: theme.spacing(1),
    },
    gridRightCompact: {
      [theme.breakpoints.up("lg")]: {
        width: "calc(100% - 250px)",
      },
    },
  };
});
