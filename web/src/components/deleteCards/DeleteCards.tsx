import React from "react";
import Tooltip from "@material-ui/core/Tooltip";
import IconButton from "@material-ui/core/IconButton";
import Delete from "@material-ui/icons/Delete";
import { withRouter, RouteComponentProps } from "react-router-dom";

export const DeleteCardsView: React.FC<RouteComponentProps> = ({ history }) => {
  return (
    <Tooltip title="Koš">
      <IconButton color="inherit" onClick={() => history.push("/bin")}>
        <Delete color="inherit" />
      </IconButton>
    </Tooltip>
  );
};

export const DeleteCards = withRouter(DeleteCardsView);
