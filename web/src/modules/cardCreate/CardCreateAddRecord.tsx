import React, { useState, useEffect, useContext } from "react";
import { Field, FieldProps } from "formik";

import { CreateLabel } from "../../components/tabContent/CreateLabel";
import { recordGet } from "../../context/actions/record";
import { Modal } from "../../components/portal/Modal";
import { LabelProps } from "../../types/label";
import { GlobalContext, StateProps } from "../../context/Context";
import { OptionType } from "../../components/form/reactSelect/_reactSelectTypes";
import { ReactSelect } from "../../components/form/reactSelect/ReactSelect";

import { parseLabel } from "./_utils";

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

  useEffect(() => {
    recordGet(dispatch);
  }, []);
  useEffect(() => {
    const recordsParsed = state.record.records.map(parseLabel);
    setOptions(recordsParsed);
  }, [state.record.records]);
  return (
    <Field
      name="records"
      render={({ field, form }: FieldProps<any>) => (
        <>
          <ReactSelect
            form={form}
            field={field}
            loading={false}
            label="Citace"
            options={options}
          />
        </>
      )}
    />
  );
};
