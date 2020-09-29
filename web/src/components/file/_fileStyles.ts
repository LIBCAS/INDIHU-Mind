import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    fileIcons: {
      height: "24px",
      cursor: "pointer",
      opacity: 1,
      transition: ".3s ease opacity"
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
        boxShadow: "0 0 0 2px rgba(0,0,0,.1)",
        "& $fileIcons": {
          opacity: 1
        }
      }
    },
    fileUploadPickerWrapper: {
      position: "relative",
      width: "100%",
      [theme.breakpoints.up("md")]: {
        width: "calc(50% - 8px)"
      },
      [theme.breakpoints.up("lg")]: {
        width: "calc(33% - 8px)"
      }
    },
    fileUploadPickerFormWrapper: {
      display: "flex",
      flexDirection: "column",
      width: "100%",
      padding: theme.spacing(2),
      maxWidth: 400
    },
    fileUploadPickerActionWrapper: {
      display: "flex",
      flexDirection: "row-reverse",
      justifyContent: "space-between",
      marginTop: theme.spacing(2)
    },
    fileUploadPickerFirstStageButtonWrapper: {
      flexDirection: "row"
    }
  };
});
