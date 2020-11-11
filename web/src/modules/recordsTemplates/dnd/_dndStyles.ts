import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    card: {
      border: `1px dashed ${theme.palette.grey[500]}`,
      padding: "0.5rem 1rem",
      backgroundColor: "white",
      cursor: "move",
    },
    cardContainer: {
      position: "relative",
      marginBottom: ".5rem",
      marginRight: ".5rem",
    },
    cardMenu: {
      position: "absolute",
      top: -40,
      left: 0,
      zIndex: 10,
      height: 36,
      background: "white",
      border: `1px solid ${theme.palette.grey[500]}`,
      borderRadius: 4,
    },
    cardMenuRight: {
      left: "auto",
      right: 0,
    },
    cardMenuBottom: {
      top: "auto",
      bottom: 40,
      height: "auto",
      minWidth: 150,
    },
    cardMenuIcons: {
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
      height: 36,
      width: "100%",
      "& > *": {
        marginLeft: 8,
        marginRight: 8,
        cursor: "pointer",
      },
    },
    creatorSelects: {
      display: "flex",
      flexDirection: "column",
      alignItems: "center",
    },
    creatorSelect: {
      margin: 4,
      minWidth: 220,
    },
    cardSelection: {
      border: `1px solid ${theme.palette.grey[500]}`,
      minHeight: 36,
      padding: ".25rem 1rem",
      marginBottom: ".5rem",
      backgroundColor: "white",
      display: "flex",
      alignItems: "center",
      "&:hover": {
        "& $cardSelectionText": {
          width: "calc(100% - 24px)",
        },
        "& $cardSelectionTextShort": {
          width: "calc(100% - 48px - 0.25rem)",
        },
        "& $cardSelectionIcons": {
          display: "flex",
          alignItems: "center",
        },
      },
    },
    cardSelectionDisabled: {
      border: `1px solid ${theme.palette.grey[400]}`,
      color: theme.palette.grey[400],
    },
    cardSelectionText: {
      width: "100%",
      wordWrap: "break-word",
    },
    cardSelectionTextShort: {},
    cardSelectionIcons: {
      display: "none",
    },
    cardSelectionAdd: {
      display: "flex",
      alignItems: "center",
      cursor: "pointer",
      marginLeft: ".25rem",
      "&:hover": {
        color: theme.palette.primary.main,
      },
    },
    container: {
      display: "flex",
      justifyContent: "center",
      alignItems: "center",
      border: "1px dashed gray",
      height: "3rem",
      width: "45%",
      marginBottom: "1rem",
    },
    iconSelected: {
      color: theme.palette.primary.main,
    },
  };
});
