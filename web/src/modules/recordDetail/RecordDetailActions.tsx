import React, { useState, useContext } from "react";
import classNames from "classnames";
import KeyboardArrowLeft from "@material-ui/icons/KeyboardArrowLeft";
import Delete from "@material-ui/icons/Delete";
import IconButton from "@material-ui/core/IconButton";
import Tooltip from "@material-ui/core/Tooltip";
import Edit from "@material-ui/icons/Edit";

import { GlobalContext } from "../../context/Context";

import { Popconfirm } from "../../components/portal/Popconfirm";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles } from "./_recordDetailStyles";
import { Modal } from "../../components/portal/Modal";
import { RecordsForm } from "../records/RecordsForm";
import { RecordProps } from "../../types/record";
import { onDeleteRecord } from "../records/_utils";

interface RecordDetailActionsProps {
  record: RecordProps;
  history: any;
  loadRecord: () => void;
}

export const RecordDetailActions: React.FC<RecordDetailActionsProps> = ({
  record,
  history,
  loadRecord
}) => {
  const classes = useStyles();
  const classesLayout = useLayoutStyles();
  const classesText = useTextStyles();
  const [showModal, setShowModal] = useState(false);
  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;

  const handleDelete = () => {
    onDeleteRecord(record.id, dispatch, () => history.push("/records"));
  };

  return (
    <div className={classes.actionsWrapper}>
      <div className={classNames(classes.actionsBack, classes.actionsIcon)}>
        <IconButton
          onClick={() => history.goBack()}
          className={classNames(classesLayout.flex, classesLayout.alignCenter)}
        >
          <KeyboardArrowLeft fontSize={"large"} />
          <div className={classesText.normal}>Zpět</div>
        </IconButton>
      </div>
      <Tooltip title="Editovat">
        <IconButton
          color="inherit"
          onClick={() => setShowModal(true)}
          className={classes.actionsIcon}
        >
          <Edit />
        </IconButton>
      </Tooltip>
      <Popconfirm
        Button={() => (
          <Tooltip title="Smazat">
            <IconButton className={classes.iconSecondary}>
              <Delete color="inherit" />
            </IconButton>
          </Tooltip>
        )}
        confirmText="Smazat?"
        onConfirmClick={() => {
          handleDelete();
        }}
      />
      <Modal
        open={showModal}
        setOpen={setShowModal}
        content={
          <RecordsForm
            setShowModal={setShowModal}
            record={record}
            afterEdit={loadRecord}
          />
        }
      />
    </div>
  );
};
