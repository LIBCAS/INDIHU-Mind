import React, { useContext } from "react";
import { withRouter, RouteComponentProps } from "react-router-dom";
import MuiTypography from "@material-ui/core/Typography";
import MuiClearIcon from "@material-ui/icons/Clear";
import MuiTooltip from "@material-ui/core/Tooltip";

import { onEditCard } from "./_utils";
import { formatCategoryName } from "../../../utils/category";

import { Popconfirm } from "../../../components/portal/Popconfirm";
import { CardContentProps } from "../../../types/card";
import { GlobalContext } from "../../../context/Context";
import { categoryActiveSet } from "../../../context/actions/category";
import { CategoryProps } from "../../../types/category";
import { useStyles } from "./_cardStyles";

interface CardDetailContentCategoryViewProps {
  card: CardContentProps;
  cardContent: CardContentProps[] | undefined;
  setCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
  category: CategoryProps;
}

const CardDetailContentCategoryView: React.FC<
  CardDetailContentCategoryViewProps & RouteComponentProps
> = ({ card, setCardContent, category, history }) => {
  const context: any = useContext(GlobalContext);

  const dispatch: Function = context.dispatch;

  const classes = useStyles();

  const handleCategoryClick = () => {
    categoryActiveSet(dispatch, category);

    history.push("/cards");
  };

  const handleDelete = () => {
    const categories = card.card.categories.filter(
      (cat) => cat.id !== category.id
    );

    onEditCard("categories", categories, card, setCardContent);
  };

  return (
    <MuiTypography
      component="div"
      key={category.id}
      variant="body1"
      className={classes.category}
    >
      <MuiTooltip title="Kliknutím přejdete na detail kategorie.">
        <div onClick={handleCategoryClick}>{formatCategoryName(category)}</div>
      </MuiTooltip>
      {card.lastVersion && (
        <Popconfirm
          confirmText="Odebrat kategorii?"
          onConfirmClick={handleDelete}
          tooltip="Kliknutím odeberete kategorii"
          acceptText="Ano"
          cancelText="Ne"
          Button={
            <MuiClearIcon fontSize="small" className={classes.removeCategory} />
          }
        />
      )}
    </MuiTypography>
  );
};

export const CardDetailContentCategory = withRouter(
  CardDetailContentCategoryView
);
