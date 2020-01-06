import React from "react";
import Typography from "@material-ui/core/Typography";
import classNames from "classnames";
import Slide from "@material-ui/core/Slide";
import IconButton from "@material-ui/core/IconButton";
import ExpandMore from "@material-ui/icons/ExpandMore";
import ExpandLess from "@material-ui/icons/ExpandLess";

import { CategoryProps } from "../../types/category";

import { useStyles } from "./_tabContentStyles";
import { useStyles as useStylesSpacing } from "../../theme/styles/spacingStyles";

interface CategoryItemProps {
  c: CategoryProps;
  categoryActiveSetHandler: (category?: CategoryProps) => void;
  findCategoryById: (id: string) => CategoryProps | undefined;
  categoriesExpanded: CategoryProps[];
  setCategoriesExpanded: React.Dispatch<React.SetStateAction<CategoryProps[]>>;
  categoryActive?: CategoryProps;
}

export const CategoryItem: React.FC<CategoryItemProps> = ({
  c,
  categoryActiveSetHandler,
  findCategoryById,
  categoriesExpanded,
  setCategoriesExpanded,
  categoryActive
}) => {
  const classes = useStyles();
  const classesSpacing = useStylesSpacing();

  // is categoryActive inside subCategories tree
  const isParent = (c: CategoryProps): boolean => {
    if (categoryActive && c.id === categoryActive.id) {
      return true;
    }
    if (c.subCategories) {
      return c.subCategories.some(cat => isParent(cat));
    }
    return false;
  };

  const setCategoryActiveToParent = () => {
    if (categoryActive && categoryActive.parentId) {
      const parent = findCategoryById(categoryActive.parentId);
      categoryActiveSetHandler(parent);
    } else {
      categoryActiveSetHandler(undefined);
    }
  };
  const hasSubCategories = c.subCategories && c.subCategories.length > 0;
  const isActiveCategory =
    categoryActive && (categoryActive.id === c.id || isParent(c));

  const isExpanded = hasSubCategories && isActiveCategory;

  return (
    <React.Fragment key={c.name}>
      <Slide in direction="right">
        <Typography
          onClick={() => {
            if (categoryActive && categoryActive.id === c.id) {
              setCategoryActiveToParent();
            } else {
              categoryActiveSetHandler(c);
            }
          }}
          className={classNames({
            [classes.category]: true,
            [classes.categoryActive]:
              categoryActive && categoryActive.id === c.id,
            [classes.categorySub]: c.parentId
          })}
          variant="body1"
          color="inherit"
        >
          {c.name}
          {isExpanded ? (
            <IconButton
              size="small"
              color="inherit"
              // onClick={e => {
              //   e.stopPropagation();
              //   setCategoriesExpanded(prev => prev.filter(p => p.id !== c.id));
              // }}
              className={classNames(classesSpacing.mlAuto)}
            >
              <ExpandLess />
            </IconButton>
          ) : hasSubCategories ? (
            <IconButton
              size="small"
              color="inherit"
              // onClick={e => {
              //   e.stopPropagation();
              //   setCategoriesExpanded(prev => [...prev, c]);
              // }}
              className={classNames(classesSpacing.mlAuto)}
            >
              <ExpandMore />
            </IconButton>
          ) : null}
        </Typography>
      </Slide>
      {isExpanded && (
        <div className={classesSpacing.ml1}>
          {c.subCategories &&
            c.subCategories.map(cSub => (
              <CategoryItem
                key={cSub.id}
                c={cSub}
                categoryActiveSetHandler={categoryActiveSetHandler}
                findCategoryById={findCategoryById}
                categoriesExpanded={categoriesExpanded}
                setCategoriesExpanded={setCategoriesExpanded}
                categoryActive={categoryActive}
              />
            ))}
        </div>
      )}
    </React.Fragment>
  );
};
