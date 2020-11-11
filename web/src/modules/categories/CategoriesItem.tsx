import React, { useState, useEffect, useRef } from "react";
import classNames from "classnames";
import KeyboardArrowRight from "@material-ui/icons/KeyboardArrowRight";
import KeyboardArrowDown from "@material-ui/icons/KeyboardArrowDown";
import KeyboardArrowUp from "@material-ui/icons/KeyboardArrowUp";
import Collapse from "@material-ui/core/Collapse";

import { CategoryProps } from "../../types/category";
import { useStyles } from "./_categoriesStyles";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";

import { CategoriesPopover } from "./CategoriesPopover";
import Paper from "@material-ui/core/Paper";
import { theme } from "../../theme/theme";
import useMediaQuery from "@material-ui/core/useMediaQuery";

interface CategoriesItemProps {
  category: CategoryProps;
  loadCategories: Function;
  isSubCategory?: boolean;
  parentCategory?: CategoryProps;
  openedCategory?: string;
  setOpenedCategory?: Function;
}

export const CategoriesItem: React.FC<CategoriesItemProps> = ({
  category,
  loadCategories,
  isSubCategory,
  parentCategory,
  openedCategory,
  setOpenedCategory,
}) => {
  const { name, subCategories } = category;
  const [open, setOpen] = useState(false);
  const classes = useStyles();
  const classesSpacing = useSpacingStyles();
  const classesLayout = useLayoutStyles();
  const classesText = useTextStyles();

  const categoryRef = useRef<HTMLDivElement>(null);

  const hasSubCategories =
    category.subCategories && category.subCategories.length > 0;
  const matchesSm = useMediaQuery(theme.breakpoints.up("sm"));

  // keeps opened only one subcategory tree at a time
  useEffect(() => {
    setOpen(openedCategory === category.id);
  }, [openedCategory, category.id]);

  useEffect(() => {
    //scrolls to the top of a newly opened/closed subcategory tree
    openedCategory === category.id &&
      setTimeout(() => {
        const scrollElement = document.getElementById("categories-section");
        scrollElement &&
          scrollElement.parentElement &&
          categoryRef.current &&
          scrollElement.parentElement.scrollTo({
            behavior: "smooth",
            top: categoryRef.current.offsetTop - (64 + theme.spacing(2)),
          });
      }, 400);
  }, [open, openedCategory, category.id]);

  useEffect(() => {
    if (!hasSubCategories) setOpen(false);
  }, [hasSubCategories]);

  const handleOpen = () => {
    if (hasSubCategories) {
      setOpen((prev) => !prev);
      setOpenedCategory && setOpenedCategory(category.id);
    }
  };
  return (
    <>
      <Paper
        className={classNames(classes.categoryItem, {
          [classesText.cursor]: hasSubCategories,
          [classes.subCategoryItem]: isSubCategory,
          [classes.categoryItemFullWidth]: open || isSubCategory,
          [classes.categoryItemOpened]: open || isSubCategory,
        })}
        onClick={handleOpen}
        ref={categoryRef}
      >
        {" "}
        {(open || isSubCategory) &&
          matchesSm &&
          subCategories &&
          subCategories.length > 0 && (
            <div
              className={classNames(
                classesSpacing.ml1,
                classesText.icon,
                classesText.iconBig,
                {
                  [classesText.iconChangedBg]: open || isSubCategory,
                }
              )}
            >
              {!open ? (
                <KeyboardArrowRight fontSize="inherit" />
              ) : (
                <KeyboardArrowDown fontSize="inherit" />
              )}
            </div>
          )}
        <div
          className={classNames(classes.categoryName, {
            [classes.categoryNameClosed]:
              (!open && !isSubCategory) || !matchesSm,
          })}
        >
          {name}
          {(!open && !isSubCategory) || !matchesSm ? (
            <CategoriesPopover
              iconBgChange={open}
              category={category}
              loadCategories={loadCategories}
            />
          ) : null}
        </div>
        <div>
          <div
            className={classNames(
              classesLayout.flex,
              classesLayout.flexWrap,
              classes.countersContainer
            )}
          >
            <div
              className={classNames(
                classesLayout.flex,
                classesLayout.directionColumn,
                classesLayout.alignCenter,
                classes.counter,
                classes.counterFullWidth
              )}
            >
              <div
                className={classNames(classesText.textGrey, {
                  [classesText.textGreyDark]: open || isSubCategory,
                })}
              >
                POČET SUBKATEGORIÍ
              </div>
              <div
                className={classNames(classesText.text600, classes.countText)}
              >
                {category.subCategories && category.subCategories.length}
              </div>
            </div>
            <div
              className={classNames(
                classesLayout.flex,
                classesLayout.directionColumn,
                classesLayout.alignCenter,
                classes.counter,
                { [classes.counterFullWidth]: true || open || isSubCategory }
              )}
            >
              <div
                className={classNames(classesText.textGrey, {
                  [classesText.textGreyDark]: open || isSubCategory,
                })}
              >
                POČET KARET
              </div>
              <div
                className={classNames(classesText.text600, classes.countText)}
              >
                {category.cardsCount}
              </div>
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
          {(open || isSubCategory) && matchesSm ? (
            <CategoriesPopover
              iconBgChange={open || isSubCategory}
              category={category}
              loadCategories={loadCategories}
              parentCategory={parentCategory}
            />
          ) : null}
          {(!matchesSm || !(open || isSubCategory)) &&
            subCategories &&
            subCategories.length > 0 && (
              <div
                className={classNames(
                  classesSpacing.ml1,
                  classesText.icon,
                  classesText.iconBig,
                  {
                    [classesText.iconChangedBg]: open,
                  }
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
      </Paper>
      <div></div>
      {subCategories && subCategories.length > 0 && (
        <Collapse
          in={open}
          className={classNames(classes.subCategoriesCollapse, {
            [classes.subCategoriesCollapseRoot]: !isSubCategory,
          })}
        >
          <div
            className={classNames(classes.subCategoryWrapper, {
              [classes.subCategoryLine]: subCategories.length > 0,
            })}
          >
            {subCategories.map((cat) => (
              <CategoriesItem
                key={cat.id}
                category={cat}
                loadCategories={loadCategories}
                isSubCategory={true}
                parentCategory={category}
              />
            ))}
          </div>
        </Collapse>
      )}
    </>
  );
};
