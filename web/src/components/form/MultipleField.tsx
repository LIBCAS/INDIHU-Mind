import React from "react";
import { get } from "lodash";
import classNames from "classnames";
import TextField from "@material-ui/core/TextField";
import Input from "@material-ui/core/Input";
import MenuItem from "@material-ui/core/MenuItem";
import MaterialSelect from "@material-ui/core/Select";
import AddCircleIcon from "@material-ui/icons/AddCircle";
import RemoveCircleIcon from "@material-ui/icons/RemoveCircle";

import { useStyles as useFormStyles } from "./_styles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import {
  createCreatorValue,
  createCreatorRegex,
} from "../../modules/records/_utils";

interface CallbackParams {
  selectValue?: string;
}

interface MultipleFieldProps {
  field: any;
  form: any;
  label?: string | JSX.Element;
  items?: { label: string; value: string }[];
  inputPath: string;
  selectPath: string;
  onAdd: (params: CallbackParams) => void;
  onRemove: (params: CallbackParams) => void;
  onSelect?: (value: string) => void;
  enableAdd?: boolean;
  enableRemove?: boolean;
  invisibleAdd?: boolean;
  placeholder?: string;
  firstPlaceholder?: string;
  secondPlaceholder?: string;
  showSecondInput?: boolean;
  LabelIcon?: (props: any) => JSX.Element;
}

export const MultipleField: React.FC<MultipleFieldProps> = ({
  items,
  field,
  form,
  inputPath,
  selectPath,
  onAdd,
  onRemove,
  onSelect = () => {},
  enableAdd,
  enableRemove,
  invisibleAdd,
  placeholder,
  firstPlaceholder,
  secondPlaceholder,
  showSecondInput,
  LabelIcon,
}) => {
  const classesForm = useFormStyles();
  const classesLayout = useLayoutStyles();

  const realInputPath = `${field.name}${inputPath}`;
  const realSelectPath = `${field.name}${selectPath}`;
  const value = get(field, "value", "");
  const inputValue = get(value, inputPath, "");
  const selectValue = get(value, selectPath);
  const firstValue = inputValue.replace(
    createCreatorRegex({ suffix: ".*$" }),
    ""
  );
  const secondValue = createCreatorRegex().test(inputValue)
    ? inputValue.replace(createCreatorRegex({ prefix: "^.*" }), "")
    : "";

  const handleSelectChange = (e: any) => {
    const newValue = e.target.value;
    form.setFieldValue(realSelectPath, newValue);
    onSelect(newValue);
  };

  const handleFirstChange = (e: any) => {
    const newValue = e.target.value;
    form.setFieldValue(
      realInputPath,
      showSecondInput ? createCreatorValue(newValue, secondValue) : newValue
    );
  };

  const handleSecondChange = (e: any) => {
    form.setFieldValue(
      realInputPath,
      createCreatorValue(firstValue, e.target.value)
    );
  };

  return (
    <div
      className={classNames(
        classesLayout.flex,
        classesLayout.alignCenter,
        classesLayout.fullWidth
      )}
    >
      <div className={classesForm.labelSelectWrapper}>
        <MaterialSelect
          displayEmpty
          value={selectValue}
          onChange={handleSelectChange}
          input={
            <Input
              disableUnderline
              inputProps={{
                className: classNames(
                  classesForm.labelSelect,
                  LabelIcon && classesForm.labelSelectShort
                ),
              }}
            />
          }
        >
          {(items || []).map(({ value, label }) => (
            <MenuItem key={value} value={value}>
              {label}
            </MenuItem>
          ))}
        </MaterialSelect>
        {LabelIcon && <LabelIcon selectValue={selectValue} />}
      </div>
      <div className={classesForm.multipleFieldTextFields}>
        <TextField
          type="text"
          fullWidth={true}
          className={classNames(
            showSecondInput && classesForm.multipleFieldFirst
          )}
          InputProps={{
            autoComplete: "off",
            disableUnderline: true,
          }}
          inputProps={{
            className: classesForm.default,
          }}
          placeholder={showSecondInput ? firstPlaceholder : placeholder}
          value={firstValue}
          onChange={handleFirstChange}
        />
        {showSecondInput && (
          <TextField
            type="text"
            fullWidth={true}
            InputProps={{
              autoComplete: "off",
              disableUnderline: true,
            }}
            inputProps={{
              className: classesForm.default,
            }}
            placeholder={secondPlaceholder}
            value={secondValue}
            onChange={handleSecondChange}
          />
        )}
        {[
          {
            key: "remove",
            Icon: RemoveCircleIcon,
            disabled: !enableRemove,
            onClick: onRemove,
          },
          {
            key: "add",
            Icon: AddCircleIcon,
            disabled: !enableAdd,
            onClick: onAdd,
            invisible: invisibleAdd,
          },
        ].map(({ key, Icon, disabled, onClick, invisible }) => (
          <div
            key={key}
            className={classNames(
              classesForm.icon,
              disabled
                ? invisible
                  ? classesForm.invisible
                  : classesForm.disabledIcon
                : null
            )}
            onClick={() => !disabled && onClick({ selectValue })}
          >
            <Icon />
          </div>
        ))}
      </div>
    </div>
  );
};
