import { StateProps, ActionProps } from "../Context";

export const RECORD_GET = "RECORD_GET";
export const RECORD_MARC_GET = "RECORD_MARC_GET";

export const reducerRecord = (state: StateProps, action: ActionProps) => {
  const { record } = state;
  switch (action.type) {
    case RECORD_GET:
      return { ...record, records: action.payload };
    case RECORD_MARC_GET:
      return { ...record, marc: action.payload };
    default:
      return { ...record };
  }
};
