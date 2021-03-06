import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    textGreyLight: {
      color: theme.greyLight,
    },
    textGrey: {
      color: theme.greyText,
    },
    textGreyDark: {
      color: theme.greyDark,
    },
    textWhite: {
      color: "#fff",
    },
    textBold: {
      fontWeight: 800,
    },
    text600: {
      fontWeight: 600,
    },
    textUppercase: {
      textTransform: "uppercase",
    },
    textLink: {
      color: "#fff",
      fontSize: 16,
      whiteSpace: "nowrap",
      cursor: "pointer",
      "&:hover": {
        textDecoration: "none",
      },
      "&:not(:last-child)": {
        marginRight: theme.spacing(4),
      },
    },
    noWrap: {
      whiteSpace: "nowrap",
      textOverflow: "ellipsis",
      overflow: "hidden",
    },
    textLeft: {
      textAlign: "left",
    },
    textRight: {
      textAlign: "right",
    },
    textCenter: {
      textAlign: "center",
    },
    subtitle: {
      color: theme.greyText,
      fontWeight: 800,
      textTransform: "uppercase",
      fontSize: 14,
    },
    cursor: {
      cursor: "pointer",
    },
    icon: {
      display: "flex",
      color: theme.blackIconColor,
      background: "white", //theme.greyLightUltra,
      borderRadius: "50%",
      transition: ".3s ease color",
      "&:hover": {
        color: theme.palette.primary.main,
      },
    },
    iconChangedBg: {
      background: theme.blueLight,
    },
    iconBig: {
      fontSize: 28,
    },
    error: {
      color: theme.red,
    },
    small: {
      fontSize: 12,
    },
    normal: {
      fontSize: 16,
    },
    textBlack: {
      color: "#000",
    },
  };
});
