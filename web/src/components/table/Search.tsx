import React, { useState } from "react";
import { debounce } from "lodash";
import MuiTextField from "@material-ui/core/TextField";

export interface SearchProps {
  searchText: string;
  onChange: (searchText: string) => void;
}

export const Search: React.FC<SearchProps> = ({ searchText, onChange }) => {
  const [value, setValue] = useState(searchText);

  const onDebouncedChange = debounce(onChange, 500);

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = event.target.value;
    setValue(newValue);
    onDebouncedChange(newValue);
  };

  return (
    <form autoComplete="off">
      <MuiTextField
        id="table-search-box"
        label="Vyhledat"
        variant="outlined"
        value={value}
        onChange={handleChange}
      />
    </form>
  );
};
