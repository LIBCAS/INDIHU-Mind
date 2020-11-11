import { lighten } from "@material-ui/core/styles/colorManipulator";
import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => ({
  root: {
    position: "relative",
    width: "100%",
    marginTop: theme.spacing(1),
  },
  table: {
    width: "100%",
    // minWidth: 1020,
  },
  tableWrapper: {
    overflowX: "auto",
  },
  createButton: {
    flexGrow: 1,
  },
  toolbarRoot: {
    paddingRight: theme.spacing(1),
  },
  tableCell: {
    whiteSpace: "nowrap",
    maxWidth: "440px",
    overflow: "hidden",
    textOverflow: "ellipsis",
  },
  highlight:
    theme.palette.type === "light"
      ? {
          color: theme.palette.secondary.main,
          backgroundColor: lighten(theme.palette.secondary.light, 0.85),
        }
      : {
          color: theme.palette.text.primary,
          backgroundColor: theme.palette.secondary.dark,
        },
  spacer: {
    flex: "1 1 100%",
  },
  actions: {
    color: theme.palette.text.secondary,
  },
  createToolbar: {
    width: "100%",
    marginTop: theme.spacing(1),
    [theme.breakpoints.up("md")]: {
      marginTop: "0",
      width: "33%",
    },
  },
  loader: {
    position: "absolute",
    left: "0",
    top: "0",
    width: "100%",
    height: "8px",
    borderRadius: "3px",
  },
  contentRow: {
    "&:hover $icons": {
      opacity: 1,
    },
  },
  iconsWrapper: {
    display: "flex",
    alignItems: "center",
    justifyContent: "flex-end",
    // flexWrap: "wrap",
    minWidth: "54px",
    marginLeft: "5px",
    marginRight: theme.spacing(1),
  },
  icons: {
    padding: "5px",
    color: theme.blackIconColor,
    [theme.breakpoints.up("md")]: {
      opacity: 0,
    },
  },
  tableSortLabel: {
    textTransform: "uppercase",
    fontWeight: 800,
    whiteSpace: "nowrap",
  },
  tableSortLabelIcon: {
    color: "rgba(0, 0, 0, 0.87)",
    opacity: 1,
  },
  groupEditWrapper: {
    display: "flex",
    alignItems: "flex-end",
    flexWrap: "wrap",
    justifyContent: "space-between",
    "@media (min-width:1200px)": {
      flexDirection: "row",
      justifyContent: "flex-start",
    },
  },
  groupEditWrapperNoCheckbox: {
    alignItems: "center",
  },
  groupTitleWrapper: {
    display: "flex",
    alignItems: "center",
    width: "100%",
    flexWrap: "wrap",
    "@media (min-width:1200px)": {
      width: "auto",
    },
  },
  groupTitle: {
    marginRight: theme.spacing(1),
    minWidth: 190,
    "@media (min-width:1200px)": {
      width: "auto",
    },
  },
  groupInputWrapper: {
    width: "100%",
    "@media (min-width:700px)": {
      width: "48%",
    },
    "@media (min-width:1200px)": {
      marginRight: theme.spacing(1),
      width: 320,
    },
  },
  groupInputWrapperSelectedRow: {
    "@media (min-width:1200px)": {
      width: "auto",
      flex: 1,
    },
  },
  groupSubmit: {
    marginRight: theme.spacing(1),
  },
  paginationToolbar: {
    [theme.breakpoints.down("xs")]: {
      marginTop: "10px",
      flexWrap: "wrap",
      height: "auto",
    },
  },
  paginationCaption: {
    [theme.breakpoints.down("xs")]: {
      width: "50%",
    },
  },
  paginationSelect: {
    [theme.breakpoints.down("xs")]: {
      marginLeft: "auto",
      marginRight: "22px",
    },
  },
  paginationActions: {
    [theme.breakpoints.down("xs")]: {
      marginLeft: "auto",
    },
  },
  checkbox: {
    width: "42px",
    padding: "0 0 0 4px",
  },
  cardsWrapper: {
    display: "flex",
    flexWrap: "wrap",
    maxWidth: "100%",
    "& > div": {
      width: "100%",
    },
    [theme.breakpoints.up("sm")]: {
      marginRight: `-${theme.spacing(2)}px`,
      "& > div": {
        width: `calc(50% - ${theme.spacing(2)}px)`,
        marginRight: theme.spacing(2),
      },
    },
    [theme.breakpoints.up("md")]: {
      "& > div": {
        width: `calc(33% - ${theme.spacing(2)}px)`,
        marginRight: theme.spacing(2),
      },
    },
    [theme.breakpoints.up("lg")]: {
      "& > div": {
        width: `calc(25% - ${theme.spacing(2)}px)`,
        marginRight: theme.spacing(2),
      },
    },
  },
  toolbar: {
    display: "flex",
    alignItems: "flex-end",
    [theme.breakpoints.up(1548)]: {
      flexWrap: "wrap",
    },
  },
  tilesPagination: {
    width: "100%",
    marginBottom: 200,
  },
  pageSizeSelect: {
    width: 75,
  },
}));
