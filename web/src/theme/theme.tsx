import { createMuiTheme } from "@material-ui/core/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";
import { isFirefox } from "../utils/browser";

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
    fontFamily: ["Raleway", "sans-serif"].join(","),
  },
  palette: {
    primary: {
      main: "#083d77",
    },
    error: {
      main: "#ff0000",
    },
  },
  breakpoints: {
    values: {
      xs: 0,
      sm: 600,
      md: 960,
      lg: 1280,
      xl: 1460,
      xxl: 1680,
    } as any,
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
        width: "100%",
      },
      item: {
        // fix for ignored padding-bottom in Firefox
        paddingBottom: isFirefox ? 8 : "inherit",
      },
    },
    MuiButton: {
      root: {
        textTransform: "none",
        padding: "10px 20px",
      },
      outlined: {
        textTransform: "none",
        padding: "10px 20px",
      },
    },
    MuiTableRow: {
      root: {
        cursor: "pointer",
        "&$selected": {
          backgroundColor: purpleLight,
        },
      },
    },
    MuiTypography: {
      h6: {
        fontSize: "1.1rem",
      },
    },
    MuiSnackbarContent: {
      root: {
        flexWrap: "nowrap",
      },
    },
    MuiIconButton: {
      root: {
        color: blackIconColor,
      },
    },
    MuiListItemIcon: {
      root: {
        color: blackIconColor,
      },
    },
    MuiCollapse: {
      hidden: {
        position: "absolute",
        opacity: 0,
      },
      container: {
        transition: "all 300ms ease-out",
      },
      entered: {
        opacity: 1,
      },
    },
  },
});
