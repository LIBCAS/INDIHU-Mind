import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    category: {
      display: "inline-block",
      fontWeight: 800,
      fontSize: 14,
      marginRight: theme.spacing(1),
      cursor: "pointer",
      transition: ".3s ease color",
      "&:hover": {
        color: theme.palette.primary.main
      }
    },
    label: {
      display: "inline-flex",
      alignItems: "center",
      color: theme.greyDark,
      padding: theme.spacing(0.5),
      marginRight: theme.spacing(1.5),
      background: theme.greyLightUltra,
      borderRadius: "10px",
      cursor: "pointer"
    },
    labelText: {
      fontSize: 14,
      transition: ".3s ease color",
      "&:hover": {
        color: theme.palette.primary.main
      }
    },
    labelDot: {
      display: "inline-block",
      width: "8px",
      height: "8px",
      marginLeft: "8px",
      marginRight: "8px",
      borderRadius: "50%"
    },
    cardLinked: {
      height: "140px",
      width: "100%",
      marginTop: theme.spacing(1.5),
      borderTop: `1px solid ${theme.greyLight}`,
      borderLeft: `1px solid ${theme.greyLight}`,
      borderRight: `1px solid ${theme.greyLight}`,
      borderTopLeftRadius: "5px",
      borderTopRightRadius: "5px",
      padding: "12px 8px"
    },

    cardLinkedNote: {
      overflow: "hidden",
      background:
        "linear-gradient(to bottom, rgba(0,0,0,1) 0%, rgba(0,0,0,1) 25%, rgba(0,0,0,0) 100%)",
      backgroundClip: "text",
      "-webkit-background-clip": "text",
      color: "transparent"
    },
    cardLinkedButton: {
      borderTop: `1px solid ${theme.greyLight}`,
      borderLeft: `1px solid ${theme.greyLight}`,
      borderRight: `1px solid ${theme.greyLight}`,
      borderRadius: 0
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
