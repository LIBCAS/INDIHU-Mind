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
    blueLight: string;
    blueLightHover: string;
    blackIconColor: string;
  }
  // allow configuration using `createMuiTheme`
  interface ThemeOptions {
    greyText: string;
    greyLight: string;
    greyLightUltra: string;
    greyDark: string;
    purpleLight: string;
    red: string;
    blueLight: string;
    blueLightHover: string;
    blackIconColor: string;
  }
}

const greyLight = "#D3D3D3";
const greyLightUltra = "#F9FAFE";
const purpleLight = "#EAEAF4";
const blueLight = "#ebf1f7";
const blackIconColor = "#111";

export const theme: Theme = createMuiTheme({
  typography: {
    fontFamily: ["Raleway", "sans-serif"].join(",")
  },
  palette: {
    primary: {
      main: "#083d77"
    },
    error: {
      main: "#ff0000"
    }
  },
  greyText: "#B0B0B0",
  greyLight,
  greyLightUltra,
  greyDark: "#696969",
  purpleLight,
  red: "#ff0000",
  blueLight,
  blackIconColor,
  blueLightHover: "#dfe4eb",
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
    },
    MuiIconButton: {
      root: {
        color: blackIconColor
      }
    },
    MuiListItemIcon: {
      root: {
        color: blackIconColor
      }
    },
    MuiCollapse: {
      hidden: {
        position: "absolute",
        transform: "translate3d(30px,0,0)",
        opacity: 0
      },
      container: {
        transition: "all 300ms ease-out"
      },
      entered: {
        transform: "translate3d(0,0,0)",
        opacity: 1
      }
    }
  }
});
