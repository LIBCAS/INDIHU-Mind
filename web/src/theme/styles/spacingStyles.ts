import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    m1: {
      margin: theme.spacing(1),
    },
    m2: {
      margin: theme.spacing(2),
    },
    m3: {
      margin: theme.spacing(2),
    },
    mtAuto: {
      marginTop: "auto",
    },
    mt1: {
      marginTop: theme.spacing(1),
    },
    mt2: {
      marginTop: theme.spacing(2),
    },
    mt3: {
      marginTop: theme.spacing(3),
    },
    mrAuto: {
      marginRight: "auto",
    },
    mr1: {
      marginRight: theme.spacing(1),
    },
    mr2: {
      marginRight: theme.spacing(2),
    },
    mr3: {
      marginRight: theme.spacing(3),
    },
    mbAuto: {
      marginBottom: "auto",
    },
    mb1: {
      marginBottom: theme.spacing(1),
    },
    mb2: {
      marginBottom: theme.spacing(2),
    },
    mb3: {
      marginBottom: theme.spacing(3),
    },
    mlAuto: {
      marginLeft: "auto",
    },
    ml1: {
      marginLeft: theme.spacing(1),
    },
    ml2: {
      marginLeft: theme.spacing(2),
    },
    ml3: {
      marginLeft: theme.spacing(3),
    },
    p0: {
      padding: "0",
    },
    p1: {
      padding: theme.spacing(1),
    },
    p2: {
      padding: theme.spacing(2),
    },
    p3: {
      padding: theme.spacing(3),
    },
    pt1: {
      paddingTop: theme.spacing(1),
    },
    pt2: {
      paddingTop: theme.spacing(2),
    },
    pt3: {
      paddingTop: theme.spacing(3),
    },
    pb1: {
      paddingBottom: theme.spacing(1),
    },
    pb2: {
      paddingBottom: theme.spacing(2),
    },
    pb3: {
      paddingBottom: theme.spacing(3),
    },
    pl1: {
      paddingLeft: theme.spacing(1),
    },
    pl2: {
      paddingLeft: theme.spacing(2),
    },
    pl3: {
      paddingLeft: theme.spacing(3),
    },
    pr1: {
      paddingRight: theme.spacing(1),
    },
    pr2: {
      paddingRight: theme.spacing(2),
    },
    pr3: {
      paddingRight: theme.spacing(3),
    },
    centerHorizontally: {
      marginLeft: "auto",
      marginRight: "auto",
      display: "block",
    },
  };
});
