import { api } from "../../utils/api";

import { RECORD_GET, RECORD_MARC_GET } from "./../reducers/record";
import {
  STATUS_LOADING_COUNT_CHANGE,
  STATUS_ERROR_COUNT_CHANGE
} from "../reducers/status";

export const recordGet = async (dispatch: any) => {
  dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: 1 });
  try {
    const response = await api().post("record/parametrized", {
      json: {
        page: 0,
        pageSize: 0,
        filter: []
      }
    });
    const records = await response.json();
    const resFiltered = records.items.filter((r: any) => !r.deleted);
    dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: -1 });
    dispatch({ type: RECORD_GET, payload: resFiltered });
    return Promise.resolve(resFiltered);
  } catch {
    dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: -1 });
    dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
  }
};

export const recordGetMarc = (dispatch: any) => {
  dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: 1 });
  api()
    .get("record/marc-fields")
    .json()
    .then((res: any) => {
      dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: -1 });
      dispatch({ type: RECORD_MARC_GET, payload: res });
    })
    .catch(() => {
      dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: -1 });
      dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
    });
};
