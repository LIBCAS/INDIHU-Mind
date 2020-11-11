import { api } from "../../utils/api";

export const changePassword = async (
  oldPassword: string,
  newPassword: string
): Promise<{ ok: boolean; error?: string }> => {
  try {
    const response = await api().put(
      `password/change?new=${encodeURI(newPassword)}&old=${encodeURI(
        oldPassword
      )}`
    );

    const { ok } = response;

    return {
      ok: ok,
    };
  } catch (e) {
    console.log(e);
    return { ok: false, error: e.response.code };
  }
};
