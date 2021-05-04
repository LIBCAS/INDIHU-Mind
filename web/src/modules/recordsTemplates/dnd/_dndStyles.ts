import { Theme } from "@material-ui/core/styles/createMuiTheme";
import { makeStyles } from "@material-ui/styles";

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
      minWidth: 260,
      maxWidth: 260,
    },
    cardSelection: {
      border: `1px solid ${theme.palette.grey[500]}`,
      minHeight: 36,
      padding: ".25rem 1rem",
      marginBottom: ".5rem",
      backgroundColor: "white",
      display: "flex",
      alignItems: "center",
      width: "100%",
      justifyContent: "space-between",
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
          justifyContent: "flex-end",
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
