import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import classNames from "classnames";
import React, { useCallback, useContext, useEffect, useState } from "react";
import { labelActiveSet, labelGet } from "../../context/actions/label";
import { GlobalContext, StateProps } from "../../context/Context";
import { LabelProps } from "../../types/label";
import { TabProps } from "../leftPanel/LeftPanelContent";
import { Modal } from "../portal/Modal";
import { CreateLabel } from "./CreateLabel";
import { LabelsActions } from "./LabelsActions";
import { useStyles } from "./LabelsStyles";

interface LabelsProps {
  activeTab: TabProps;
  setActiveCallback: () => void;
}

export const Labels: React.FC<LabelsProps> = ({
  activeTab,
  setActiveCallback,
}) => {
  const context: any = useContext(GlobalContext);
  const state: StateProps = context.state;
  const dispatch: Function = context.dispatch;
  const { labelActive } = state.label;
  const [editLabel, setEditLabel] = useState<LabelProps | undefined>(undefined);
  const [open, setOpen] = useState<boolean>(false);
  const classes = useStyles();

  const loadLabels = useCallback(() => {
    labelGet(dispatch);
  }, [dispatch]);
  useEffect(() => {
    loadLabels();
  }, [loadLabels]);

  useEffect(() => {
    if (activeTab !== "label") labelActiveSet(dispatch, undefined);
  }, [activeTab, dispatch]);

  const setActiveLabel = (label: LabelProps | undefined) => {
    setActiveCallback();
    labelActiveSet(dispatch, label);
  };

  return (
    <>
      <Button
        onClick={() => {
          setEditLabel(undefined);
          setOpen(true);
        }}
        className={classes.createButton}
        size="small"
        color="inherit"
        fullWidth
      >
        + Vytvořit nový
      </Button>
      <Modal
        open={open}
        setOpen={setOpen}
        content={
          <CreateLabel
            loadLabels={loadLabels}
            setOpen={setOpen}
            previousLabel={editLabel}
          />
        }
      />
      {state.label.labels.map((l) => {
        const isActive = labelActive && l.id === labelActive.id;
        const { color } = l;
        return (
          <div
            onClick={() =>
              isActive ? setActiveLabel(undefined) : setActiveLabel(l)
            }
            key={l.name}
            className={classNames({
              [classes.wrapper]: true,
              [classes.wrapperActive]: isActive,
            })}
          >
            <div className={classes.innerWrapper}>
              <Typography
                className={classNames({
                  [classes.label]: true,
                  [classes.labelActive]: isActive,
                })}
                variant="body1"
                color="inherit"
              >
                {l.name}
              </Typography>

              <span
                className={classes.dot}
                style={{
                  background: `${color}`,
                }}
              />

              <LabelsActions
                label={l}
                setActiveLabel={setActiveLabel}
                loadLabels={loadLabels}
                setEditLabel={setEditLabel}
                setEditOpen={setOpen}
              />
            </div>
          </div>
        );
      })}
    </>
  );
};
