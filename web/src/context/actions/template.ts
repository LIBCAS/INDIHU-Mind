import { api } from "../../utils/api";

import { TEMPLATE_GET } from "../reducers/template";
import {
  STATUS_LOADING_COUNT_CHANGE,
  STATUS_ERROR_COUNT_CHANGE,
} from "../reducers/status";

export const templateGet = (dispatch: any) => {
  dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: 1 });
  api()
    .get("card/template/all")
    .json()
    .then((res: any) => {
      dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: -1 });
      dispatch({ type: TEMPLATE_GET, payload: res });
    })
    .catch(() => {
      dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: -1 });
      dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
    });
};
