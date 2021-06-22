import Button from "@material-ui/core/Button";
import ListItemIcon from "@material-ui/core/ListItemIcon";
import ListItemText from "@material-ui/core/ListItemText";
import MenuItem from "@material-ui/core/MenuItem";
import MenuList from "@material-ui/core/MenuList";
import { ArrowLeft, ArrowRight } from "@material-ui/icons";
import Add from "@material-ui/icons/Add";
import ArrowForward from "@material-ui/icons/ArrowForward";
import Delete from "@material-ui/icons/Delete";
import Edit from "@material-ui/icons/Edit";
import MoreVert from "@material-ui/icons/MoreVert";
import OpenWith from "@material-ui/icons/OpenWith";
import classNames from "classnames";
import React, { useContext, useRef, useState } from "react";
import { RouteComponentProps, withRouter } from "react-router-dom";
import { Loader } from "../../components/loader/Loader";
import { MessageSnackbar } from "../../components/messages/MessageSnackbar";
import { Popover } from "../../components/portal/Popover";
import { CreateCategory } from "../../components/tabContent/CreateCategory";
import { categoryActiveSet } from "../../context/actions/category";
import { GlobalContext } from "../../context/Context";
import {
  STATUS_ERROR_COUNT_CHANGE,
  STATUS_ERROR_TEXT_SET,
} from "../../context/reducers/status";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { CategoryProps } from "../../types/category";
import { api } from "../../utils/api";
import { CategoriesMove } from "./CategoriesMove";
import { CategoriesRename } from "./CategoriesRename";

interface CategoriesPopoverProps {
  category: CategoryProps;
  loadCategories: Function;
  iconBgChange?: boolean;
  parentCategory?: CategoryProps;
  moveForward?: () => void;
  moveBackward?: () => void;
}

const CategoriesPopoverView: React.FC<
  CategoriesPopoverProps & RouteComponentProps
> = ({
  category,
  loadCategories,
  history,
  iconBgChange,
  parentCategory,
  moveBackward,
  moveForward,
}) => {
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
                {moveForward && (
                  <MenuItem onClick={moveForward}>
                    <ListItemIcon>
                      <ArrowRight />
                    </ListItemIcon>
                    <ListItemText primary="Posunout dopředu" />
                  </MenuItem>
                )}
                {moveBackward && (
                  <MenuItem onClick={moveBackward}>
                    <ListItemIcon>
                      <ArrowLeft />
                    </ListItemIcon>
                    <ListItemText primary="Posunout dozadu" />
                  </MenuItem>
                )}
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
