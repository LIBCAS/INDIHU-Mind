import React, { useContext } from "react";
import { withRouter, RouteComponentProps } from "react-router-dom";
import Typography from "@material-ui/core/Typography";

import { GlobalContext } from "../../context/Context";
import { labelActiveSet } from "../../context/actions/label";
import { useStyles } from "./_cardStyles";
import { RecordProps } from "../../types/record";

interface RecordPropsView {
  record: RecordProps;
}

const RecordView: React.FC<RecordPropsView & RouteComponentProps> = ({
  record,
  history
}) => {
  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;
  const classes = useStyles();
  const onClick = () => {
    history.push(`/record/${record.id}`);
  };
  return (
    <div
      key={record.id}
      className={classes.label}
      style={{ marginRight: 8 }}
      onClick={onClick}
    >
      <Typography className={classes.labelText}>{record.name}</Typography>
    </div>
  );
};

export const Record = withRouter(RecordView);
