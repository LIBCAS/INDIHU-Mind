import { api } from "../../utils/api";

const baseUrl = "password";

export const resetPassword = async (email: string) => {
  try {
    const response = await api().put(
      `${baseUrl}/reset?email=${encodeURI(email)}`
    );

    return response.ok;
  } catch (e) {
    console.log(e);
    return false;
  }
};

export const newPassword = async (tokenId: string, newPassword: string) => {
  try {
    const response = await api().put(
      `${baseUrl}/new/${encodeURI(tokenId)}?newPassword=${encodeURI(
        newPassword
      )}`
    );

    return response.ok;
  } catch (e) {
    console.log(e);
    return false;
  }
};
