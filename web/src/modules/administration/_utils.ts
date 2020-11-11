import { api } from "../../utils/api";

export const reindex = async () => {
  try {
    await api().post("administration/reindex");
  } catch (e) {
    console.log(e);
  }
};
