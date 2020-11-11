import ky from "ky";
import { map } from "lodash";

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
    hooks: {
      beforeRequest: [
        (request: any) => {
          if (!opts.noContentType) {
            request.headers.set("Content-type", "application/json");
          }

          map(headers, (value, key) => request.headers.set(key, value));

          if (!skipToken) {
            // If we have token, use it in request
            const token = store.get("token", undefined);
            if (token) {
              request.headers.set("Authorization", `Bearer ${token}`);
            }
          }
        },
      ],
      afterResponse: [
        async (_: any, __: any, response: any) => {
          // If response contains bearer, save it as token
          if (response.headers.has("bearer")) {
            store.set("token", response.headers.get("bearer"));
          }
          if (!response.ok) {
            const body = await response.json();
            throw new ky.HTTPError(body);
          }
        },
      ],
    },
  });
};
