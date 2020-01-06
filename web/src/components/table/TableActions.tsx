import React, { useContext } from "react";
import Edit from "@material-ui/icons/Edit";
import Delete from "@material-ui/icons/Delete";
import classNames from "classnames";
import Tooltip from "@material-ui/core/Tooltip";
import IconButton from "@material-ui/core/IconButton";

import { Popconfirm } from "../../components/portal/Popconfirm";

import { useStyles as useEffectStyles } from "../../theme/styles/effectStyles";
import { useStyles } from "./_tableStyles";
import { api } from "../../utils/api";
import { GlobalContext } from "../../context/Context";
import {
  STATUS_ERROR_TEXT_SET,
  STATUS_ERROR_COUNT_CHANGE
} from "../../context/reducers/status";

interface TableActionsProps {
  baseUrl: string;
  setMenuOpen: (value: React.SetStateAction<boolean>) => void;
  selectRow: (event: any, row: any) => void;
  row: any;
  setCheckboxRows: any;
  loadData: any;
  loading: boolean;
  setLoading: any;
}

export const TableActions: React.FC<TableActionsProps> = ({
  setMenuOpen,
  row,
  baseUrl,
  setCheckboxRows,
  loadData,
  setLoading,
  selectRow
}) => {
  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;
  const classes = useStyles();
  const classesEffect = useEffectStyles();
  return (
    <div className={classes.iconsWrapper} onClick={e => e.stopPropagation()}>
      <Popconfirm
        Button={() => (
          <Tooltip title="Smazat">
            <IconButton
              className={classNames(
                classesEffect.hoverSecondary,
                classes.icons
              )}
            >
              <Delete />
            </IconButton>
          </Tooltip>
        )}
        confirmText="Smazat?"
        onConfirmClick={() => {
          const request = api().delete(`${baseUrl}/${row.id}`);
          request
            .then(() => {
              // Reload table data
              setCheckboxRows([]);
              loadData();
              setLoading(false);
              dispatch({
                type: STATUS_ERROR_TEXT_SET,
                payload: `Smazáno`
              });
              dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
            })
            .catch(() => {
              dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
              setLoading(false);
            });
        }}
      />
      <Tooltip title="Editovat">
        <IconButton
          onClick={e => {
            selectRow(e, row);
            setMenuOpen(prev => !prev);
            // history.push(`/${baseUrl}/${row.id}`);
          }}
          className={classNames(classesEffect.hoverPrimary, classes.icons)}
        >
          <Edit />
        </IconButton>
      </Tooltip>
    </div>
  );
};
