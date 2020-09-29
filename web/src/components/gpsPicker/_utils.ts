import { round, get, isString, toNumber, isEmpty, isUndefined } from "lodash";

export const roundDD = (dd: number) => round(dd, 4);

export const parseLatitudeLongitude = (value: any) => {
  if (
    isString(value) &&
    !isEmpty(value) &&
    /^\s*[-+]?([1-8]?\d(\.\d+)?|90(\.0+)?)\s*,\s*[-+]?(180(\.0+)?|((1[0-7]\d)|([1-9]?\d))(\.\d+)?)\s*$/.test(
      value
    )
  ) {
    const latitude = toNumber(
      get(value.match(/^(?:\s*)([-+]?\d+(?:\.\d+)?)/), "[1]")
    );
    const longitude = toNumber(
      get(value.match(/([-+]?\d+(?:\.\d+)?)(?:\s*)$/), "[1]")
    );

    return {
      latitude: !isNaN(latitude) ? roundDD(latitude) : undefined,
      longitude: !isNaN(longitude) ? roundDD(longitude) : undefined
    };
  }

  return {};
};

export const validateGps = (gps: any) => {
  const { latitude, longitude } = parseLatitudeLongitude(gps);
  return isEmpty(gps) ||
    (!isUndefined(latitude) &&
      !isUndefined(longitude) &&
      !isNaN(latitude) &&
      !isNaN(longitude))
    ? undefined
    : "Zadejte gps ve správném formátu.";
};
