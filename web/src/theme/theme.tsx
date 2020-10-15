import { createMuiTheme } from "@material-ui/core/styles";
import { Theme, ThemeOptions } from "@material-ui/core/styles/createMuiTheme";

declare module "@material-ui/core/styles/createMuiTheme" {
  interface Theme {
    greyText: string;
    greyLight: string;
    greyLightUltra: string;
    greyDark: string;
    purpleLight: string;
    red: string;
  }
  // allow configuration using `createMuiTheme`
  interface ThemeOptions {
    greyText: string;
    greyLight: string;
    greyLightUltra: string;
    greyDark: string;
    purpleLight: string;
    red: string;
  }
}

const greyLight = "#D3D3D3";
const greyLightUltra = "#F9FAFE";
const purpleLight = "#EAEAF4";

export const theme: Theme = createMuiTheme({
  typography: {
    fontFamily: ["Raleway", "sans-serif"].join(",")
  },
  palette: {
    primary: {
      main: "#083d77"
    }
  },
  greyText: "#B0B0B0",
  greyLight,
  greyLightUltra,
  greyDark: "#696969",
  purpleLight,
  red: "#ff0000",
  overrides: {
    MuiGrid: {
      container: {
        width: "100%"
      }
    },
    MuiButton: {
      root: {
        textTransform: "none",
        padding: "10px 20px"
      },
      outlined: {
        textTransform: "none",
        padding: "10px 20px"
      }
    },
    MuiTableRow: {
      root: {
        cursor: "pointer",
        "&$selected": {
          backgroundColor: purpleLight
        }
      }
    },
    MuiTypography: {
      h6: {
        fontSize: "1.1rem"
      }
    },
    MuiSnackbarContent: {
      root: {
        flexWrap: "nowrap"
      }
    }
  }
});
