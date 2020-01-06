import { USERS_UPDATED } from "../reducers/users";

// if users are updated, refresh user table
export const usersUpdated = (dispatch: any, updated: boolean) => {
  dispatch({ type: USERS_UPDATED, payload: updated });
};
