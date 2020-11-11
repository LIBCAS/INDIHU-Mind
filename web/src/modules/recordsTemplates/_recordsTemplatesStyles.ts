import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    createButton: {
      width: "100%",
      marginTop: theme.spacing(1),
      [theme.breakpoints.up("md")]: {
        marginTop: "0",
        width: "33%",
      },
    },
    recordWrapper: {
      border: `1px solid ${theme.palette.grey[300]}`,
      padding: "0 10px",
    },
    recordTemplateItemName: {
      fontWeight: 800,
    },
    recordTemplateItemIcon: {
      color: theme.greyText,
      [theme.breakpoints.up("md")]: {
        opacity: 0,
      },
    },
    recordTemplateItem: {
      display: "flex",
      alignItems: "center",
      padding: "20px 10px",
      border: `1px solid ${theme.greyLight}`,
      borderRadius: "3px",
      marginBottom: "20px",
      "&:hover": {
        background: theme.purpleLight,
      },
      "&:hover $recordTemplateItemIcon": {
        opacity: 1,
      },
    },
    recordTemplateForm: {
      width: "85vw",
      height: "85vh",
      display: "flex",
    },
    recordTemplateFormLeftPanel: {
      height: "100%",
      minHeight: "100%",
      maxHeight: "100%",
      overflowY: "auto",
      width: "calc(50% - 1px)",
      borderRight: `1px solid ${theme.palette.grey[300]}`,
      padding: "20px 20px 0",
      [theme.breakpoints.up("md")]: {
        width: "calc(30% - 1px)",
      },
    },
    recordTemplateFormCustom: {
      width: "100%",
      paddingBottom: 20,
    },
    recordTemplateFormRightPanel: {
      height: "100%",
      minHeight: "100%",
      width: "50%",
      [theme.breakpoints.up("md")]: {
        width: "70%",
      },
    },
    recordTemplateFormMainPanel: {
      height: "calc(100% - 60px - 1px)", // 1px divider
      width: "100%",
      padding: "20px",
      overflowY: "auto",
    },
    recordTemplateFormButtonsPanel: {
      height: 60,
      width: "100%",
      display: "flex",
      justifyContent: "center",
      alignItems: "center",
    },
    preview: {
      border: `1px solid ${theme.palette.grey[300]}`,
      padding: ".5rem 1rem",
      borderRadius: 4,
      wordWrap: "break-word",
    },
  };
});
