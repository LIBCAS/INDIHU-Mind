import React, { useReducer } from "react";

import { GlobalProvider, initialState } from "./Context";
import { reducerGlobal } from "./reducerGlobal";

export const ContextProvider: React.FC = ({ children }) => {
  const [state, dispatch] = useReducer(reducerGlobal, initialState);
  return (
    <GlobalProvider value={{ state, dispatch }}>{children}</GlobalProvider>
  );
};
