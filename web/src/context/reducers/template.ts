import { StateProps, ActionProps } from "../Context";

export const TEMPLATE_GET = "TEMPLATE_GET";

export const reducerTemplate = (state: StateProps, action: ActionProps) => {
  const { template } = state;
  switch (action.type) {
    case TEMPLATE_GET:
      return { templates: action.payload };
    default:
      return { ...template };
  }
};
