import React, { useContext } from "react";
import { withRouter, RouteComponentProps } from "react-router-dom";
import Typography from "@material-ui/core/Typography";

import { GlobalContext } from "../../context/Context";
import { categoryActiveSet } from "../../context/actions/category";
import { CategoryProps as CategoryTypeProps } from "../../types/category";
import { useStyles } from "./_cardStyles";

interface CategoryProps {
  category: CategoryTypeProps;
}

const CategoryView: React.FC<CategoryProps & RouteComponentProps> = ({
  category,
  history,
}) => {
  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;
  const classes = useStyles();
  const onClick = () => {
    categoryActiveSet(dispatch, category);
    history.push("/cards");
  };
  return (
    <Typography
      onClick={onClick}
      key={category.id}
      variant="body1"
      className={classes.category}
    >
      {category.name}
    </Typography>
  );
};

export const Category = withRouter(CategoryView);
