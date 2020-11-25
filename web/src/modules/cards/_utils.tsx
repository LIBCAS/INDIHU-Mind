import { CategoryProps } from "../../types/category";
import { api } from "../../utils/api";
import { CardProps } from "../../types/card";

export const getPathToCategory = (
  cat: CategoryProps,
  categories: CategoryProps[]
): CategoryProps[] => {
  let result: CategoryProps[] = [];
  categories.forEach((c) => {
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

export const getCards = async (text?: string, page = 0, pageSize = 10) => {
  try {
    const response = await api().post("card/parametrized", {
      json: {
        page,
        pageSize,
        filter:
          text && text.length
            ? [{ field: "name", operation: "CONTAINS", value: text }]
            : [],
        order: "DESC",
      },
    });

    return await response.json();
  } catch (e) {
    console.log(e);
    return null;
  }
};

const cardArrays: string[] = [
  "categories",
  "labels",
  "linkedCards",
  "linkingCards",
  "documents",
  "records",
  "attributes",
];

const parseArrayIds = (name: string, array: any) => ({
  [name]: (array || []).map((item: any) => item.id),
});

export const flattenCardArrays = (c: CardProps) => ({
  ...c,
  ...cardArrays.reduce(
    (object, arrayName: string) => ({
      ...object,
      ...parseArrayIds(arrayName, c[arrayName]),
    }),
    {}
  ),
});

export const concatCardArrays = (c: CardProps, arrays: any) => ({
  ...c,
  ...cardArrays.reduce(
    (object, arrayName: string) => ({
      ...object,
      [arrayName]: (arrays[arrayName] || []).concat(c[arrayName]),
    }),
    {}
  ),
});
