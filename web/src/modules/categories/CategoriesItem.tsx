import React, { useState } from "react";
import classNames from "classnames";
import KeyboardArrowUp from "@material-ui/icons/KeyboardArrowUp";
import KeyboardArrowDown from "@material-ui/icons/KeyboardArrowDown";
import Collapse from "@material-ui/core/Collapse";

import { CategoryProps } from "../../types/category";
import { useStyles } from "./_categoriesStyles";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";

import { CategoriesPopover } from "./CategoriesPopover";

interface CategoriesItemProps {
  category: CategoryProps;
  loadCategories: Function;
  isLast?: boolean;
}

export const CategoriesItem: React.FC<CategoriesItemProps> = ({
  category,
  isLast,
  loadCategories
}) => {
  const { name, subCategories } = category;
  const [open, setOpen] = useState(false);
  const classes = useStyles();
  const classesSpacing = useSpacingStyles();
  const classesLayout = useLayoutStyles();
  const classesText = useTextStyles();
  const hasSubCategories =
    category.subCategories && category.subCategories.length > 0;

  const handleOpen = () => {
    if (hasSubCategories) {
      setOpen(prev => !prev);
    }
  };
  return (
    <>
      <div
        className={classNames(classes.categoryItem, {
          [classesText.cursor]: hasSubCategories,
          [classes.categoryItemLast]: isLast && !open
        })}
        onClick={handleOpen}
      >
        <div className={classes.categoryName}>{name}</div>
        <div>
          <div
            className={classNames(
              classesLayout.flex,
              classesLayout.flexWrap,
              classesLayout.justifyCenter
            )}
          >
            <div
              className={classNames(
                classesLayout.flex,
                classesLayout.directionColumn,
                classesLayout.alignCenter,
                classes.counter
              )}
            >
              <div className={classesText.subtitle}>POČET SUBKATEGORIÍ</div>
              <div>
                {category.subCategories && category.subCategories.length}
              </div>
            </div>
            <div
              className={classNames(
                classesLayout.flex,
                classesLayout.directionColumn,
                classesLayout.alignCenter,
                classes.counter
              )}
            >
              <div className={classesText.subtitle}>POČET KARET</div>
              <div>{category.cardsCount}</div>
            </div>
          </div>
        </div>
        <div
          className={classNames(
            classesLayout.flex,
            classesLayout.justifyCenter,
            classes.iconsWrapper
          )}
        >
          <CategoriesPopover
            category={category}
            loadCategories={loadCategories}
          />

          {subCategories && subCategories.length > 0 && (
            <div
              className={classNames(
                classesSpacing.ml1,
                classesText.icon,
                classesText.iconBig
              )}
            >
              {open ? (
                <KeyboardArrowUp fontSize="inherit" />
              ) : (
                <KeyboardArrowDown fontSize="inherit" />
              )}
            </div>
          )}
        </div>
      </div>
      {subCategories && subCategories.length > 0 && (
        <Collapse in={open}>
          <div
            className={classNames(classes.subCategoryWrapper, {
              [classes.subCategoryLine]: subCategories.length > 0,
              [classes.subCategoryNotLast]: !isLast
            })}
          >
            {subCategories.map((cat, i) => (
              <CategoriesItem
                key={cat.id}
                category={cat}
                loadCategories={loadCategories}
                isLast={subCategories.length === i + 1}
              />
            ))}
          </div>
        </Collapse>
      )}
    </>
  );
};
