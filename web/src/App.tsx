import React from "react";
import CssBaseline from "@material-ui/core/CssBaseline";
import { ThemeProvider } from "@material-ui/styles";
import { BrowserRouter } from "react-router-dom";

import "./theme/global.css";
import { theme } from "./theme/theme";
import { Status } from "./modules/status/Status";
import { Context } from "./context/Context";
import { Router } from "./router/Router";

const App: React.FC = () => (
  <Context>
    <ThemeProvider theme={theme}>
      <Status />
      <BrowserRouter>
        <Router />
      </BrowserRouter>
      <CssBaseline />
    </ThemeProvider>
  </Context>
);

export default App;
