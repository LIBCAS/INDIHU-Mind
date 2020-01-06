import ky, { HTTPError } from "ky";

import * as store from "./store";

interface ApiProps {
  skipToken?: boolean;
  noContentType?: boolean;
}

export const api = (opts: ApiProps = {}, headers: any = {}) => {
  const skipToken = opts.skipToken ? opts.skipToken : false; // default: false
  return ky.extend({
    prefixUrl: "/api",
    timeout: 20000,
    // throwHttpErrors: false,
    hooks: {
      beforeRequest: [
        options => {
          // no headers in options
          // https://github.com/sindresorhus/ky/pull/115
          options.headers = {
            ...(!opts.noContentType && { "Content-type": "application/json" }),
            ...headers
          };
          if (!skipToken) {
            // If we have token, use it in request
            const token = store.get("token", undefined);
            if (token) {
              options.headers = {
                Authorization: `Bearer ${token}`,
                ...options.headers
              };
            }
          }
        }
      ],
      afterResponse: [
        async response => {
          // If response contains bearer, save it as token
          if (response.headers.has("bearer")) {
            store.set("token", response.headers.get("bearer"));
          }
          if (!response.ok) {
            const body = await response.json();
            throw new HTTPError(body);
          }
        }
      ]
    }
  });
};
