import { CardComment } from "../../types/card";
import { api } from "../../utils/api";

const url = "card-comment";

export const createComment = async (cardId: string, text: string) => {
  try {
    const response = await api().post(url, { json: { cardId, text } });
    return await response.json();
  } catch {
    return false;
  }
};

export const updateComment = async (comment: CardComment) => {
  try {
    const response = await api().put(url, { json: comment });
    return await response.json();
  } catch {
    return false;
  }
};

export const deleteComment = async (id: string) => {
  try {
    const response = await api().delete(`${url}/${id}`);
    return response.ok;
  } catch {
    return false;
  }
};
