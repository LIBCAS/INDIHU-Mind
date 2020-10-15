import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    attributeWrapper: {
      display: "flex",
      flexDirection: "column",
      width: "100%",
      padding: theme.spacing(2)
    },
    title: {
      padding: "5px 0 5px 20px"
    },
    label: {
      margin: `${theme.spacing(2)}px 0 ${theme.spacing(1)}px 0`
    },
    actionWrapper: {
      display: "flex",
      flexDirection: "row-reverse",
      justifyContent: "space-between",
      marginTop: theme.spacing(2)
    },
    atributeFieldwrapper: {
      [theme.breakpoints.up("md")]: {
        width: "calc(50% - 8px)"
      }
    },
    wrapper: {
      display: "flex",
      flexDirection: "column",
      width: "100%",
      paddingBottom: theme.spacing(2),
      [theme.breakpoints.up("sm")]: {
        width: "560px"
      }
    },
    subWrapper: {
      display: "flex",
      flexDirection: "column",
      padding: `${theme.spacing(2)}px ${theme.spacing(2)}px 0 ${theme.spacing(
        2
      )}px `
    },
    addWrapper: {
      position: "relative",
      marginTop: theme.spacing(1.5),
      [theme.breakpoints.up("md")]: {
        width: "calc(50% - 8px)"
      }
    },
    outsidePanel: {
      display: "flex",
      padding: "10px 5px",
      borderTopLeftRadius: "3px",
      borderBottomLeftRadius: "3px",
      background: "#fff",
      color: "#fff",
      "& > div:not(:last-child)": {
        marginRight: "10px"
      },
      [theme.breakpoints.up("md")]: {
        position: "absolute",
        flexDirection: "column",
        top: "50%",
        left: "0",
        transform: "translate(-100%, -50%)",
        "& > div:not(:last-child)": {
          marginBottom: "10px"
        }
      }
    },
    outsidePanelItemActive: {
      color: theme.palette.primary.main
    },
    templateWrapper: {
      display: "flex",
      justifyContent: "space-between",
      flexWrap: "wrap",
      padding: "20px"
    },
    templateItem: {
      display: "flex",
      width: "100%",
      flexWrap: "wrap",
      padding: "10px 0 5px 0",
      marginBottom: "15px",
      border: `1px solid ${theme.greyLight}`,
      transition: ".3s ease box-shadow",
      [theme.breakpoints.up("md")]: {
        width: "calc(50% - 8px)"
      },
      "&:hover": {
        boxShadow: "0px 0px 5px 0px rgba(0,0,0,0.25)"
      }
    },
    templateItemContent: {
      display: "flex",
      padding: "0 5px"
    },
    templateItemIcon: {
      display: "flex",
      marginTop: "-8px",
      justifyContent: "center"
    },
    templateItemText: {
      width: "100%",
      paddingLeft: "5px"
    },
    templateItemActions: {
      display: "flex",
      width: "100%",
      alignItems: "center",
      marginTop: "10px",
      paddingLeft: "5px"
    },
    templateItemSelect: {
      width: "100%",
      fontWeight: 800,
      textTransform: "none"
    },
    templateItemMenu: {
      marginLeft: "auto",
      padding: "5px",
      color: theme.greyText
    },
    wrapperHalfItems: {
      display: "flex",
      flexWrap: "wrap",
      justifyContent: "space-between",
      "& > div": {
        width: "100%",
        [theme.breakpoints.up("md")]: {
          width: "calc(50% - 8px)"
        }
      }
    },
    cardLinked: {
      height: "140px",
      width: "100%",
      marginTop: theme.spacing(1.5),
      borderTop: `1px solid ${theme.greyLight}`,
      borderLeft: `1px solid ${theme.greyLight}`,
      borderRight: `1px solid ${theme.greyLight}`,
      borderTopLeftRadius: "5px",
      borderTopRightRadius: "5px",
      padding: "12px 8px"
    },

    cardLinkedNote: {
      overflow: "hidden",
      background:
        "linear-gradient(to bottom, rgba(0,0,0,1) 0%, rgba(0,0,0,1) 25%, rgba(0,0,0,0) 100%)",
      backgroundClip: "text",
      "-webkit-background-clip": "text",
      color: "transparent"
    },
    cardLinkedButton: {
      borderTop: `1px solid ${theme.greyLight}`,
      borderLeft: `1px solid ${theme.greyLight}`,
      borderRight: `1px solid ${theme.greyLight}`
    },
    cardLinkedButtonLast: {
      border: `1px solid ${theme.greyLight}`,
      borderTopLeftRadius: "0",
      borderTopRightRadius: "0",
      borderBottomLeftRadius: "5px",
      borderBottomRightRadius: "5px"
    },
    templateButton: {
      justifyContent: "flex-end",
      [theme.breakpoints.up("sm")]: {
        // justifyContent: "flex-end",
      }
    }
  };
});
