import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    content: {
      maxWidth: "calc(100% - 2em)",
      overflow: "hidden",
      "& > span > span": {
        display: "inline-block",
        maxWidth: "100%",
        overflow: "hidden",
        textOverflow: "ellipsis",
      },
    },
    attachmentCardRow: { display: "flex", flexDirection: "column" },
    attachmentCardLabel: {
      display: "flex",
      flexDirection: "row",
      justifyContent: "space-between",
      flexWrap: "wrap",
    },
    attachmentCardValue: {
      fontWeight: 600,
      maxWidth: "100%",
      wordWrap: "break-word",
    },
  };
});
