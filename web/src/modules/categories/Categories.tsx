import Button from "@material-ui/core/Button";
import Fade from "@material-ui/core/Fade";
import IconButton from "@material-ui/core/IconButton";
import Tooltip from "@material-ui/core/Tooltip";
import Typography from "@material-ui/core/Typography";
import ViewHeadline from "@material-ui/icons/ViewHeadline";
import ViewModule from "@material-ui/icons/ViewModule";
import classNames from "classnames";
import React, { useCallback, useContext, useEffect, useState } from "react";
import { RouteComponentProps } from "react-router-dom";
import OrderedItems from "../../components/orderedItems/OrderedItems";
import { Modal } from "../../components/portal/Modal";
import { CreateCategory } from "../../components/tabContent/CreateCategory";
import { categoryGet } from "../../context/actions/category";
import { GlobalContext, StateProps } from "../../context/Context";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { theme } from "../../theme/theme";
import { CategoriesItem } from "./CategoriesItem";
import { useStyles } from "./_categoriesStyles";

export const Categories: React.FC<RouteComponentProps> = () => {
  const classes = useStyles();
  const classesSpacing = useSpacingStyles();
  const classesLayout = useLayoutStyles();
  const [open, setOpen] = useState(false);
  const [openedCategory, setOpenedCategory] = useState(undefined);
  const context: any = useContext(GlobalContext);
  const state: StateProps = context.state;
  const dispatch: Function = context.dispatch;
  const loadCategories = useCallback(() => {
    categoryGet(dispatch);
  }, [dispatch]);

  const [viewCards, setViewCards] = useState<boolean>(true);

  const Icon = viewCards ? ViewHeadline : ViewModule;

  useEffect(() => {
    loadCategories();
  }, [loadCategories]);

  return (
    <div id="categories-section">
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

            <Tooltip
              className={classes.toggleViewButton}
              title={viewCards ? "Řádky" : "Dlaždice"}
            >
              <IconButton
                style={{ color: theme.blackIconColor }}
                onClick={() => setViewCards((prev) => !prev)}
              >
                <Icon fontSize="large" color="inherit" />
              </IconButton>
            </Tooltip>
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
          <div className={classes.categoriesContainer}>
            <OrderedItems
              initialItems={state.category.categories}
              label="Kategorie"
              endpoint="category"
              itemComponent={({ item: cat, moveForward, moveBackward }) => (
                <CategoriesItem
                  openedCategory={openedCategory}
                  setOpenedCategory={setOpenedCategory}
                  key={cat.id}
                  category={cat}
                  loadCategories={loadCategories}
                  moveForward={moveForward}
                  moveBackward={moveBackward}
                  displayFullWidth={!viewCards}
                />
              )}
            />
          </div>
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
    </div>
  );
};

export { Categories as default };
