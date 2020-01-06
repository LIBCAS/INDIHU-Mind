export const notEmpty = (value: any) => {
  let error;
  if (value === "" || value === undefined || value === null) {
    error = "Povinné";
  }
  return error;
};
