import { StateProps, ActionProps } from "../Context";

export const LABEL_GET = "LABEL_GET";
export const LABEL_ACTIVE_SET = "LABEL_ACTIVE_SET";

export const reducerLabel = (state: StateProps, action: ActionProps) => {
  const { label } = state;
  switch (action.type) {
    case LABEL_GET:
      return { ...label, labels: action.payload };
    case LABEL_ACTIVE_SET:
      return { ...label, labelActive: action.payload };
    default:
      return { ...label };
  }
};
