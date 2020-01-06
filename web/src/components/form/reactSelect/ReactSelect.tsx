import React, { CSSProperties } from "react";
import { useTheme } from "@material-ui/core/styles";
// import { NoticeProps } from "react-select/lib/components/Menu";
import { ValueType } from "react-select/lib/types";
import NoSsr from "@material-ui/core/NoSsr";
import Select from "react-select";
import CreatableSelect from "react-select/creatable";
import classNames from "classnames";

import { useStyles as useFormStyles } from "../_formStyles";

import { ReactSelectControl } from "./ReactSelectControl";
import { ReactSelectMenu } from "./ReactSelectMenu";
import { ReactSelectMultiValue } from "./ReactSelectMultiValue";
import { ReactSelectNoOptions } from "./ReactSelectNoOptions";
import { ReactSelectOption } from "./ReactSelectOption";
import { ReactSelectPlaceholder } from "./ReactSelectPlaceholder";
import { ReactSelectSingleValue } from "./ReactSelectSingleValue";
import { ReactSelectValueContainer } from "./ReactSelectValueContainer";

import { OptionType } from "./_reactSelectTypes";
import { useStyles } from "./_reactSelectStyles";
import { Typography } from "@material-ui/core";

let components = {
  Control: ReactSelectControl,
  Menu: ReactSelectMenu,
  MultiValue: ReactSelectMultiValue,
  NoOptionsMessage: ReactSelectNoOptions,
  Option: ReactSelectOption,
  Placeholder: ReactSelectPlaceholder,
  SingleValue: ReactSelectSingleValue,
  ValueContainer: ReactSelectValueContainer
};

interface ReactSelectProps {
  form: any;
  field: any;
  label?: string;
  loading: boolean;
  options: OptionType[];
  onCreate?: (inputValue: string) => void;
  placeholder?: string;
  autoFocus?: boolean;
  menuIsOpen?: boolean;
  isMulti?: boolean;
}

export const ReactSelect: React.FC<ReactSelectProps> = ({
  form,
  field,
  label,
  loading,
  options,
  onCreate,
  placeholder,
  autoFocus,
  menuIsOpen,
  isMulti = true
}) => {
  const classes = useStyles();
  const classesForm = useFormStyles();
  const theme = useTheme();

  const onChange = (value: ValueType<OptionType>) => {
    form.setFieldValue(field.name, value ? value : null);
  };

  const onChangeMulti = (value: ValueType<OptionType>) => {
    form.setFieldValue(field.name, value ? value : []);
  };

  const handleCreate = (inputValue: string) => {
    if (onCreate) {
      onCreate(inputValue);
    }
  };

  const selectStyles = {
    input: (base: CSSProperties) => ({
      ...base,
      color: theme.palette.text.primary,
      "& input": {
        font: "inherit"
      }
    })
  };
  return (
    <>
      <div className={classes.root}>
        <NoSsr>
          {onCreate ? (
            <CreatableSelect
              autoFocus={autoFocus}
              defaultMenuIsOpen={menuIsOpen}
              closeMenuOnSelect={false}
              classes={classes}
              styles={selectStyles}
              TextFieldProps={{
                label: label,
                InputLabelProps: {
                  shrink: true
                }
              }}
              options={options.length > 0 ? options : undefined}
              components={components}
              value={field.value}
              onChange={onChangeMulti}
              noOptionsMessage={() => "Nic nenalezeno"}
              placeholder={placeholder ? placeholder : "Vyberte"}
              isMulti
              isLoading={loading}
              onCreateOption={handleCreate}
              formatCreateLabel={(inputValue: string) =>
                `Vytvořit ${inputValue}`
              }
            />
          ) : (
            <>
              <Select
                autoFocus={autoFocus}
                defaultMenuIsOpen={menuIsOpen}
                closeMenuOnSelect={false}
                classes={classes}
                styles={selectStyles}
                TextFieldProps={{
                  label: label,
                  InputLabelProps: {
                    shrink: true
                  }
                }}
                options={options.length > 0 ? options : undefined}
                components={components}
                value={field.value}
                onChange={isMulti ? onChangeMulti : onChange}
                noOptionsMessage={() => "Nic nenalezeno"}
                placeholder={placeholder ? placeholder : "Vyberte"}
                isMulti={isMulti}
                isLoading={loading}
              />
              {form.touched[field.name] && form.errors[field.name] && (
                <Typography
                  className={classNames(classesForm.error)}
                  color="error"
                >
                  {form.touched[field.name] &&
                    form.errors[field.name] &&
                    form.errors[field.name]}
                </Typography>
              )}
            </>
          )}
        </NoSsr>
      </div>
    </>
  );
};
