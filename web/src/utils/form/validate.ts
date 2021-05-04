export const notEmpty = (value: any) => {
  let error;
  if (value === "" || value === undefined || value === null) {
    error = "Povinné";
  }
  return error;
};

export const notLongerThan255 = (value: string | undefined) =>
  value && value.length > 255 ? "Smí mít maximálně 255 znaků" : undefined;
