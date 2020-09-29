import { api } from "../../utils/api";
// import { UserProps } from "../../types/user";

interface RequestProps {
  page: number;
  pageSize: number;
  sorting?: any[];
}

let controller = new AbortController();

export const changeData = (
  url: string,
  request: RequestProps,
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
    .post(url, { signal: controller.signal, json: request })
    .json()
    .then((res: any) => {
      setLoading(false);
      setData(res);
    })
    // TODO error
    .catch((err: any) => {
      if (err.name === "AbortError") {
        // fetch aborted, do nothing
        return;
      } else {
        console.log(err);
      }
    });
};
