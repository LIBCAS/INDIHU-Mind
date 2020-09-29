import React, { useEffect, useState } from "react";
import { find } from "lodash";

import { Select, SelectProps, OptionType } from "../select";

export interface AsyncSelectProps extends Omit<SelectProps, "options"> {
  loadOptions: (text: string | undefined, options: any[]) => Promise<any[]>;
  valueMapper?: (o: any) => string;
  labelMapper?: (o: any) => string;
}

export const AsyncSelect: React.FC<AsyncSelectProps> = ({
  value,
  onChange,
  loadOptions,
  valueMapper = (o: any) => o.id,
  labelMapper = (o: any) => o.name,
  ...props
}) => {
  const [options, setOptions] = useState<any[]>([]);
  const [loading, setLoading] = useState<boolean>(false);

  const getOptions = async (text?: string) => {
    setLoading(true);
    const newOptions = (await loadOptions(text, options)) || [];
    const mergedOptions = newOptions.concat(
      options.filter(o => !newOptions.some(nO => nO.id === o.id))
    );
    setOptions(mergedOptions);
    setLoading(false);
  };

  const mapOptionValue = (value: string) =>
    find(options, o => valueMapper(o) === value) || null;

  const handleChange = (value: any) => {
    onChange &&
      onChange(
        props.isMulti
          ? (value || []).map(mapOptionValue)
          : mapOptionValue(value)
      );
  };

  const createValue = (o: any) => ({
    value: valueMapper(o),
    label: labelMapper(o)
  });

  useEffect(() => {
    getOptions();
  }, []);

  return (
    <Select
      {...props}
      value={
        props.isMulti
          ? (value || []).map(createValue)
          : value
          ? createValue(value)
          : undefined
      }
      onChange={handleChange}
      options={options.map(createValue)}
      loading={loading}
      onInputChangeCallback={getOptions}
    />
  );
};
