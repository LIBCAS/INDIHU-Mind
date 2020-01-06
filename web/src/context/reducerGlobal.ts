import { reducerRecord } from "./reducers/record";
import { StateProps, ActionProps } from "./Context";

import { reducerSearch } from "./reducers/search";
import { reducerCategory } from "./reducers/category";
import { reducerLabel } from "./reducers/label";
import { reducerStatus } from "./reducers/status";
import { reducerTemplate } from "./reducers/template";
import { reducerUsers } from "./reducers/users";
import { reducerRecordTemplate } from "./reducers/recordTemplate";

export const reducerGlobal = (state: StateProps, action: ActionProps) => {
  return {
    search: reducerSearch(state, action),
    category: reducerCategory(state, action),
    label: reducerLabel(state, action),
    template: reducerTemplate(state, action),
    status: reducerStatus(state, action),
    users: reducerUsers(state, action),
    record: reducerRecord(state, action),
    recordTemplate: reducerRecordTemplate(state, action)
  };
};
