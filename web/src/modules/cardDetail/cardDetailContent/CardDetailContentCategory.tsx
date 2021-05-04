import MuiTooltip from "@material-ui/core/Tooltip";
import MuiTypography from "@material-ui/core/Typography";
import MuiClearIcon from "@material-ui/icons/Clear";
import React, { useContext } from "react";
import { RouteComponentProps, withRouter } from "react-router-dom";
import { Popconfirm } from "../../../components/portal/Popconfirm";
import { categoryActiveSet } from "../../../context/actions/category";
import { GlobalContext } from "../../../context/Context";
import { CardContentProps, CardProps } from "../../../types/card";
import { CategoryProps } from "../../../types/category";
import { formatCategoryName } from "../../../utils/category";
import { useStyles } from "./_cardStyles";
import { onEditCard } from "./_utils";

interface CardDetailContentCategoryViewProps {
  card: CardProps;
  setCard: React.Dispatch<React.SetStateAction<CardProps | undefined>>;
  currentCardContent: CardContentProps;
  setCardContents: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
  category: CategoryProps;
  disabled?: boolean;
}

const CardDetailContentCategoryView: React.FC<
  CardDetailContentCategoryViewProps & RouteComponentProps
> = ({
  card,
  setCard,
  currentCardContent,
  setCardContents,
  category,
  history,
  disabled,
}) => {
  const context: any = useContext(GlobalContext);

  const dispatch: Function = context.dispatch;

  const classes = useStyles();

  const handleCategoryClick = () => {
    categoryActiveSet(dispatch, category);

    history.push("/cards");
  };

  const handleDelete = () => {
    const categories = card.categories.filter((cat) => cat.id !== category.id);

    onEditCard(
      "categories",
      categories,
      card,
      setCard,
      currentCardContent,
      setCardContents
    );
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
      {!disabled && currentCardContent.lastVersion && (
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
