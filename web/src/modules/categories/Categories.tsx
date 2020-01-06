import React, { useEffect, useContext, useState } from "react";
import { RouteComponentProps } from "react-router-dom";
import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import classNames from "classnames";
import Fade from "@material-ui/core/Fade";

import { Modal } from "../../components/portal/Modal";
import { CreateCategory } from "../../components/tabContent/CreateCategory";
import { categoryGet } from "../../context/actions/category";
import { GlobalContext, StateProps } from "../../context/Context";
import { useStyles } from "./_categoriesStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";

import { CategoriesItem } from "./CategoriesItem";

export const Categories: React.FC<RouteComponentProps> = () => {
  const classes = useStyles();
  const classesSpacing = useSpacingStyles();
  const classesLayout = useLayoutStyles();
  const [open, setOpen] = useState(false);
  const context: any = useContext(GlobalContext);
  const state: StateProps = context.state;
  const dispatch: Function = context.dispatch;
  const loadCategories = () => {
    categoryGet(dispatch);
  };
  useEffect(() => {
    loadCategories();
  }, []);
  return (
    <>
      <Fade in>
        <div>
          <div
            className={classNames(
              classesLayout.flex,
              classesLayout.flexWrap,
              classesLayout.alignCenter,
              classesLayout.spaceBetween,
              classesSpacing.mt1,
              classesSpacing.mb2
            )}
          >
            <Typography variant="h5">Přehled kategorií</Typography>
            <Button
              className={classes.createButton}
              variant="contained"
              color="primary"
              onClick={() => setOpen(true)}
            >
              Nová kategorie
            </Button>
          </div>
          {state.category.categories.length === 0 &&
            state.status.loadingCount === 0 && <div>Žádné kategorie</div>}
          {state.category.categories.map(cat => (
            <CategoriesItem
              key={cat.id}
              category={cat}
              loadCategories={loadCategories}
            />
          ))}
        </div>
      </Fade>
      <Modal
        open={open}
        setOpen={setOpen}
        content={
          <CreateCategory
            setOpen={setOpen}
            activeCategory={undefined}
            loadCategories={loadCategories}
          />
        }
      />
    </>
  );
};

export { Categories as default };
