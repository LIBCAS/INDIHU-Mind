import React from "react";

import { CategoryProps } from "../types/category";

export const formatCategoryName = (category: CategoryProps) => {
  let parentString = "";
  let parent = category.parent;

  while (parent) {
    parentString += `${parent.name} > `;
    parent = parent.parent;
  }

  return (
    <span style={{ fontWeight: "normal" }}>
      {parentString}
      <span style={{ fontWeight: "bold" }}>{category.name}</span>
    </span>
  );
};
