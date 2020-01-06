import { StateProps, ActionProps } from "../Context";

export const STATUS_LOADING_COUNT_CHANGE = "STATUS_LOADING_COUNT_CHANGE";
export const STATUS_ERROR_COUNT_CHANGE = "STATUS_ERROR_COUNT_CHANGE";
export const STATUS_ERROR_TEXT_SET = "STATUS_ERROR_TEXT_SET";

export const reducerStatus = (state: StateProps, action: ActionProps) => {
  const { status } = state;
  switch (action.type) {
    case STATUS_LOADING_COUNT_CHANGE:
      return { ...status, loadingCount: status.loadingCount + action.payload };
    case STATUS_ERROR_COUNT_CHANGE:
      return { ...status, errorCount: status.errorCount + action.payload };
    case STATUS_ERROR_TEXT_SET:
      return { ...status, errorText: action.payload };
    default:
      return { ...status };
  }
};
