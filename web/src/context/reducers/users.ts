import { StateProps, ActionProps } from "../Context";

export const USERS_UPDATED = "USERS_UPDATED";

export const reducerUsers = (state: StateProps, action: ActionProps) => {
  const { users } = state;
  switch (action.type) {
    case USERS_UPDATED:
      return { ...users, updated: action.payload };
    default:
      return { ...users };
  }
};
