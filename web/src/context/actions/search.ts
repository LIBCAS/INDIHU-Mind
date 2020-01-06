import { SEARCH_CATEGORY_SET, SEARCH_LABEL_SET } from "../reducers/search";

export const searchCategorySet = (
  payload: { name: string; query: any },
  dispatch: any
) => {
  dispatch({ type: SEARCH_CATEGORY_SET, payload });
};

export const searchLabelSet = (
  payload: { name: string; query: any },
  dispatch: any
) => {
  dispatch({ type: SEARCH_LABEL_SET, payload });
};
