import { StateProps, ActionProps } from "../Context";

export const CATEGORY_GET = "CATEGORY_GET";
export const CATEGORY_ACTIVE_SET = "CATEGORY_ACTIVE_SET";

export const reducerCategory = (state: StateProps, action: ActionProps) => {
  const { category } = state;
  switch (action.type) {
    case CATEGORY_GET:
      return { ...category, categories: action.payload };
    case CATEGORY_ACTIVE_SET:
      return { ...category, categoryActive: action.payload };
    default:
      return { ...category };
  }
};
