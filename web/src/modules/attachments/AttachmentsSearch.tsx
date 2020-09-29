import React, { useState } from "react";
import { debounce } from "lodash";
import MuiTextField from "@material-ui/core/TextField";

export interface AttachmentsSearchProps {
  searchText?: string;
  onChange: (searchText: string) => void;
}

export const AttachmentsSearch: React.FC<AttachmentsSearchProps> = ({
  searchText,
  onChange
}) => {
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
        id="attachments-search-box"
        label="Vyhledat podle názvu"
        variant="outlined"
        value={value}
        onChange={handleChange}
      />
    </form>
  );
};
