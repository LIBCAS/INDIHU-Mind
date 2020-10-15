import React, { useState, useEffect, useContext } from "react";
import Button from "@material-ui/core/Button";

import { categoryGet, categoryActiveSet } from "../../context/actions/category";
import { GlobalContext, StateProps } from "../../context/Context";
import { CategoryProps } from "../../types/category";

import { TabProps } from "../leftPanel/LeftPanelContent";
import { Modal } from "../portal/Modal";

import { CategoryItem } from "./CategoryItem";
import { useStyles } from "./_tabContentStyles";
import { CreateCategory } from "./CreateCategory";

interface CategoriesProps {
  activeTab: TabProps;
  setActiveTab: Function;
  transition: boolean;
}

export const Categories: React.FC<CategoriesProps> = ({
  activeTab,
  setActiveTab,
  transition
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

  const loadCategories = () => {
    categoryGet(dispatch);
  };

  useEffect(() => {
    if (activeTab === undefined) {
      setActiveTab("category");
      categoryActiveSet(dispatch, undefined);
    }
  }, [activeTab]);

  useEffect(() => {
    loadCategories();
  }, []);

  const categoryActiveSetHandler = (category?: CategoryProps) => {
    categoryActiveSet(dispatch, category);
  };

  const findCategoryById = (id: string): CategoryProps | undefined => {
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
  };

  useEffect(() => {
    if (categoryActive && !findCategoryById(categoryActive.id)) {
      categoryActiveSet(dispatch, undefined);
    }
  }, [categories]);

  useEffect(() => {
    if (activeTab !== "category") categoryActiveSet(dispatch, undefined);
  }, [activeTab]);

  return (
    <React.Fragment>
      <Button
        onClick={() => setOpen(true)}
        className={classes.createButton}
        size="small"
        color="inherit"
        fullWidth
      >
        + Vytvořit novou
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
        style={{ maxHeight: transition ? "52vh" : "65vh" }}
      >
        {categories.map(c => (
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
