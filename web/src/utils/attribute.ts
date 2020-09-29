import { get, find } from "lodash";

import { AttributeType, AttributeTypeEnum } from "../enums";
import { notEmpty } from "./form/validate";

export const getAttributeTypeDefaultValue = (type: string) => {
  const date = new Date();
  switch (type) {
    case AttributeType.DOUBLE:
      return 0;
    case AttributeType.BOOLEAN:
      return false;
    case AttributeType.DATE:
      date.setHours(0, 0, 0);
      return date;
    case AttributeType.DATETIME:
      return date;
    default:
      return "";
  }
};

export const getAttributeTypeLabel = (type: string) =>
  get(
    find(AttributeTypeEnum, ({ value }) => type === value),
    "label",
    ""
  );

export const validateAttributeType = (type: AttributeType) => (value: any) => {
  let error;
  const notEmptyTypes = [
    AttributeType.STRING,
    AttributeType.DOUBLE,
    AttributeType.URL,
    AttributeType.GEOLOCATION
  ];
  if (notEmptyTypes.indexOf(type) !== -1) {
    error = notEmpty(value);
  }
  return error;
};
