import { union, isEmpty } from "lodash";

import { api } from "../../utils/api";
import { OrderProps } from "./_types";
import { CardProps } from "../../types/card";
import { FormValues } from "./TableGroupEdit";

export const changeFilter = (
  setFilter: Function,
  query: any,
  order: OrderProps,
  page: number,
  rowsPerPage: number
) => {
  const newFilter = {
    filter: isEmpty(query) ? undefined : query,
    order: order.column !== "" ? order.direction : undefined,
    sort: order.column !== "" ? order.column : undefined,
    page,
    pageSize: rowsPerPage
  };
  setFilter(newFilter);
};

let controller = new AbortController();
export const changeData = (
  filter: any,
  baseUrl: string,
  setData: Function,
  loading: boolean,
  setLoading: Function
) => {
  if (!controller.signal.aborted && loading) controller.abort();
  controller = new AbortController();
  setLoading(true);
  api()
    .post(baseUrl, { signal: controller.signal, json: filter })
    .json()
    .then((res: any) => {
      setLoading(false);
      setData(res);
    })
    .catch((err: any) => {
      if (err.name === "AbortError") {
        // fetch aborted, do nothing
        return;
      } else {
        console.log(err);
      }
    });
};

export const getUpdatedCard = (card: CardProps, values: FormValues) => {
  const { name, note, categories, labels, linkedCards } = card;
  const createIds = (o: any) => o.id;
  const categoriesSum = [
    ...categories.map(createIds),
    ...values.categories.map(o => o.value)
  ];
  const labelsSum = [
    ...labels.map(createIds),
    ...values.labels.map(o => o.value)
  ];
  const categoriesPayload = union(categoriesSum);
  const labelsPayload = union(labelsSum);
  return {
    name,
    note,
    categories: categoriesPayload,
    labels: labelsPayload,
    linkedCards: linkedCards && linkedCards.map(createIds)
  };
};
