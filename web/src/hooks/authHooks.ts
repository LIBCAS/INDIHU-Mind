import { useState, useEffect, useCallback } from "react";

import * as store from "../utils/store";
import { decodeToken, renewToken, validateTokenExp } from "../utils/token";
import { useInterval } from "./timerHooks";

type Token = ReturnType<typeof decodeToken>;

export const useUserToken = () => {
  // Token
  const [token, setToken] = useState<Token>(decodeToken(store.get("token")));

  // Handlers
  const handleTokenChange = useCallback((newToken: string) => {
    const decodedToken = decodeToken(newToken);
    const isTokenValid = validateTokenExp(decodedToken);
    // If token is invalid/expired, delete it
    if (!isTokenValid && newToken) {
      store.remove("token");
    } else {
      setToken(decodedToken);
    }
  }, []);

  useEffect(() => {
    let watchId: string | undefined;
    // Watch for token change
    watchId = store.watch("token", handleTokenChange);
    // Set new token
    const newToken: string = store.get("token");
    // Decode token
    handleTokenChange(newToken);

    return () => {
      if (watchId) {
        store.unwatch(watchId);
      }
    };
  }, [handleTokenChange]);

  return token;
};

export const useTokenRefresh = () => {
  const token = useUserToken();
  const hasToken = token !== null;
  const handleRefreshToken = useCallback(async () => {
    if (hasToken) {
      renewToken().then(newToken => {
        if (newToken === null) {
          // TODO: handle no refresh token, new token is handled by api
        }
      });
    }
  }, [hasToken]);

  // Repeating refresh every 3 minutes
  useInterval(handleRefreshToken, 1000 * 60 * 4);

  // Init refresh
  useEffect(() => {
    handleRefreshToken();
  }, [handleRefreshToken]);

  return token;
};
