import { api } from "../../utils/api";

import { RECORD_TEMPLATE_GET } from "../reducers/recordTemplate";
import {
  STATUS_LOADING_COUNT_CHANGE,
  STATUS_ERROR_COUNT_CHANGE
} from "../reducers/status";

export const recordTemplateGet = (dispatch: any) => {
  dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: 1 });
  api()
    .post("template/parametrized", {
      json: {
        page: 0,
        pageSize: 0,
        filter: []
      }
    })
    .json<any[]>()
    .then((res: any) => {
      const resFiltered = res.items.filter((r: any) => !r.deleted);
      dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: -1 });
      dispatch({ type: RECORD_TEMPLATE_GET, payload: resFiltered });
    })
    .catch(() => {
      dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: -1 });
      dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
    });
};
