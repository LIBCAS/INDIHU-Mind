import IconButton from "@material-ui/core/IconButton";
import Slide from "@material-ui/core/Slide";
import Typography from "@material-ui/core/Typography";
import { ArrowDropDown, ArrowDropUp } from "@material-ui/icons";
import ExpandLess from "@material-ui/icons/ExpandLess";
import ExpandMore from "@material-ui/icons/ExpandMore";
import classNames from "classnames";
import React, { useState } from "react";
import { useStyles as useStylesSpacing } from "../../theme/styles/spacingStyles";
import { CategoryProps } from "../../types/category";
import OrderedItems from "../orderedItems/OrderedItems";
import { useStyles } from "./_tabContentStyles";

interface CategoryItemProps {
  c: CategoryProps;
  categoryActiveSetHandler: (category?: CategoryProps) => void;
  findCategoryById: (id: string) => CategoryProps | undefined;
  categoriesExpanded: CategoryProps[];
  setCategoriesExpanded: React.Dispatch<React.SetStateAction<CategoryProps[]>>;
  categoryActive?: CategoryProps;
  moveForward?: () => void;
  moveBackward?: () => void;
}

export const CategoryItem: React.FC<CategoryItemProps> = ({
  c,
  categoryActiveSetHandler,
  findCategoryById,
  categoriesExpanded,
  setCategoriesExpanded,
  categoryActive,
  moveBackward,
  moveForward,
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
        <div className={classes.item}>
          <div className={classes.itemArrows}>
            {" "}
            <ArrowDropUp
              className={moveBackward ? "" : classes.arrowDisabled}
              onClick={moveBackward}
            />
            <ArrowDropDown
              className={moveForward ? "" : classes.arrowDisabled}
              onClick={moveForward}
            />
          </div>

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
            {hasSubCategories ? (
              isExpanded ? (
                <IconButton
                  size="small"
                  color="inherit"
                  className={classNames(classesSpacing.mlAuto)}
                >
                  <ExpandLess />
                </IconButton>
              ) : (
                <IconButton
                  size="small"
                  color="inherit"
                  className={classNames(classesSpacing.mlAuto)}
                >
                  <ExpandMore />
                </IconButton>
              )
            ) : null}
          </Typography>
        </div>
      </Slide>
      {isExpanded && (
        <div className={classesSpacing.ml1}>
          <OrderedItems
            label="Kategorie"
            endpoint="category"
            initialItems={c.subCategories}
            itemComponent={({ item: cSub, moveForward, moveBackward }) => (
              <CategoryItem
                key={cSub.id}
                c={cSub}
                categoryActiveSetHandler={categoryActiveSetHandler}
                findCategoryById={findCategoryById}
                categoriesExpanded={categoriesExpanded}
                setCategoriesExpanded={setCategoriesExpanded}
                categoryActive={categoryActive}
                moveForward={moveForward}
                moveBackward={moveBackward}
              />
            )}
          />
        </div>
      )}
    </React.Fragment>
  );
};
