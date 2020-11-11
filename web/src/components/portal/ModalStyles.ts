import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    modal: {
      position: "absolute",
      width: "95vw",
      maxHeight: "95%",
      maxWidth: "95vw",
      top: `50%`,
      left: `50%`,
      transform: `translate(-50%, -50%)`,
      "&:focus": {
        outline: "none",
      },
      [theme.breakpoints.up("sm")]: {
        width: "auto",
        minWidth: "300px",
      },
      [theme.breakpoints.up("md")]: {
        width: "auto",
      },
    },
    cancelWrapper: {
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
      position: "absolute",
      top: 0,
      right: 0,
      height: "30px",
      width: "30px",
      cursor: "pointer",
      transform: "translate(25%, -25%)",
      borderRadius: "50%",
      border: "none",
      backgroundColor: theme.greyLightUltra,
      color: theme.greyText,
      transition: ".3s ease background, .3s ease color",
      "&:hover, &:focus, &:active": {
        outline: "none",
        background: theme.palette.secondary.main,
        color: "#fff",
      },
      zIndex: 999,
    },
    cancelIcon: {
      fontSize: 18,
    },
    modalContentWrapper: {
      maxHeight: "85vh",
      overflowX: "visible",
      overflowY: "auto",
      [theme.breakpoints.up("md")]: {
        maxHeight: "95vh",
      },
    },
    modalContentWrapperFull: {
      maxHeight: "90vh",
      minHeight: "90vh",
      maxWidth: "90vw",
      minWidth: "90vw",
      [theme.breakpoints.up("md")]: {
        maxHeight: "85vh",
        minHeight: "85vh",
        maxWidth: "85vw",
        minWidth: "85vw",
      },
    },
    modalContent: {
      position: "relative",
      // overflowX: "visible",
      overflowX: "hidden",
      overflowY: "auto",
      background: "#fff",
    },
    modalContentFull: {
      maxHeight: "90vh",
      minHeight: "90vh",
      maxWidth: "90vw",
      minWidth: "90vw",
      [theme.breakpoints.up("md")]: {
        maxHeight: "85vh",
        minHeight: "85vh",
        maxWidth: "85vw",
        minWidth: "85vw",
      },
    },
    modalWithPadding: {
      padding: theme.spacing(2),
    },
  };
});
