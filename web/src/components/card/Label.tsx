import React, { useContext } from "react";
import { withRouter, RouteComponentProps } from "react-router-dom";
import Typography from "@material-ui/core/Typography";

import { GlobalContext } from "../../context/Context";
import { labelActiveSet } from "../../context/actions/label";
import { LabelProps as LabelTypeProps } from "../../types/label";
import { useStyles } from "./_cardStyles";

interface LabelProps {
  label: LabelTypeProps;
}

const LabelView: React.FC<LabelProps & RouteComponentProps> = ({
  label,
  history
}) => {
  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;
  const classes = useStyles();
  const onClick = () => {
    labelActiveSet(dispatch, label);
    history.push("/cards");
  };
  return (
    <div key={label.id} className={classes.label} onClick={onClick}>
      <Typography className={classes.labelText}>{label.name}</Typography>
      <span className={classes.labelDot} style={{ background: label.color }} />
    </div>
  );
};

export const Label = withRouter(LabelView);
