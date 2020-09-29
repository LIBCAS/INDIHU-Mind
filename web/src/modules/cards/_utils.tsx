import React from "react";
import moment from "moment";

import { ColumnProps } from "../../components/tableCard/_types";
import { CategoryProps } from "../../types/category";
import { api } from "../../utils/api";

export const columns: ColumnProps[] = [
  {
    id: "1",
    path: "name",
    name: "Název",
    format: (row: any) => {
      return <span style={{ fontWeight: 800 }}>{row.name}</span>;
    }
  },
  {
    id: "2",
    path: "updated",
    name: "Poslední úprava",
    format: (row: any) => {
      return moment(row.updated).format("DD. MM. YYYY");
    }
  },
  {
    id: "3",
    path: "note",
    name: "Popis",
    unsortable: true
  },
  {
    id: "4",
    path: "labels",
    name: "Štítky",
    unsortable: true,
    printable: false
  }
];

export const getPathToCategory = (
  cat: CategoryProps,
  categories: CategoryProps[]
): CategoryProps[] => {
  let result: CategoryProps[] = [];
  categories.forEach(c => {
    if (c.id === cat.id) {
      result = [c];
    }
    if (c.subCategories && c.subCategories.length > 0) {
      const nestedCategory = getPathToCategory(cat, c.subCategories);
      if (nestedCategory.length > 0) {
        result = [c, ...nestedCategory];
      }
    }
  });
  return result;
};

export const getCards = async (text?: string, page = 0, pageSize = null) => {
  try {
    const response = await api().post("card/parametrized", {
      json: {
        page,
        pageSize,
        filter:
          text && text.length
            ? [{ field: "name", operation: "CONTAINS", value: text }]
            : []
      }
    });

    return await response.json();
  } catch (e) {
    console.log(e);
    return null;
  }
};
