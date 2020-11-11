import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    categoryFormWrapper: {
      [theme.breakpoints.up("md")]: {
        width: 550,
      },
    },
    category: {
      display: "inline-flex",
      alignItems: "center",
      fontWeight: 800,
      fontSize: 14,
      marginRight: theme.spacing(1),
      cursor: "pointer",
      padding: "0 4px 0 8px",
      margin: "4px",
      borderRadius: "5px",
      backgroundColor: "#e0e0e0",
      transition: ".3s ease color",
      "& > div": {
        padding: `${theme.spacing(0.5)}px 0`,
        marginRight: theme.spacing(0.5),
      },
      "& svg": {
        display: "block",
        opacity: 1,
        color: theme.palette.action.active,
        transition: ".3s ease-in opacity",
        [theme.breakpoints.up("md")]: {
          opacity: 0,
        },
      },
      "&:hover": {
        color: theme.palette.primary.main,
        "& svg": {
          display: "block",
          opacity: 1,
        },
      },
    },
    removeCategory: {
      "&:hover": {
        color: theme.red,
      },
    },
    label: {
      display: "inline-flex",
      alignItems: "center",
      color: theme.greyDark,
      padding: "4px 0 4px 12px",
      background: "#e0e0e0",
      borderRadius: "15px",
      cursor: "pointer",
      transition: ".3s ease color",
      "& svg": {
        display: "block",
        opacity: 1,
        color: theme.palette.action.active,
        transition: ".3s ease-in opacity",
        [theme.breakpoints.up("md")]: {
          opacity: 0,
        },
      },
      "&:hover svg": {
        display: "block",
        opacity: 1,
      },
      "&:hover": {
        color: theme.palette.primary.main,
      },
    },
    labelText: {
      fontSize: 14,
      paddingRight: "8px",
    },
    labelDot: {
      display: "inline-block",
      width: "8px",
      height: "8px",
      marginRight: "8px",
      borderRadius: "50%",
    },
    linkedCardsAddButtonWrapper: {
      display: "flex",
      marginTop: theme.spacing(1.5),
    },
    addWrapper: {
      position: "relative",
      marginTop: theme.spacing(1.5),
      width: "100%",
      [theme.breakpoints.up("md")]: {
        width: "calc(50% - 8px)",
      },
      [theme.breakpoints.up("lg")]: {
        width: "calc(33% - 8px)",
      },
    },
    attributeWrapper: {
      display: "flex",
      flexDirection: "column",
      width: "100%",
      padding: theme.spacing(2),
      paddingTop: 0,
    },
    actionWrapper: {
      display: "flex",
      flexDirection: "row-reverse",
      justifyContent: "space-between",
      marginTop: theme.spacing(2),
    },
    titleTooltip: {
      arrow: {
        color: theme.palette.common.black,
      },
    },
    addLinkedCardWrapper: {
      maxWidth: 400,
    },
    attributeItemWrapper: {
      padding: theme.spacing(1.5),
      border: `1px solid ${theme.greyLight}`,
      width: "fit-content",
      borderRadius: 5,
      maxWidth: "100%",
      minWidth: 150,
    },
    attributeItemNotEditing: {
      cursor: "pointer",
    },
    arrowsWrapper: {
      marginBottom: theme.spacing(1),
      marginRight: theme.spacing(0.5),
    },
    id: {
      fontSize: 13,
      color: theme.palette.grey[600],
    },
  };
});
export const useTooltipStyles = makeStyles((theme: Theme) => ({
  arrow: {
    width: "100%",
  },
}));

export const useArrowStyles = makeStyles((theme: Theme) => ({
  root: {
    "&:hover": {
      color: theme.palette.primary.dark,
    },
  },
}));
