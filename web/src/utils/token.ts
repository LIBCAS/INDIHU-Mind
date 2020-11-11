import decodeJWT from "jwt-decode";

import { api } from "./api";

export type ApiUserRole = "ROLE_USER" | "ROLE_ADMIN";

export interface ApiUser {
  authorities: string[];
  // issued at
  iat: string;
  exp: string;
  // userId
  sub: string;
  email: string;
}

export const decodeToken = (token: string) => {
  let decodedToken: ApiUser | null;
  // Decode token
  try {
    decodedToken = decodeJWT(token);
  } catch (error) {
    decodedToken = null;
  }

  return decodedToken;
};

export const renewToken = (url: string = "keepalive") => {
  return api()
    .get(url)
    .then((response) => {
      if (response.ok && response.headers.has("bearer")) {
        return response.headers.get("bearer");
      } else {
        return null;
      }
    })
    .catch(() => {
      return null;
    });
};

export const validateTokenExp = (user: ApiUser | null) => {
  if (user !== null && typeof user.exp === "number") {
    // If exp date is before now, its invalid
    return new Date().getTime() - new Date(user.exp * 1000).getTime() < 0;
  }
  // If exp date is not a number, its invalid
  return false;
};

export const isAdmin = (token: ReturnType<typeof decodeToken>) =>
  token && token.authorities.indexOf("ROLE_ADMIN") !== -1;
