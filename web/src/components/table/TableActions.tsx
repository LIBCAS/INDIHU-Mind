import React from "react";
import { RouteComponentProps, withRouter } from "react-router-dom";
import Edit from "@material-ui/icons/Edit";
import OpenInNew from "@material-ui/icons/OpenInNew";
import Delete from "@material-ui/icons/Delete";
import classNames from "classnames";
import Tooltip from "@material-ui/core/Tooltip";
import IconButton from "@material-ui/core/IconButton";

import { Popconfirm } from "../../components/portal/Popconfirm";

import { useStyles as useEffectStyles } from "../../theme/styles/effectStyles";
import { useStyles } from "./_styles";
import { FormType } from "./_enums";
import { TableActionsProps } from "./_types";
import { openInNewTab } from "../../utils";

const TableActionsView: React.FC<TableActionsProps & RouteComponentProps> = ({
  setShowForm,
  row,
  baseUrl,
  selectRow,
  redirectOnEdit,
  handleDelete,
  history,
  enableOpenInNewTab,
}) => {
  const classes = useStyles();
  const classesEffect = useEffectStyles();

  const url = `/${baseUrl}/${row.id}`;

  return (
    <div className={classes.iconsWrapper} onClick={(e) => e.stopPropagation()}>
      <Tooltip title="Editovat">
        <IconButton
          onClick={(e) => {
            if (redirectOnEdit) {
              history.push(url);
            } else {
              selectRow(e, row);
              setShowForm(FormType.EDIT);
            }
          }}
          className={classNames(classesEffect.hoverPrimary, classes.icons)}
        >
          <Edit />
        </IconButton>
      </Tooltip>
      {enableOpenInNewTab ? (
        <Tooltip title="Otevřít v nové záložce">
          <IconButton
            onClick={() => openInNewTab(`${window.location.origin}${url}`)}
            className={classNames(classesEffect.hoverPrimary, classes.icons)}
          >
            <OpenInNew />
          </IconButton>
        </Tooltip>
      ) : (
        <></>
      )}
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
        confirmText="Smazat?"
        onConfirmClick={handleDelete}
      />
    </div>
  );
};

export const TableActions = withRouter(TableActionsView);
