import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    createButton: {
      width: "100%",
      marginTop: theme.spacing(1),
      [theme.breakpoints.up("md")]: {
        marginTop: "0",
        width: "33%"
      }
    },
    categoryItem: {
      display: "flex",
      width: "100%",
      flexDirection: "column",
      justifyContent: "center",
      padding: "15px 20px",
      border: `1px solid ${theme.greyLight}`,
      borderRadius: "3px",
      marginBottom: "20px",
      transition: ".3s ease background, .3s ease border",
      "&:hover": {
        background: theme.purpleLight,
        border: `1px solid ${theme.purpleLight}`
      },
      [theme.breakpoints.up("md")]: {
        flexDirection: "row",
        alignItems: "center"
      }
    },
    categoryItemLast: {
      marginBottom: "0"
    },
    counter: {
      marginBottom: theme.spacing(1),
      width: "100%",
      [theme.breakpoints.up("sm")]: {
        width: "auto",
        "&:first-child": {
          marginRight: theme.spacing(3)
        }
      },
      [theme.breakpoints.up("md")]: {
        marginBottom: "0",
        marginLeft: theme.spacing(2),
        "&:first-child": {
          marginRight: "0"
        }
      }
    },
    iconsWrapper: {
      cursor: "pointer",
      [theme.breakpoints.up("md")]: {
        marginLeft: theme.spacing(1)
      }
    },

    subCategoryWrapper: {
      marginLeft: theme.spacing(1),
      paddingLeft: theme.spacing(1),
      marginBottom: "0"
    },
    subCategoryNotLast: {
      marginBottom: "20px"
    },
    subCategoryLine: {
      borderLeft: `1px solid ${theme.greyText}`
    },
    categoryName: {
      fontWeight: 800,
      textAlign: "center",
      marginBottom: theme.spacing(1),
      [theme.breakpoints.up("md")]: {
        marginRight: "auto",
        marginBottom: "0"
      }
    }
  };
});
