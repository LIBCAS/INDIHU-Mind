import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    customScrollbar: {
      "& *": {
        "&::-webkit-scrollbar": {
          width: "6px",
          height: "6px",
          backgroundColor: "#F5F5F5"
        },
        "&::-webkit-scrollbar-track": {
          "-webkit-box-shadow": "inset 0 0 6px rgba(0,0,0,0.3)",
          backgroundColor: "#F5F5F5"
        },
        "&::-webkit-scrollbar-thumb": {
          backgroundColor: "#093d77"
        }
      }
    },
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
      color: theme.greyDark,
      background: theme.palette.grey[300],
      padding: "4px 8px",
      borderRadius: 14,
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
      borderTop: `1px solid ${theme.greyLight}`,
      borderLeft: `1px solid ${theme.greyLight}`,
      borderRight: `1px solid ${theme.greyLight}`,
      borderTopLeftRadius: "5px",
      borderTopRightRadius: "5px",
      padding: theme.spacing(2),
      display: "flex",
      flexDirection: "column",
      justifyContent: "space-between"
    },

    cardLinkedNote: {
      overflow: "hidden",
      background:
        "linear-gradient(to bottom, rgba(0,0,0,1) 0%, rgba(0,0,0,1) 25%, rgba(0,0,0,0) 100%)",
      backgroundClip: "text",
      "-webkit-background-clip": "text"
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
    },
    cardTileLabelsContainer: {
      overflow: "auto",
      flexShrink: 0,
      whiteSpace: "nowrap"
    },
    cardTileTitle: {
      width: "100%",
      overflow: "auto",
      minHeight: "20%",
      maxHeight: "80%",
      flexShrink: 1
    },
    cardTileNote: {
      minHeight: "20%",
      flexShrink: 1
    }
  };
});
