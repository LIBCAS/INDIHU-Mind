import React, { useState, useEffect, useContext, useCallback } from "react";
import Button from "@material-ui/core/Button";

import { categoryGet, categoryActiveSet } from "../../context/actions/category";
import { GlobalContext, StateProps } from "../../context/Context";
import { CategoryProps } from "../../types/category";

import { TabProps } from "../leftPanel/LeftPanelContent";
import { Modal } from "../portal/Modal";

import { CategoryItem } from "./CategoryItem";
import { useStyles } from "./_tabContentStyles";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { CreateCategory } from "./CreateCategory";

interface CategoriesProps {
  activeTab: TabProps;
  setActiveTab: Function;
  transition: boolean;
}

export const Categories: React.FC<CategoriesProps> = ({
  activeTab,
  setActiveTab,
  transition,
}) => {
  const context: any = useContext(GlobalContext);

  const state: StateProps = context.state;

  const dispatch: Function = context.dispatch;

  const { categories, categoryActive } = state.category;

  const [categoriesExpanded, setCategoriesExpanded] = useState<CategoryProps[]>(
    []
  );

  const [open, setOpen] = useState<boolean>(false);

  const classes = useStyles();
  const classesText = useTextStyles();

  const loadCategories = useCallback(() => {
    categoryGet(dispatch);
  }, [dispatch]);

  useEffect(() => {
    if (activeTab === undefined) {
      setActiveTab("category");
      categoryActiveSet(dispatch, undefined);
    }
  }, [setActiveTab, activeTab, dispatch]);

  useEffect(() => {
    loadCategories();
  }, [loadCategories]);

  const categoryActiveSetHandler = (category?: CategoryProps) => {
    categoryActiveSet(dispatch, category);
  };

  const findCategoryById = useCallback(
    (id: string): CategoryProps | undefined => {
      let result: CategoryProps | undefined;
      const loop = (cat: CategoryProps) => {
        if (cat.id === id) {
          result = cat;
          return true;
        }
        if (cat.subCategories) {
          cat.subCategories.some(loop);
        }
        return false;
      };
      categories.some(loop);
      return result;
    },
    [categories]
  );

  useEffect(() => {
    if (categoryActive && !findCategoryById(categoryActive.id)) {
      categoryActiveSet(dispatch, undefined);
    }
  }, [categoryActive, dispatch, findCategoryById]);

  useEffect(() => {
    if (activeTab !== "category") categoryActiveSet(dispatch, undefined);
  }, [activeTab, dispatch]);

  return (
    <React.Fragment>
      <Button
        onClick={() => setOpen(true)}
        className={classes.createButton}
        size="small"
        color="inherit"
        fullWidth
      >
        {" "}
        <div>
          + Vytvořit novou{" "}
          {categoryActive && (
            <>
              subkategorii pro{" "}
              <span className={classesText.textBold}>
                {categoryActive.name}
              </span>
            </>
          )}
        </div>
      </Button>
      <Modal
        open={open}
        setOpen={setOpen}
        content={
          <CreateCategory
            setOpen={setOpen}
            activeCategory={categoryActive}
            loadCategories={loadCategories}
          />
        }
      />
      <div
        className={classes.categoriesContainer}
        style={{
          maxHeight: transition
            ? window.innerWidth < 512
              ? "75%"
              : "90%"
            : window.innerWidth < 512
            ? "85%"
            : `calc(100% - 32px)`,
        }}
      >
        {categories.map((c) => (
          <CategoryItem
            key={c.id}
            c={c}
            categoryActiveSetHandler={categoryActiveSetHandler}
            findCategoryById={findCategoryById}
            categoryActive={categoryActive}
            categoriesExpanded={categoriesExpanded}
            setCategoriesExpanded={setCategoriesExpanded}
          />
        ))}
      </div>
    </React.Fragment>
  );
};
