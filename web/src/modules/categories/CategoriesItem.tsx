import Collapse from "@material-ui/core/Collapse";
import IconButton from "@material-ui/core/IconButton";
import Paper from "@material-ui/core/Paper";
import Tooltip from "@material-ui/core/Tooltip";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import { ArrowDownward, ArrowUpward } from "@material-ui/icons";
import KeyboardArrowDown from "@material-ui/icons/KeyboardArrowDown";
import KeyboardArrowRight from "@material-ui/icons/KeyboardArrowRight";
import KeyboardArrowUp from "@material-ui/icons/KeyboardArrowUp";
import classNames from "classnames";
import React, { useEffect, useRef, useState } from "react";
import OrderedItems from "../../components/orderedItems/OrderedItems";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { theme } from "../../theme/theme";
import { CategoryProps } from "../../types/category";
import { CategoriesPopover } from "./CategoriesPopover";
import { useStyles } from "./_categoriesStyles";

interface CategoriesItemProps {
  category: CategoryProps;
  loadCategories: Function;
  displayFullWidth: boolean;
  isSubCategory?: boolean;
  parentCategory?: CategoryProps;
  openedCategory?: string;
  setOpenedCategory?: Function;
  moveForward?: () => void;
  moveBackward?: () => void;
}

export const CategoriesItem: React.FC<CategoriesItemProps> = ({
  category,
  loadCategories,
  isSubCategory,
  parentCategory,
  openedCategory,
  setOpenedCategory,
  moveForward,
  moveBackward,
  displayFullWidth,
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

  const isFullWidth = displayFullWidth || open || isSubCategory;

  const handleEventWithoutPropagation = (
    callback: (() => void) | undefined
  ) => (event: React.MouseEvent<HTMLButtonElement>) => {
    event.stopPropagation();
    if (callback) {
      callback();
    }
  };
  return (
    <>
      <Paper
        className={classNames(classes.categoryItem, {
          [classesText.cursor]: hasSubCategories,
          [classes.subCategoryItem]: isSubCategory,
          [classes.categoryItemFullWidth]: isFullWidth,
          [classes.categoryItemOpened]: open,
        })}
        onClick={handleOpen}
        ref={categoryRef}
      >
        {isFullWidth && (
          <div className={classes.arrowsWrapper}>
            <Tooltip title="Posunout nahoru">
              <IconButton
                disabled={!moveBackward}
                onClick={handleEventWithoutPropagation(moveBackward)}
              >
                <ArrowUpward />
              </IconButton>
            </Tooltip>
            <Tooltip title="Posunout dolů">
              <IconButton
                disabled={!moveForward}
                onClick={handleEventWithoutPropagation(moveForward)}
              >
                <ArrowDownward />
              </IconButton>
            </Tooltip>
          </div>
        )}{" "}
        {isFullWidth && matchesSm && subCategories && subCategories.length > 0 && (
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
            {!open ? (
              <KeyboardArrowRight fontSize="inherit" />
            ) : (
              <KeyboardArrowDown fontSize="inherit" />
            )}
          </div>
        )}
        <div
          className={classNames(classes.categoryName, {
            [classes.categoryNameClosed]: !isFullWidth || !matchesSm,
          })}
        >
          {name}
          {!isFullWidth || !matchesSm ? (
            <CategoriesPopover
              iconBgChange={open}
              category={category}
              loadCategories={loadCategories}
              moveForward={moveForward}
              moveBackward={moveBackward}
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
                  [classesText.textGreyDark]: isFullWidth,
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
                classes.counterFullWidth
              )}
            >
              <div
                className={classNames(classesText.textGrey, {
                  [classesText.textGreyDark]: isFullWidth,
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
          {isFullWidth && matchesSm ? (
            <CategoriesPopover
              iconBgChange={open}
              category={category}
              loadCategories={loadCategories}
              parentCategory={parentCategory}
              moveForward={moveForward}
              moveBackward={moveBackward}
            />
          ) : null}
          {(!matchesSm || !isFullWidth) &&
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
            <OrderedItems
              initialItems={subCategories}
              label="Kategorie"
              endpoint="category"
              itemComponent={({ item: cat, moveBackward, moveForward }) => (
                <CategoriesItem
                  key={cat.id}
                  category={cat}
                  loadCategories={loadCategories}
                  isSubCategory={true}
                  parentCategory={category}
                  moveForward={moveForward}
                  moveBackward={moveBackward}
                  displayFullWidth={displayFullWidth}
                />
              )}
            />
          </div>
        </Collapse>
      )}
    </>
  );
};
