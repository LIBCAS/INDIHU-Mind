import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
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
        color: "#fff"
      },
      zIndex: 999
    },
    variantPopover: {
      transform: "translate(0%, -0%)"
    },
    cancelIcon: {
      fontSize: 18
    }
  };
});
