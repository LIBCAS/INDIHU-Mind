import { Theme } from "@material-ui/core/styles/createMuiTheme";
import { makeStyles } from "@material-ui/styles";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    wrapper: {
      width: "100%",
      cursor: "pointer",
      padding: `4px`,
      // marginBottom: theme.spacing(1),
      "&:hover": {
        "& p": {
          color: theme.palette.grey[100],
        },
        backgroundColor: theme.palette.primary.dark,
      },
      "&:hover $wrapperIcons": {
        opacity: 1,
      },
    },
    innerWrapper: {
      display: "flex",
      // flexWrap: "wrap",
      alignItems: "center",
      padding: "5px",
    },
    wrapperActive: {
      borderRadius: "350px",
      background: "#313840",
      "& $wrapperIcons": {
        opacity: 1,
      },
    },
    wrapperIcons: {
      display: "flex",
      opacity: 0,
      color: theme.greyText,
      marginLeft: "auto",
    },
    label: {
      color: "#A0A1A3",
      paddingLeft: theme.spacing(1),
      fontSize: 15,
    },
    labelActive: {
      color: "#fff",
    },
    dot: {
      display: "inline-block",
      minWidth: 8,
      minHeight: 8,
      borderRadius: "50%",
      marginLeft: theme.spacing(1),
      marginRight: theme.spacing(1),
    },
    createButton: {
      marginBottom: theme.spacing(1),
    },
  };
});
