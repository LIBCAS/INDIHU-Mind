import { round, get, isString, toNumber, isEmpty } from "lodash";

export const parseLatitudeLongitude = (value: any) => {
  if (
    !isEmpty(value) &&
    isString(value) &&
    /^\s*[-+]?([1-8]?\d(\.\d+)?|90(\.0+)?)\s*,\s*[-+]?(180(\.0+)?|((1[0-7]\d)|([1-9]?\d))(\.\d+)?)\s*$/.test(
      value
    )
  ) {
    const parse = (regexp: RegExp) => {
      const parsed = toNumber(get(value.match(regexp), "[1]"));
      return !isNaN(parsed) ? round(parsed, 4) : undefined;
    };

    return {
      latitude: parse(/^(?:\s*)([-+]?\d+(?:\.\d+)?)/),
      longitude: parse(/([-+]?\d+(?:\.\d+)?)(?:\s*)$/),
    };
  }

  return {};
};
