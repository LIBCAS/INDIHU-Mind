import React, { useContext } from "react";
import { withRouter, RouteComponentProps } from "react-router-dom";
import Typography from "@material-ui/core/Typography";
import Cancel from "@material-ui/icons/Cancel";

import { onEditCard } from "./_utils";

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
  const onClick = () => {
    categoryActiveSet(dispatch, category);
    history.push("/cards");
  };
  const onDelete = () => {
    const categories = card.card.categories.filter(
      cat => cat.id !== category.id
    );
    // .map(cat => ({ id: cat.id }));
    onEditCard("categories", categories, card, setCardContent);
  };
  return (
    <Typography
      component="div"
      key={category.id}
      variant="body1"
      className={classes.category}
    >
      <div onClick={onClick}>{category.name}</div>
      {card.lastVersion && (
        <Popconfirm
          confirmText="Odebrat kategorii?"
          onConfirmClick={onDelete}
          tooltip="Smazat"
          Button={() => <Cancel />}
        />
      )}
    </Typography>
  );
};

export const CardDetailContentCategory = withRouter(
  CardDetailContentCategoryView
);
