import React, { useState } from "react";
import { filter, find, get } from "lodash";
import { Field, FieldProps } from "formik";
import { Tooltip } from "@material-ui/core";
import InfoIcon from "@material-ui/icons/Info";

import { MultipleField } from "../../components/form/MultipleField";
import { RecordFormValues, Creator } from "./RecordsForm";
import {
  getCreatorLabel,
  isCorporate,
  clearCreatorValue,
  clearAuthorData
} from "../recordsTemplates/_utils";

interface CreatorFieldProps {
  items?: Creator[];
}

export const CreatorField: React.FC<CreatorFieldProps> = ({ items = [] }) => {
  const [stateKey, setStateKey] = useState(false);

  return (
    <>
      {items.map(({ value, data }, index) => {
        const notCorporate = !isCorporate(value);
        return (
          <Field
            key={`${value}${index}${stateKey}`}
            name={`creators[${index}]`}
            render={({ field, form }: FieldProps<RecordFormValues>) => (
              <MultipleField
                field={field}
                form={form}
                items={[
                  { code: "a" },
                  { code: "a", isCorporate: true },
                  { code: "e" }
                ].map(({ code, isCorporate = false }) => ({
                  label: getCreatorLabel(isCorporate, code),
                  value: `${isCorporate ? "_" : ""}${code}`
                }))}
                inputPath="data"
                selectPath="value"
                onAdd={({ selectValue }) =>
                  form.setFieldValue(
                    `creators[${index + 1}].value`,
                    selectValue
                  )
                }
                onRemove={() => {
                  form.setFieldValue(
                    "creators",
                    filter(items, (_, i) => i !== index)
                  );
                  setStateKey(!stateKey);
                }}
                onSelect={newValue => {
                  if (isCorporate(newValue) && notCorporate && data) {
                    form.setFieldValue(
                      `creators[${index}].data`,
                      clearAuthorData(data)
                    );
                  }
                }}
                enableAdd={
                  !find(
                    items,
                    ({ value, data }) =>
                      !data || (!isCorporate(value) && !/\S+#&&#/.test(data))
                  ) && index === items.length - 1
                }
                invisibleAdd={index < items.length - 1}
                enableRemove={!!index || items.length > 1}
                showSecondInput={notCorporate}
                placeholder="Název"
                firstPlaceholder="Příjmení"
                secondPlaceholder="Jméno"
                LabelIcon={({ selectValue }: any) => (
                  <Tooltip
                    title={`${index ? "7" : "1"}${
                      isCorporate(selectValue) ? 1 : 0
                    }0 ${clearCreatorValue(selectValue)}`}
                  >
                    <InfoIcon />
                  </Tooltip>
                )}
              />
            )}
          />
        );
      })}
    </>
  );
};
