import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    actionsWrapper: {
      display: "flex",
      width: "100%"
    },
    actionsIcon: {
      marginLeft: theme.spacing(1),
      transition: ".3s ease color",
      cursor: "pointer",
      "&:hover": {
        color: theme.palette.primary.main
      }
    },
    actionsBack: {
      marginLeft: 0,
      marginRight: "auto"
    },
    contentName: {
      marginBottom: theme.spacing(1),
      marginRight: "15px"
    },
    contentCategory: {
      display: "inline-block",
      fontWeight: 800,
      fontSize: 14,
      marginRight: theme.spacing(1)
    },
    contentLabel: {
      display: "inline-flex",
      alignItems: "center",
      color: theme.greyDark,
      padding: theme.spacing(0.5),
      marginRight: theme.spacing(1.5),
      background: theme.greyLightUltra,
      borderRadius: "10px"
    },
    contentLabelText: {
      fontSize: 14
    },
    contentLabelDot: {
      display: "inline-block",
      width: "8px",
      height: "8px",
      marginLeft: "8px",
      marginRight: "8px",
      borderRadius: "50%"
    },
    contentAttributeTitle: {
      fontWeight: 800,
      marginRight: theme.spacing(1)
    },
    columnsWrapper: {
      display: "flex",
      flexWrap: "wrap",
      justifyContent: "space-between",
      "& > div": {
        width: "100%",
        [theme.breakpoints.up("lg")]: {
          width: "calc(50% - 8px)"
        }
      }
    },
    fileIcons: {
      opacity: 0,
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
      cursor: "pointer",
      "&:hover": {
        boxShadow: "0 0 0 1px rgba(0,0,0,.1)",

        "& $fileIcons": {
          opacity: 1
        }
      }
    }
  };
});
