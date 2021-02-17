import IconButton from "@material-ui/core/IconButton";
import Slide from "@material-ui/core/Slide";
import Typography from "@material-ui/core/Typography";
import ExpandLess from "@material-ui/icons/ExpandLess";
import ExpandMore from "@material-ui/icons/ExpandMore";
import classNames from "classnames";
import React, { useState } from "react";
import { useStyles as useStylesSpacing } from "../../theme/styles/spacingStyles";
import { CategoryProps } from "../../types/category";
import { useStyles } from "./_tabContentStyles";

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
  categoryActive,
}) => {
  const classes = useStyles();
  const classesSpacing = useStylesSpacing();

  const setCategoryActiveToParent = () => {
    if (categoryActive && categoryActive.parentId) {
      const parent = findCategoryById(categoryActive.parentId);
      categoryActiveSetHandler(parent);
    } else {
      categoryActiveSetHandler(undefined);
    }
  };
  const hasSubCategories = c.subCategories && c.subCategories.length > 0;

  const [isExpanded, setIsExpanded] = useState(false);

  return (
    <React.Fragment key={c.name}>
      <Slide in direction="right">
        <Typography
          onClick={() => {
            setIsExpanded((prev) => !prev);
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
            [classes.categorySub]: c.parentId,
          })}
          variant="body1"
          color="inherit"
        >
          {c.name}
          {isExpanded ? (
            <IconButton
              size="small"
              color="inherit"
              className={classNames(classesSpacing.mlAuto)}
            >
              <ExpandLess />
            </IconButton>
          ) : hasSubCategories ? (
            <IconButton
              size="small"
              color="inherit"
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
            c.subCategories.map((cSub) => (
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
