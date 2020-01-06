import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    flex: {
      display: "flex"
    },
    flexWrap: {
      flexWrap: "wrap"
    },
    flexGrow: {
      flexGrow: 1
    },
    directionRowReverse: {
      flexDirection: "row-reverse"
    },
    directionColumn: {
      flexDirection: "column"
    },
    justifyCenter: {
      justifyContent: "center"
    },
    alignCenter: {
      alignItems: "center"
    },
    alignStart: {
      alignItems: "flex-start"
    },
    alignEnd: {
      alignItems: "flex-end"
    },
    spaceBetween: {
      justifyContent: "space-between"
    },
    halfItems: {
      "& > *": {
        width: "50%"
      }
    },
    fullItemsMobile: {
      [theme.breakpoints.down("md")]: {
        "& > *": {
          width: "100%"
        }
      }
    },
    directionColumnMobile: {
      [theme.breakpoints.down("md")]: {
        flexDirection: "column"
      }
    }
  };
});
