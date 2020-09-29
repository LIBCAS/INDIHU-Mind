import React from "react";
import Tooltip from "@material-ui/core/Tooltip";
import IconButton from "@material-ui/core/IconButton";
import Delete from "@material-ui/icons/Delete";
import { withRouter, RouteComponentProps } from "react-router-dom";

interface DeleteCardsViewProps {
  setLeftPanelOpen?: Function;
}

export const DeleteCardsView: React.FC<DeleteCardsViewProps &
  RouteComponentProps> = ({ setLeftPanelOpen, history }) => {
  return (
    <Tooltip title="Koš">
      <IconButton
        color="inherit"
        onClick={() => {
          history.push("/bin");
          setLeftPanelOpen && setLeftPanelOpen(false);
        }}
      >
        <Delete color="inherit" />
      </IconButton>
    </Tooltip>
  );
};

export const DeleteCards = withRouter(DeleteCardsView);
