import { isEmpty, isString } from "lodash";

export const isValidDate = (dateString?: string) => {
  if (!isEmpty(dateString) && isString(dateString)) {
    const date = new Date(dateString);

    if (date && date.getFullYear() < 2999) {
      return true;
    }
  }

  return false;
};

export const formatYear = (date?: string) => {
  if (!date || !isValidDate(date)) {
    return "";
  }

  const pattern = /(\d{4}).*/;
  return date.replace(pattern, "$1");
};

export const formatDate = (date?: string) => {
  if (!date || !isValidDate(date)) {
    return "";
  }

  const pattern = /(\d{4})-(\d{2})-(\d{2}).*/;
  return date.replace(pattern, "$3. $2. $1");
};

export const formatDateTime = (dateTime?: string) => {
  if (!dateTime || !isValidDate(dateTime)) {
    return "";
  }

  const pattern = /(\d{4})-(\d{2})-(\d{2})T(\d{1,2}):(\d{1,2}).*/;
  return dateTime.replace(pattern, "$3. $2. $1 $4:$5");
};
