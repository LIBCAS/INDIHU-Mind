import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => ({
  printWrapper: {
    "& $printContent": {
      display: "block"
    }
  },
  printContent: {
    display: "none",
    "-webkit-print-color-adjust": "exact !important",
    "& *": {
      fontSize: 14,
      "font-family": "'Arial' !important",
      color: "#111111"
    }
  },
  printTableWrapper: {
    padding: 14
  },
  printTable: {
    borderCollapse: "collapse",
    borderSpacing: 0,
    width: "100%",
    maxWidth: "100%",
    tableLayout: "fixed"
  },
  printTableHead: {
    backgroundColor: "#6894ff"
  },
  printTableCell: {
    textAlign: "left",
    verticalAlign: "bottom",
    padding: 5,
    wordBreak: "break-word",
    whiteSpace: "normal",
    border: "1px solid black",
    height: 24
  },
  printTableHeadCell: {
    verticalAlign: "top",
    height: "auto"
  }
}));
