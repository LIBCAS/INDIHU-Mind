import { StateProps, ActionProps } from "../Context";

export const RECORD_TEMPLATE_GET = "RECORD_TEMPLATE_GET";

export const reducerRecordTemplate = (
  state: StateProps,
  action: ActionProps
) => {
  const { recordTemplate } = state;
  switch (action.type) {
    case RECORD_TEMPLATE_GET:
      return { recordsTemplates: action.payload };
    default:
      return { ...recordTemplate };
  }
};
