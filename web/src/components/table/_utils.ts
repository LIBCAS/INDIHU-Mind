import { isEmpty } from "lodash";

import { api } from "../../utils/api";
import { OrderProps } from "./_types";

export const changeFilter = (
  setFilter: Function,
  query: any,
  order: OrderProps,
  page: number,
  rowsPerPage: number
) => {
  const newFilter = {
    filter: isEmpty(query) ? undefined : query,
    order: order.column !== "" ? order.direction : undefined,
    sort: order.column !== "" ? order.column : undefined,
    page,
    pageSize: rowsPerPage
  };
  setFilter(newFilter);
};

let controller = new AbortController();
export const changeData = (
  filter: any,
  baseUrl: string,
  setData: Function,
  loading: boolean,
  setLoading: Function
) => {
  if (!controller.signal.aborted && loading) controller.abort();
  controller = new AbortController();
  setLoading(true);
  api()
    .post(`${baseUrl}/parametrized`, {
      signal: controller.signal,
      json: filter
    })
    .json()
    .then((res: any) => {
      setLoading(false);
      setData(res);
    })
    .catch((err: any) => {
      if (err.name === "AbortError") {
        // fetch aborted, do nothing
        return;
      } else {
        setLoading(false);
        console.log(err);
      }
    });
};
