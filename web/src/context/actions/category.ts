import { api } from "../../utils/api";

import { CATEGORY_GET, CATEGORY_ACTIVE_SET } from "../reducers/category";
import {
  STATUS_LOADING_COUNT_CHANGE,
  STATUS_ERROR_COUNT_CHANGE
} from "../reducers/status";
import { CategoryProps } from "../../types/category";

export const categoryGet = (dispatch: any) => {
  dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: 1 });
  api()
    .get("category")
    .json()
    .then(res => {
      dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: -1 });
      dispatch({ type: CATEGORY_GET, payload: res });
    })
    .catch(() => {
      dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: -1 });
      dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
    });
};

export const categoryActiveSet = (
  dispatch: any,
  categoryActive: CategoryProps | undefined
) => {
  dispatch({ type: CATEGORY_ACTIVE_SET, payload: categoryActive });
};
