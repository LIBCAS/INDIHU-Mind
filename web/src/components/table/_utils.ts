import { api } from "../../utils/api";

export const getData = async (
  params: any,
  url: string,
  requestType?: string
) => {
  try {
    if (requestType === "GET") {
      const response = await api().get(url);
      const parsed = await response.json();
      return { items: parsed, count: parsed.length };
    }
    return (await api()
      .post(url, {
        json: params,
      })
      .json()) as any;
  } catch {
    return null;
  }
};
