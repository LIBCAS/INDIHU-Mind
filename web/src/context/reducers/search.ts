import { StateProps, ActionProps } from "../Context";

export const SEARCH_CATEGORY_SET = "SEARCH_CATEGORY_SET";
export const SEARCH_LABEL_SET = "SEARCH_LABEL_SET";

export const reducerSearch = (state: StateProps, action: ActionProps) => {
  const { search } = state;
  if (action.payload === undefined) return { ...search };
  const { name, query } = action.payload;
  switch (action.type) {
    case SEARCH_CATEGORY_SET:
      return {
        ...search,
        labelName: "",
        label: "",
        categoryName: name,
        category: query
      };
    case SEARCH_LABEL_SET:
      return {
        ...search,
        categoryName: "",
        category: "",
        labelName: name,
        label: query
      };
    default:
      return { ...search };
  }
};
