import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    tileWrapper: {
      display: "flex",
      flexWrap: "wrap",
      "& > div": {
        width: "100%"
      },
      [theme.breakpoints.up("sm")]: {
        marginRight: `-${theme.spacing(2)}px`,
        "& > div": {
          width: `calc(50% - ${theme.spacing(2)}px)`,
          marginRight: theme.spacing(2)
        }
      },
      [theme.breakpoints.up("md")]: {
        "& > div": {
          width: `calc(33% - ${theme.spacing(2)}px)`,
          marginRight: theme.spacing(2)
        }
      },
      [theme.breakpoints.up("lg")]: {
        "& > div": {
          width: `calc(25% - ${theme.spacing(2)}px)`,
          marginRight: theme.spacing(2)
        }
      }
    },
    cardLinked: {
      height: "140px",
      width: "100%",
      //marginTop: theme.spacing(1.5),
      borderTop: `1px solid ${theme.greyLight}`,
      borderLeft: `1px solid ${theme.greyLight}`,
      borderRight: `1px solid ${theme.greyLight}`,
      borderTopLeftRadius: "5px",
      borderTopRightRadius: "5px",
      padding: theme.spacing(2)
    },
    cardLinkedNote: {
      overflow: "hidden"
    },
    cardLinkedDate: {
      width: "100%",
      padding: theme.spacing(2),
      textAlign: "center",
      borderLeft: `1px solid ${theme.greyLight}`,
      borderRight: `1px solid ${theme.greyLight}`
    },
    cardLinkedButton: {
      borderTop: `1px solid ${theme.greyLight}`,
      borderLeft: `1px solid ${theme.greyLight}`,
      borderRight: `1px solid ${theme.greyLight}`
    },
    cardLinkedButtonLast: {
      border: `1px solid ${theme.greyLight}`,
      borderTopLeftRadius: "0",
      borderTopRightRadius: "0",
      borderBottomLeftRadius: "5px",
      borderBottomRightRadius: "5px"
    }
  };
});
