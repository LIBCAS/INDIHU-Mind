import React from "react";
import Edit from "@material-ui/icons/Edit";
import Delete from "@material-ui/icons/Delete";
import classNames from "classnames";
import Tooltip from "@material-ui/core/Tooltip";
import IconButton from "@material-ui/core/IconButton";

import { Popconfirm } from "../portal/Popconfirm";

import { useStyles as useEffectStyles } from "../../theme/styles/effectStyles";
import { useStyles } from "./_tableStyles";

interface TableActionsProps {
  setMenuOpen: (value: React.SetStateAction<boolean>) => void;
  selectRow: (event: any, row: any) => void;
  handleDelete: (id: string, afterEdit: Function) => void;
  row: any;
  afterEdit: () => void;
  history: any;
}

export const TableActions: React.FC<TableActionsProps> = ({
  handleDelete,
  row,
  afterEdit,
  history
}) => {
  const classes = useStyles();
  const classesEffect = useEffectStyles();
  return (
    <div className={classes.iconsWrapper} onClick={e => e.stopPropagation()}>
      <Tooltip title="Editovat">
        <IconButton
          onClick={() => {
            // selectRow(e, row);
            // setMenuOpen(prev => !prev);
            history.push(`/card/${row.id}`);
          }}
          className={classNames(classesEffect.hoverPrimary, classes.icons)}
        >
          <Edit />
        </IconButton>
      </Tooltip>
      <Popconfirm
        Button={
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
        }
        confirmText="Smazat kartu?"
        onConfirmClick={() => {
          handleDelete(row.id, afterEdit);
        }}
      />
    </div>
  );
};
