import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    actionsWrapper: {
      display: "flex",
      width: "100%",
      alignItems: "center",
    },
    actionsIcon: {
      marginLeft: theme.spacing(1),
      transition: ".3s ease color",
      cursor: "pointer",
      "&:hover": {
        color: theme.palette.primary.main,
      },
    },
    actionsBack: {
      marginLeft: 0,
      marginRight: "auto",
    },
    actions: {
      display: "flex",
      alignItems: "center",
    },
    contentCategoryCreate: {
      display: "inline-block",
    },
    contentLabel: {
      display: "inline-flex",
      alignItems: "center",
      color: theme.greyDark,
      padding: theme.spacing(0.5),
      marginRight: theme.spacing(1.5),
      background: theme.greyLightUltra,
      borderRadius: "10px",
    },
    contentLabelText: {
      fontSize: 14,
    },
    contentLabelDot: {
      display: "inline-block",
      width: "8px",
      height: "8px",
      marginLeft: "8px",
      marginRight: "8px",
      borderRadius: "50%",
    },
    contentAttributeTitle: {
      fontWeight: 800,
      marginRight: theme.spacing(1),
    },
    columnsWrapper: {
      display: "flex",
      flexWrap: "wrap",
      justifyContent: "space-between",
      "& > div": {
        width: "100%",
        [theme.breakpoints.up("md")]: {
          width: "calc(50% - 8px)",
        },
        [theme.breakpoints.up("lg")]: {
          width: "calc(33% - 15px)",
          marginRight: "15px",
        },
      },
      [theme.breakpoints.up("lg")]: {
        justifyContent: "flex-start",
        marginRight: "-15px",
      },
    },
    fileIcons: {
      opacity: 0,
      transition: ".3s ease opacity",
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
          opacity: 1,
        },
      },
    },
    iconSecondary: {
      transition: ".3s ease color",
      cursor: "pointer",
      color: theme.blackIconColor,
      "&:hover": {
        color: theme.palette.secondary.main,
      },
    },
    cardPrint: {
      padding: 12,
    },
    cardPrintRow: {
      display: "flex",
      marginBottom: 6,
    },
    cardPrintLabel: {
      width: 80,
      marginRight: 8,
      textAlign: "right",
    },
    cardPrintValue: {
      fontWeight: "bold",
    },
    commentWrapper: {
      position: "relative",
      maxWidth: "100%",
      minWidth: 150,
      paddingBottom: theme.spacing(1),
      "&:hover": {
        "& $commentActions": {
          display: "block",
        },
      },
    },
    commentActions: {
      display: "none",
      position: "absolute",
      top: -12,
      right: 0,
      paddingRight: 4,
      cursor: "pointer",
    },
    commentInnerWrapper: {
      padding: theme.spacing(1.5),
      border: `1px solid ${theme.greyLight}`,
      borderRadius: 5,
      width: "100%",
    },
    updatedWrapper: {
      maxWidth: "calc(100% - 4px)",
      paddingRight: 4,
      display: "flex",
      justifyContent: "flex-end",
      fontSize: 11,
    },
  };
});
