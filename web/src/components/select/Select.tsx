import React, { CSSProperties } from "react";
import { renderToStaticMarkup } from "react-dom/server";
import { get, find } from "lodash";
import { useTheme } from "@material-ui/core/styles";
import NoSsr from "@material-ui/core/NoSsr";
import ReactSelect, { ValueType } from "react-select";
import CreatableSelect from "react-select/creatable";
import Add from "@material-ui/icons/Add";

import { SelectControl } from "./SelectControl";
import { SelectMenu } from "./SelectMenu";
import { SelectMultiValue } from "./SelectMultiValue";
import { SelectNoOptions } from "./SelectNoOptions";
import { SelectOption } from "./SelectOption";
import { SelectPlaceholder } from "./SelectPlaceholder";
import { SelectSingleValue } from "./SelectSingleValue";
import { SelectValueContainer } from "./SelectValueContainer";

import { OptionType } from "./_types";
import { useStyles } from "./_styles";

let components = {
  Control: SelectControl,
  Menu: SelectMenu,
  MultiValue: SelectMultiValue,
  NoOptionsMessage: SelectNoOptions,
  Option: SelectOption,
  Placeholder: SelectPlaceholder,
  SingleValue: SelectSingleValue,
  ValueContainer: SelectValueContainer,
};

export interface SelectProps {
  value?: any;
  onChange?: (value: any) => void;
  label?: string | JSX.Element;
  loading?: boolean;
  options: OptionType[];
  onCreate?: (inputValue: string) => void;
  placeholder?: string;
  autoFocus?: boolean;
  menuIsOpen?: boolean;
  isMulti?: boolean;
  onInputChangeCallback?: (value: string) => void;
  customFilter?: (candidate: OptionType, input: string) => boolean;
  parseSelectedLabel?: (value: string) => string;
  createLabel?: string;
  isClearable?: boolean;
}

export const Select: React.FC<SelectProps> = ({
  value,
  onChange = () => {},
  onCreate,
  label,
  options,
  menuIsOpen,
  loading = false,
  isMulti = false,
  onInputChangeCallback,
  customFilter,
  placeholder = "Vyberte",
  createLabel = "Vytvořit: ",
  parseSelectedLabel: parseValueOnSelect = (value: string) => value,
  ...props
}) => {
  const classes = useStyles();
  const theme = useTheme();

  const handleChange = (value: ValueType<any>) => {
    onChange(
      value
        ? isMulti
          ? value.map((o: OptionType) => o.value)
          : value.value
        : isMulti
        ? []
        : null
    );
  };

  const createValue = (value: string) =>
    typeof value === "string"
      ? {
          value,
          label: parseValueOnSelect(
            get(
              find(options, (o) => o.value === value),
              "label",
              ""
            )
          ),
        }
      : value;

  const componentProps = {
    noOptionsMessage: () => "Nic nenalezeno",
    loadingMessage: () => "Načítá se...",
    placeholder,
    options: options.length > 0 ? options : undefined,
    isLoading: loading,
    isMulti,
    isClearable: true,
    defaultMenuIsOpen: menuIsOpen,
    onInputChange: (text: string) => {
      onInputChangeCallback && onInputChangeCallback(text);
    },
    TextFieldProps: {
      label: label,
      InputLabelProps: {
        shrink: true,
      },
    },
    value: isMulti
      ? (value || []).map(createValue)
      : value
      ? createValue(value)
      : undefined,
    onChange: handleChange,
    filterOption: customFilter,
    components,
    closeMenuOnSelect: !isMulti,
    classes: classes,
    styles: {
      input: (styles: CSSProperties) => ({
        ...styles,
        color: theme.palette.text.primary,
        "& input": {
          font: "inherit",
        },
      }),
      menuPortal: (styles: CSSProperties) => ({ ...styles, zIndex: 10000 }),
      menuList: (styles: CSSProperties) => ({ ...styles, maxHeight: 200 }),
    },
    ...props,
  };

  return (
    <>
      <div className={classes.root}>
        <NoSsr>
          {onCreate ? (
            <CreatableSelect
              onCreateOption={onCreate}
              formatCreateLabel={(inputValue: string) =>
                renderToStaticMarkup(
                  <span className={classes.createLabel}>
                    <Add className={classes.createLabelAddIcon} /> {createLabel}{" "}
                    {inputValue}
                  </span>
                )
              }
              {...componentProps}
            />
          ) : (
            <ReactSelect {...(componentProps as any)} />
          )}
        </NoSsr>
      </div>
    </>
  );
};
