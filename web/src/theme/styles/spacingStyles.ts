import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    m1: {
      margin: theme.spacing(1)
    },
    m2: {
      margin: theme.spacing(2)
    },
    mtAuto: {
      marginTop: "auto"
    },
    mt1: {
      marginTop: theme.spacing(1)
    },
    mt2: {
      marginTop: theme.spacing(2)
    },
    mt3: {
      marginTop: theme.spacing(3)
    },
    mrAuto: {
      marginRight: "auto"
    },
    mr1: {
      marginRight: theme.spacing(1)
    },
    mr2: {
      marginRight: theme.spacing(2)
    },
    mr3: {
      marginRight: theme.spacing(3)
    },
    mbAuto: {
      marginBottom: "auto"
    },
    mb1: {
      marginBottom: theme.spacing(1)
    },
    mb2: {
      marginBottom: theme.spacing(2)
    },
    mlAuto: {
      marginLeft: "auto"
    },
    ml1: {
      marginLeft: theme.spacing(1)
    },
    ml2: {
      marginLeft: theme.spacing(2)
    },
    ml3: {
      marginLeft: theme.spacing(3)
    },
    p0: {
      padding: "0"
    },
    p1: {
      padding: theme.spacing(1)
    }
  };
});
