import { api } from "../../utils/api";

import { RECORD_MARC_GET } from "./../reducers/record";
import {
  STATUS_LOADING_COUNT_CHANGE,
  STATUS_ERROR_COUNT_CHANGE,
} from "../reducers/status";

export const recordGet = async (text?: string, page = 0, pageSize = 10) => {
  try {
    const response = await api().post("record/parametrized", {
      json: {
        page,
        pageSize,
        filter: text ? [{ field: "name", operation: "EQ", value: text }] : [],
      },
    });
    const records = await response.json();
    // const resFiltered = records.items.filter((r: any) => !r.deleted);
    return records.items;
  } catch {
    return [];
  }
};

export const recordGetMarc = (dispatch: any) => {
  dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: 1 });
  api({ noContentType: true })
    .get("record/marc-fields", {
      headers: new Headers({ accept: "text/plain" }),
    })
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
