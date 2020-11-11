import React, { useRef, useState, useContext } from "react";
import MenuList from "@material-ui/core/MenuList";
import MenuItem from "@material-ui/core/MenuItem";
import ListItemIcon from "@material-ui/core/ListItemIcon";
import ListItemText from "@material-ui/core/ListItemText";
import Button from "@material-ui/core/Button";
import MoreVert from "@material-ui/icons/MoreVert";
import Edit from "@material-ui/icons/Edit";
import Delete from "@material-ui/icons/Delete";
import Add from "@material-ui/icons/Add";
import ArrowForward from "@material-ui/icons/ArrowForward";
import OpenWith from "@material-ui/icons/OpenWith";
import classNames from "classnames";

import { GlobalContext } from "../../context/Context";
import { categoryActiveSet } from "../../context/actions/category";
import {
  STATUS_ERROR_COUNT_CHANGE,
  STATUS_ERROR_TEXT_SET,
} from "../../context/reducers/status";
import { api } from "../../utils/api";
import { Loader } from "../../components/loader/Loader";
import { MessageSnackbar } from "../../components/messages/MessageSnackbar";

import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { Popover } from "../../components/portal/Popover";
import { CategoryProps } from "../../types/category";

import { CategoriesRename } from "./CategoriesRename";
import { RouteComponentProps, withRouter } from "react-router-dom";
import { CreateCategory } from "../../components/tabContent/CreateCategory";
import { CategoriesMove } from "./CategoriesMove";
interface CategoriesPopoverProps {
  category: CategoryProps;
  loadCategories: Function;
  iconBgChange?: boolean;
  parentCategory?: CategoryProps;
}

const CategoriesPopoverView: React.FC<
  CategoriesPopoverProps & RouteComponentProps
> = ({ category, loadCategories, history, iconBgChange, parentCategory }) => {
  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;
  const [open, setOpen] = useState(false);
  const [content, setContent] = useState<
    "menu" | "rename" | "delete" | "addSubcategory" | "move"
  >("menu");
  const [errorShow, setErrorShow] = useState(false);
  const [loading, setLoading] = useState(false);
  const menuRef = useRef(null);
  const classesSpacing = useSpacingStyles();
  const classesLayout = useLayoutStyles();
  const classesText = useTextStyles();

  const handleOpen = (open: boolean) => {
    setContent("menu");
    setOpen(open);
  };

  const showCardsOfCategory = (category: CategoryProps) => {
    categoryActiveSet(dispatch, category);
    history.push("/cards");
  };

  const handleDelete = () => {
    if (loading) return false;
    setLoading(true);
    api()
      .delete(`category/${category.id}`)
      .then(() => {
        dispatch({
          type: STATUS_ERROR_TEXT_SET,
          payload: `Kategorie ${category.name} byla úspěšně odstraněna`,
        });
        dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
        setLoading(false);
        loadCategories();
        setContent("menu");
        handleOpen(false);
      })
      .catch(() => {
        setLoading(false);
        setErrorShow(true);
      });
  };
  return (
    <div onClick={(e: React.MouseEvent) => e.stopPropagation()}>
      <Loader loading={loading} />
      {errorShow && <MessageSnackbar setVisible={setErrorShow} />}
      <div
        ref={menuRef}
        onClick={(e: any) => {
          e.stopPropagation();
          handleOpen(!open);
        }}
        className={classNames(classesText.icon, classesText.iconBig, {
          [classesText.iconChangedBg]: iconBgChange,
        })}
      >
        <MoreVert fontSize="inherit" />
      </div>
      <Popover
        anchorEl={menuRef.current}
        open={open}
        setOpen={handleOpen}
        width={300}
        overflowVisible={true}
        content={
          <div onClick={(e: any) => e.stopPropagation()}>
            {content === "menu" && (
              <MenuList>
                <MenuItem onClick={() => showCardsOfCategory(category)}>
                  <ListItemIcon>
                    <ArrowForward />
                  </ListItemIcon>
                  <ListItemText primary="Zobrazit karty kategorie" />
                </MenuItem>
                <MenuItem
                  onClick={() => {
                    setContent("addSubcategory");
                  }}
                >
                  <ListItemIcon>
                    <Add />
                  </ListItemIcon>
                  <ListItemText primary="Přidat subkategorii" />
                </MenuItem>
                <MenuItem onClick={() => setContent("rename")}>
                  <ListItemIcon>
                    <Edit />
                  </ListItemIcon>
                  <ListItemText primary="Přejmenovat" />
                </MenuItem>
                <MenuItem onClick={() => setContent("move")}>
                  <ListItemIcon>
                    <OpenWith />
                  </ListItemIcon>
                  <ListItemText primary="Přesunout" />
                </MenuItem>
                <MenuItem onClick={() => setContent("delete")}>
                  <ListItemIcon>
                    <Delete />
                  </ListItemIcon>
                  <ListItemText primary="Odstranit" />
                </MenuItem>
              </MenuList>
            )}
            {content === "addSubcategory" && (
              <CreateCategory
                setOpen={handleOpen}
                activeCategory={category}
                loadCategories={loadCategories}
              />
            )}
            {content === "rename" && (
              <CategoriesRename
                category={category}
                setContent={setContent}
                loadCategories={loadCategories}
                setOpen={handleOpen}
                dispatch={dispatch}
              />
            )}
            {content === "move" && (
              <CategoriesMove
                category={category}
                parentCategory={parentCategory}
                setOpen={handleOpen}
                loadCategories={loadCategories}
              />
            )}
            {content === "delete" && (
              <div
                className={classNames(
                  classesSpacing.mt1,
                  classesSpacing.ml2,
                  classesSpacing.mr2,
                  classesSpacing.mb1
                )}
              >
                <div
                  className={classNames(
                    classesText.textBold,
                    classesText.textCenter,
                    classesSpacing.p2,
                    classesSpacing.pb1
                  )}
                >
                  Opravdu chcete smazat kategorii {category.name}
                  {category.subCategories && category.subCategories.length > 0
                    ? `, která má ${category.subCategories.length} podkategorie?`
                    : "?"}{" "}
                </div>
                <div
                  className={classNames(
                    classesSpacing.mt1,
                    classesLayout.flex,
                    classesLayout.spaceBetween,
                    classesLayout.flexGrow,
                    classesLayout.directionRowReverse
                  )}
                >
                  <Button
                    variant="text"
                    color="secondary"
                    onClick={handleDelete}
                  >
                    Smazat
                  </Button>
                  <Button
                    className={classNames(classesText.textGrey)}
                    variant="text"
                    onClick={() => setContent("menu")}
                  >
                    Zrušit
                  </Button>
                </div>
              </div>
            )}
          </div>
        }
      />
    </div>
  );
};
export const CategoriesPopover = withRouter(CategoriesPopoverView);
