import React, { useState, useContext } from "react";
import Delete from "@material-ui/icons/Delete";
import Edit from "@material-ui/icons/Edit";
import Tooltip from "@material-ui/core/Tooltip";
import IconButton from "@material-ui/core/IconButton";

import { GlobalContext } from "../../context/Context";
import {
  STATUS_ERROR_COUNT_CHANGE,
  STATUS_ERROR_TEXT_SET
} from "../../context/reducers/status";
import { LabelProps } from "../../types/label";
import { api } from "../../utils/api";

import { MessageSnackbar } from "../messages/MessageSnackbar";
import { Loader } from "../loader/Loader";
import { Popconfirm } from "../portal/Popconfirm";

import { useStyles as useEffectStyles } from "../../theme/styles/effectStyles";
import { useStyles } from "./LabelsStyles";

interface LabelsActionsProps {
  label: LabelProps;
  loadLabels: Function;
  setEditLabel: Function;
  setEditOpen: Function;
  setActiveLabel: Function;
}

export const LabelsActions: React.FC<LabelsActionsProps> = ({
  label,
  setActiveLabel,
  loadLabels,
  setEditLabel,
  setEditOpen
}) => {
  const classes = useStyles();
  const classesEffect = useEffectStyles();
  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;
  const [error, setError] = useState(false);
  const [loading, setLoading] = useState<boolean>(false);

  const onDelete = () => {
    if (loading) return false;
    setLoading(true);
    api()
      .delete(`label/${label.id}`)
      .then(() => {
        dispatch({
          type: STATUS_ERROR_TEXT_SET,
          payload: `Štítek ${label.name} byl úspěšně odstraněn`
        });
        dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
        setLoading(false);
        loadLabels();
      })
      .catch(() => {
        setLoading(false);
        setError(true);
      });
  };
  const onEdit = (e: any): void => {
    e.stopPropagation();
    setActiveLabel(label);
    setEditLabel(label);
    setEditOpen(true);
  };
  return (
    <div className={classes.wrapperIcons}>
      {error && <MessageSnackbar setVisible={setError} />}
      <Loader loading={loading} />
      <Tooltip title="Editovat">
        <IconButton
          color="inherit"
          onClick={onEdit}
          className={classesEffect.hoverPrimary}
          size="small"
        >
          <Edit style={{ fontSize: "18px" }} />
        </IconButton>
      </Tooltip>
      <Popconfirm
        confirmText="Opravdu chcete smazat tento štítek"
        onConfirmClick={onDelete}
        Button={() => (
          <Tooltip title="Smazat">
            <IconButton
              color="inherit"
              className={classesEffect.hoverSecondary}
              onClick={() => setActiveLabel(label)}
              size="small"
            >
              <Delete style={{ fontSize: "18px" }} />
            </IconButton>
          </Tooltip>
        )}
      />
    </div>
  );
};
