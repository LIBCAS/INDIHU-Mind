import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";
import { rgbToHex } from "@material-ui/core";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    createButton: {
      width: "100%",
      marginTop: theme.spacing(1),
      [theme.breakpoints.up("sm")]: {
        marginTop: "0",
        width: "33%"
      }
    },
    categoryItem: {
      display: "flex",
      minWidth: "250px",
      flexGrow: 0,
      flexDirection: "column",
      justifyContent: "space-around",
      //border: `1px solid ${theme.greyLight}`,
      //borderRadius: "3px",
      margin: "20px",
      paddingBottom: theme.spacing(1),
      transition: "all .3s ease-out, width 0s",
      "&:hover": {
        background: theme.purpleLight
      },
      [theme.breakpoints.up("sm")]: {
        alignItems: "center",
        width: "320px"
      }
    },

    categoryItemFullWidth: {
      marginTop: "20px",
      padding: theme.spacing(1),
      alignSelf: "flex-start",
      flexGrow: 1,
      [theme.breakpoints.up("sm")]: {
        flexDirection: "row",
        width: "calc(100% - 20px)",
        height: "70px"
      },
      margin: "0 20px",
      [theme.breakpoints.down(360)]: {
        padding: 0,
        paddingBottom: theme.spacing(1)
      }
    },
    subCategoryItem: {
      margin: "20px 20px 0 20px"
    },
    counter: {
      marginBottom: theme.spacing(1),
      maxWidth: "80px",
      textAlign: "center",
      flexDirection: "row",
      flexWrap: "nowrap",
      [theme.breakpoints.up("sm")]: {
        width: "auto",
        paddingLeft: theme.spacing(3)
      },
      [theme.breakpoints.up("sm")]: {
        marginBottom: "0",
        "&:first-child": {
          marginRight: "20px"
        }
      },
      [theme.breakpoints.down(360)]: {
        maxWidth: "none",
        margin: 0
      }
    },
    counterFullWidth: {
      [theme.breakpoints.up("sm")]: {
        flexDirection: "row",
        flexWrap: "nowrap",
        justifyContent: "space-around",
        marginLeft: theme.spacing(2)
      }
    },
    iconsWrapper: {
      cursor: "pointer",
      [theme.breakpoints.up("sm")]: {
        marginLeft: theme.spacing(1)
      }
    },
    subCategoryWrapper: {
      marginTop: "20px"
    },
    subCategoryLine: {
      borderLeft: `1px solid ${theme.greyText}`,
      marginLeft: "20px"
    },
    subCategoriesCollapse: {
      width: "100%",
      maxWidth: "100%"
    },
    categoryName: {
      display: "flex",
      flexDirection: "row",
      justifyContent: "space-between",
      width: "100%",
      fontWeight: 700,
      textAlign: "center",
      marginBottom: 0,
      paddingRight: "auto",
      fontSize: "1.3em",
      padding: "10px 5px 10px 20px"
    },
    categoryNameClosed: {
      borderBottom: `1px solid ${theme.greyLight}`,
      marginBottom: theme.spacing(1)
    },
    categoriesContainer: {
      display: "flex",
      flexDirection: "row",
      flexWrap: "wrap",
      justifyContent: "center",
      [theme.breakpoints.up("md")]: {
        justifyContent: "start"
      }
    },
    countersContainer: {
      minWidth: "300px",
      marginRight: "auto",
      marginTop: theme.spacing(1.5),
      marginBottom: theme.spacing(1),
      justifyContent: "space-around",
      [theme.breakpoints.down(360)]: {
        flexDirection: "column",
        alignItems: "center",
        minWidth: 0,
        margin: 0
      }
    },
    countText: {
      fontSize: "1.5em",
      margin: theme.spacing(2)
    },
    subCategoriesCollapseRoot: {
      paddingRight: "20px"
    },
    categoryItemOpened: {
      "&:hover": {
        background: theme.blueLightHover
      },
      background: theme.blueLight
    }
  };
});
