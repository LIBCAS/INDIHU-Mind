import { makeStyles } from "@material-ui/styles";

export const useStyles = makeStyles(() => {
  return {
    root: {
      position: "fixed",
      height: "8px",
      bottom: "0",
      left: "0",
      zIndex: 999999,
      width: "100%"
    },
    local: {
      position: "absolute",
      top: 0,
      left: 0,
      zIndex: 1200
    }
  };
});
