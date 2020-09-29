import React, { useState, useEffect, useContext } from "react";
import { Field, FieldProps } from "formik";

import { CreateLabel } from "../../components/tabContent/CreateLabel";
import { labelGet } from "../../context/actions/label";
import { Modal } from "../../components/portal/Modal";
import { Select } from "../../components/form/Select";
import { LabelProps } from "../../types/label";
import { GlobalContext, StateProps } from "../../context/Context";
import { OptionType } from "../../components/select/_types";

import { parseLabel } from "./_utils";

interface CardCreateAddLabelProps {
  formikBag: any;
}

export const CardCreateAddLabel: React.FC<CardCreateAddLabelProps> = ({
  formikBag
}) => {
  const context: any = useContext(GlobalContext);
  const state: StateProps = context.state;
  const dispatch: Function = context.dispatch;

  const [open, setOpen] = useState(false);
  const [createValue, setCreateValue] = useState<string>("");
  const [options, setOptions] = useState<OptionType[]>([]);

  const loadLabels = (created: LabelProps) => {
    labelGet(dispatch);
    formikBag.setFieldValue("labels", [
      ...formikBag.values.labels,
      parseLabel(created)
    ]);
  };
  const onCreate = (inputValue: string) => {
    setCreateValue(inputValue);
    setOpen(true);
  };

  useEffect(() => {
    const labelsParsed = state.label.labels.map(parseLabel);
    setOptions(labelsParsed);
  }, [state.label.labels]);
  return (
    <Field
      name="labels"
      render={({ field, form }: FieldProps<any>) => (
        <div>
          <Select
            form={form}
            field={field}
            loading={false}
            isMulti={true}
            label="Štítky"
            options={options}
            onCreate={onCreate}
          />
          <Modal
            open={open}
            setOpen={setOpen}
            content={
              <CreateLabel
                setOpen={setOpen}
                loadLabels={loadLabels}
                name={createValue}
              />
            }
          />
        </div>
      )}
    />
  );
};
