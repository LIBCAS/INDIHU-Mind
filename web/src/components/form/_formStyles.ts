import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";
import { green, red } from "@material-ui/core/colors";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    title: {
      fontSize: 20,
      padding: `${theme.spacing(1.25)}px ${theme.spacing(2)}px ${theme.spacing(
        1
      )}px ${theme.spacing(2)}px`
    },
    default: {
      padding: `${theme.spacing(1)}px`,
      borderRadius: "3px",
      border: `1px solid ${theme.greyLight}`,
      "&:focus": {
        background: "#fff",
        border: `1px solid ${theme.palette.primary.main}`
      }
    },
    errorBorder: {
      borderRadius: "3px",
      border: `1px solid ${theme.palette.error.main}`,
      "&:focus": {
        background: "#fff",
        border: `1px solid ${theme.palette.error.main}`
      }
    },
    active: {
      borderRadius: "3px",
      border: `2px solid grey`,
      "&:focus": {
        background: "#fff",
        border: `2px solid ${theme.palette.primary.main}`
      }
    },
    selectError: {
      marginLeft: theme.spacing(1)
    },
    select: {
      padding: `${theme.spacing(1)}px ${theme.spacing(2.6)}px ${theme.spacing(
        1
      )}px ${theme.spacing(1)}px`
    },
    textArea: {
      boxSizing: "border-box"
    },
    label: {
      width: "100%",
      margin: `${theme.spacing(2)}px 0 ${theme.spacing(1)}px 0`
    },
    error: {
      width: "100%"
    },
    placeholder: {
      color: theme.greyText
    },
    dateTimePickerInput: {
      width: "100%"
    },
    colorPickerInput: {
      width: "100%",
      padding: "5px",
      background: "#fff",
      borderRadius: "1px",
      boxShadow: "0 0 0 1px rgba(0,0,0,.1)",
      display: "block",
      cursor: "pointer"
    },
    colorPickerColor: {
      width: "100%",
      height: "20px",
      borderRadius: "2px"
    },
    chromePickerWrapper: {
      position: "absolute",
      top: "0",
      left: "0",
      zIndex: 99999,
      width: "100vw",
      height: "100vh"
    },
    chromePickerContent: {
      display: "flex",
      position: "relative",
      top: "50%",
      left: "50%",
      transform: "translate(-50%, -50%)",
      justifyContent: "center"
    },
    chromePickerActions: {
      display: "flex",
      justifyContent: "space-between",
      position: "absolute",
      top: "100%",
      width: "225px",
      background: "#fff"
    },
    nestedSelect: {
      padding: "0",
      minHeight: "44px"
    },
    nestedSelectList: {
      // maxWidth: "500px",
      "&:focus": {
        outline: "none"
      }
    },
    nestedSelectItem: {
      paddingLeft: "0",
      paddingTop: "0",
      paddingBottom: "0"
    },
    nestedSelectItemText: {
      paddingLeft: "10px !important",
      paddingTop: "11px",
      paddingBottom: "11px"
    },
    nestedSelectChip: {
      marginTop: "5px",
      marginRight: "5px",
      marginBottom: "5px",
      marginLeft: "5px",
      height: "auto",
      "& > span": {
        whiteSpace: "normal",
        padding: "5px 12px"
      }
    },

    switchBase: {
      "&$disabled": {
        color: red[500],
        "& + $track": {
          backgroundColor: red[500],
          opacity: 0.5
        },
        "&$checked": {
          color: green[500]
        },
        "&$checked + $track": {
          backgroundColor: green[500],
          opacity: 0.5
        }
      }
    },
    checked: {},
    track: {},
    disabled: {}
  };
});
