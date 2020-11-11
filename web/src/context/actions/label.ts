import { api } from "../../utils/api";
import { LabelProps } from "../../types/label";

import { LABEL_GET, LABEL_ACTIVE_SET } from "../reducers/label";
import {
  STATUS_LOADING_COUNT_CHANGE,
  STATUS_ERROR_COUNT_CHANGE,
} from "../reducers/status";

export const labelGet = (dispatch: any) => {
  dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: 1 });
  api()
    .get("label")
    .json()
    .then((res: any) => {
      dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: -1 });
      dispatch({ type: LABEL_GET, payload: res });
    })
    .catch(() => {
      dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: -1 });
      dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
    });
};

export const labelActiveSet = (
  dispatch: any,
  labelActive: LabelProps | undefined
) => {
  dispatch({ type: LABEL_ACTIVE_SET, payload: labelActive });
};
