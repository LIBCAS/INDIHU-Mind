import { api } from "../../utils/api";
import { OrderProps } from "./_types";
// import { UserProps } from "../../types/user";

export const changeRequest = (
  setRequest: Function,
  baseUrl: string,
  order: OrderProps,
  page: number,
  rowsPerPage: number
) => {
  // page++;
  let baseQuery = `page=${page}&pageSize=${rowsPerPage}`;
  if (order.column !== "") {
    baseQuery += `&sorting[0].order=${order.direction}&sorting[0].sort=${order.column}`;
  }
  baseQuery = encodeURI(baseQuery);
  const newRequest = baseUrl + baseQuery;
  setRequest(newRequest);
};

let controller = new AbortController();

export const changeData = (
  request: string,
  setData: Function,
  loading: boolean,
  setLoading: Function
) => {
  if (!controller.signal.aborted && loading) controller.abort();
  controller = new AbortController();
  setLoading(true);

  // setLoading(false);
  // setData(sample);
  api()
    .get(request, { signal: controller.signal })
    .json()
    .then((res: any) => {
      setLoading(false);
      setData(res);
    })
    // TODO error
    .catch(err => {
      if (err.name === "AbortError") {
        // fetch aborted, do nothing
        return;
      } else {
        console.log(err);
      }
    });
};
