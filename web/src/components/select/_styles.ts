import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";
import { emphasize } from "@material-ui/core/styles/colorManipulator";

export const useStyles = makeStyles((theme: Theme) => ({
  root: {
    flexGrow: 1,
    // height: 250,
  },
  input: {
    display: "flex",
    height: "auto",
    padding: `0`,
    borderRadius: "3px",
    border: `1px solid ${theme.greyLight}`,
    "&:focus": {
      background: "#fff",
      border: `1px solid ${theme.palette.primary.main}`,
    },
  },
  valueContainer: {
    display: "flex",
    flexWrap: "wrap",
    flex: 1,
    alignItems: "center",
    overflow: "hidden",
    paddingLeft: "5px",
  },
  chip: {
    margin: theme.spacing(0.5, 0.25),
    height: "auto",
    "& > span": {
      padding: "5px 12px",
      whiteSpace: "normal",
    },
  },
  chipFocused: {
    backgroundColor: emphasize(
      theme.palette.type === "light"
        ? theme.palette.grey[300]
        : theme.palette.grey[700],
      0.08
    ),
  },
  noOptionsMessage: {
    padding: theme.spacing(1, 2),
  },
  singleValue: {
    fontSize: 16,
  },
  placeholder: {
    position: "absolute",
    left: 8,
    bottom: 6,
    fontSize: 16,
    color: theme.greyText,
  },
  paper: {
    position: "absolute",
    zIndex: 1,
    marginTop: theme.spacing(1),
    left: 0,
    right: 0,
    "& > div": {
      maxHeight: 200,
    },
  },
  divider: {
    height: theme.spacing(2),
  },
  createLabel: {
    display: "flex",
    fontWeight: 600,
  },
  createLabelAddIcon: {
    marginLeft: -theme.spacing(0.5),
    marginRight: theme.spacing(0.5),
  },
}));
