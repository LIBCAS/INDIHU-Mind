import React, { useRef, useState, useContext } from "react";
import MenuList from "@material-ui/core/MenuList";
import MenuItem from "@material-ui/core/MenuItem";
import ListItemIcon from "@material-ui/core/ListItemIcon";
import ListItemText from "@material-ui/core/ListItemText";
import Button from "@material-ui/core/Button";
import MoreVert from "@material-ui/icons/MoreVert";
import Edit from "@material-ui/icons/Edit";
import Delete from "@material-ui/icons/Delete";
import classNames from "classnames";

import { GlobalContext } from "../../context/Context";
import {
  STATUS_ERROR_COUNT_CHANGE,
  STATUS_ERROR_TEXT_SET
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

interface CategoriesPopoverProps {
  category: CategoryProps;
  loadCategories: Function;
}

export const CategoriesPopover: React.FC<CategoriesPopoverProps> = ({
  category,
  loadCategories
}) => {
  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;
  const [open, setOpen] = useState(false);
  const [content, setContent] = useState<"menu" | "rename" | "delete">("menu");
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

  const handleDelete = () => {
    if (loading) return false;
    setLoading(true);
    api()
      .delete(`category/${category.id}`)
      .then(() => {
        dispatch({
          type: STATUS_ERROR_TEXT_SET,
          payload: `Kategorie ${category.name} byla úspěšně odstraněna`
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
    <>
      <Loader loading={loading} />
      {errorShow && <MessageSnackbar setVisible={setErrorShow} />}
      <div
        ref={menuRef}
        onClick={(e: any) => {
          e.stopPropagation();
          handleOpen(!open);
        }}
        className={classNames(classesText.icon, classesText.iconBig)}
      >
        <MoreVert fontSize="inherit" />
      </div>
      <Popover
        anchorEl={menuRef.current}
        open={open}
        setOpen={handleOpen}
        width={300}
        content={
          <div onClick={(e: any) => e.stopPropagation()}>
            {content === "menu" && (
              <MenuList>
                <MenuItem onClick={() => setContent("rename")}>
                  <ListItemIcon>
                    <Edit />
                  </ListItemIcon>
                  <ListItemText primary="Přejmenovat" />
                </MenuItem>
                <MenuItem onClick={() => setContent("delete")}>
                  <ListItemIcon>
                    <Delete />
                  </ListItemIcon>
                  <ListItemText primary="Odstranit" />
                </MenuItem>
              </MenuList>
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
                    classesText.textGrey,
                    classesText.textBold,
                    classesText.textCenter
                  )}
                >
                  Opravdu chcete smazat kategorii {category.name}
                  {category.subCategories && category.subCategories.length > 0
                    ? `, která má ${category.subCategories.length} podkategorie?`
                    : "?"}{" "}
                </div>
                <div
                  className={classNames(
                    classesSpacing.mt2,
                    classesLayout.flex,
                    classesLayout.spaceBetween,
                    classesLayout.flexGrow,
                    classesLayout.directionRowReverse
                  )}
                >
                  <Button variant="text" color="primary" onClick={handleDelete}>
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
    </>
  );
};
