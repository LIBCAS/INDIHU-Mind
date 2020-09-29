import React, { useState, useEffect, useContext, useRef } from "react";
import Add from "@material-ui/icons/Add";
import Delete from "@material-ui/icons/Delete";

import { recordGet } from "../../context/actions/record";

import { GlobalContext, StateProps } from "../../context/Context";
import { OptionType } from "../../components/select/_types";

import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useEffectStyles } from "../../theme/styles/effectStyles";
import { useStyles } from "./_cardCreateStyles";

import { parseLabel } from "./_utils";
import Typography from "@material-ui/core/Typography";
import { ButtonGrey } from "../../components/control/ButtonGrey";
import { AddRecordPopper } from "../../components/record/AddRecordPopper";
import { RecordProps } from "../../types/record";
import classNames from "classnames";
import { Popconfirm } from "../../components/portal/Popconfirm";
import Tooltip from "@material-ui/core/Tooltip";

interface CardCreateAddRecordProps {
  formikBag: any;
}

export const CardCreateAddRecord: React.FC<CardCreateAddRecordProps> = ({
  formikBag
}) => {
  const context: any = useContext(GlobalContext);
  const state: StateProps = context.state;
  const dispatch: Function = context.dispatch;

  const [options, setOptions] = useState<OptionType[]>([]);
  const [popperOpen, setPopperOpen] = useState(false);

  const classesSpacing = useSpacingStyles();
  const classesText = useTextStyles();
  const classesLayout = useLayoutStyles();
  const classesEffect = useEffectStyles();
  const classes = useStyles();

  const cardCreateRecordRef = useRef(null);

  useEffect(() => {
    recordGet(dispatch);
  }, []);
  useEffect(() => {
    const recordsParsed = state.record.records
      .filter(
        rec =>
          !formikBag.values.records
            .map((record: RecordProps) => record.id)
            .includes(rec.id)
      )
      .map(parseLabel);
    setOptions(recordsParsed);
  }, [state.record.records, formikBag.values.records]);

  const onButtonClick = () => {
    setPopperOpen(prev => !prev);
  };
  const onSubmit = (values: RecordProps[]) => {
    const records = formikBag.values.records.concat(values);
    formikBag.setFieldValue("records", records);
  };
  const onDelete = (record: RecordProps) => {
    const records = formikBag.values.records.filter(
      (r: RecordProps) => r.id !== record.id
    );
    formikBag.setFieldValue("records", records);
  };

  return (
    <div>
      <div className={classesSpacing.mt2} />
      <Typography className={classesText.subtitle}>Citace</Typography>
      <div
        className={classNames(
          classesLayout.flex,
          classes.cardCreateAddRecordItemsContainer,
          classesLayout.flexWrap
        )}
      >
        {formikBag.values.records.map((record: any) => (
          <div key={record.id} className={classes.cardCreateAddRecordItem}>
            <span
              style={{
                maxWidth: "80%",
                textOverflow: "ellipsis",
                overflow: "hidden",
                whiteSpace: "nowrap"
              }}
            >
              {record.name}
            </span>
            <Popconfirm
              confirmText="Odebrat citaci z karty?"
              acceptText="Odebrat"
              onConfirmClick={(e: any) => {
                e.stopPropagation();
                onDelete(record);
              }}
              Button={
                <Tooltip title="Odebrat citaci z karty">
                  <div className={classesLayout.flex}>
                    <Delete
                      className={classNames(
                        classesSpacing.mr1,
                        classes.deleteIcon,
                        classesEffect.hoverSecondary
                      )}
                      color="action"
                    />
                  </div>
                </Tooltip>
              }
            />
          </div>
        ))}
      </div>
      <div ref={cardCreateRecordRef} className={classes.addWrapper}>
        <ButtonGrey
          text="Přidat citaci"
          onClick={onButtonClick}
          bold
          Icon={<Add fontSize="small" />}
        />
      </div>

      <AddRecordPopper
        open={popperOpen}
        setOpen={setPopperOpen}
        options={options}
        anchorEl={cardCreateRecordRef.current}
        onSubmit={onSubmit}
      />
    </div>
  );
};
