import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

export const useStyles = makeStyles((theme: Theme) => {
  return {
    header: {
      position: "fixed",
      width: "100%",
    },
    main: {
      display: "flex",
      height: "100vh",
      background:
        "linear-gradient(rgba(1,1,1,.6),rgba(1,1,1,.6)),url(/assets/images/cover.jpg) 50% no-repeat",
      [theme.breakpoints.down("xs")]: {
        height: "100%",
        padding: "15px",
        paddingTop: "79px",
        flexDirection: "column",
      },
    },
    left: {
      display: "flex",
      flexDirection: "column",
      justifyContent: "center",
      alignItems: "center",
      flex: "8",
      [theme.breakpoints.down("md")]: {
        flex: "4",
      },
      [theme.breakpoints.down("xs")]: {
        flex: "1",
        flexBasis: "100%",
        marginBottom: "15px",
        justifyContent: "flex-start",
      },
    },
    right: {
      display: "flex",
      flexDirection: "column",
      justifyContent: "center",
      alignItems: "center",
      flex: "5",
      [theme.breakpoints.down("md")]: {
        flex: "4",
      },
      [theme.breakpoints.down("xs")]: {
        flex: "1",
        flexBasis: "100%",
        justifyContent: "flex-start",
        marginBottom: "15px",
      },
    },
    content: {
      width: "80%",
      [theme.breakpoints.down("xs")]: {
        width: "100%",
      },
    },
    title: {
      fontSize: "3em",
      fontWeight: 600,
      color: "#fff",
      [theme.breakpoints.down("sm")]: {
        fontSize: "2.3em",
      },
    },
    description: {
      marginTop: "0px",
      fontSize: "1.5em",
      color: "#fff",
      [theme.breakpoints.down("sm")]: {
        fontSize: "1.2em",
      },
    },
    form: {
      background: "#fff",
      padding: "2rem",
    },
    card: {
      maxWidth: "450px",
      width: "90%",
      borderRadius: "0",
      padding: "10px",
      [theme.breakpoints.down("xs")]: {
        width: "100%",
      },
    },
    cardTitle: {
      marginBottom: "10px",
    },
  };
});
