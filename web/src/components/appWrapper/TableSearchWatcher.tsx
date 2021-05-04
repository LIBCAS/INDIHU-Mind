import { flatten } from "lodash";
import React, { useCallback, useEffect } from "react";
import { categoryActiveSet } from "../../context/actions/category";
import { labelActiveSet } from "../../context/actions/label";
import {
  searchCategorySet,
  searchLabelSet,
} from "../../context/actions/search";
import { CategoryProps } from "../../types/category";
import { LabelProps } from "../../types/label";

interface TableSearchWatcherProps {
  dispatch: any;
  categories: CategoryProps[];
  categoryActive: CategoryProps | undefined;
  labelActive: LabelProps | undefined;
}

export const TableSearchWatcher: React.FC<TableSearchWatcherProps> = ({
  dispatch,
  categoryActive,
  labelActive,
}) => {
  // is categoryActive inside subCategories tree
  // const isParent = (c: CategoryProps): boolean => {
  //   if (categoryActive && c.id === categoryActive.id) {
  //     return true;
  //   }
  //   if (c.subCategories) {
  //     return c.subCategories.some(cat => isParent(cat));
  //   }
  //   return false;
  // };

  const getSubIds = useCallback((c: CategoryProps): string[] => {
    let result: string[] = [];
    if (c.subCategories) {
      result = flatten(c.subCategories.map((cat) => getSubIds(cat)));
    }
    return [...result, c.id];
  }, []);

  // when active label or category changes then change POST payload to filter table results
  // also set active label or category to undefined if new value is selected
  useEffect(() => {
    if (categoryActive) {
      labelActiveSet(dispatch, undefined);
      const subIds = getSubIds(categoryActive);
      const filterQuery: any = [
        {
          operation: "OR",
          field: "category_ids",
          filter: subIds.map((id) => ({
            field: "category_ids",
            operation: "CONTAINS",
            value: id,
          })),
        },
      ];
      searchCategorySet(
        {
          name: categoryActive.name,
          query: filterQuery,
        },
        dispatch
      );
    }
    if (categoryActive === undefined && labelActive === undefined) {
      searchCategorySet({ name: "", query: {} }, dispatch);
    }
  }, [categoryActive, dispatch, getSubIds, labelActive]);

  useEffect(() => {
    if (labelActive) {
      categoryActiveSet(dispatch, undefined);
      searchLabelSet(
        {
          name: labelActive.name,
          query: [
            {
              field: "label_ids",
              value: labelActive.id,
              operation: "CONTAINS",
            },
          ],
        },
        dispatch
      );
    }
    if (labelActive === undefined && categoryActive === undefined) {
      searchLabelSet({ name: "", query: {} }, dispatch);
    }
  }, [labelActive, categoryActive, dispatch]);
  return null;
};
