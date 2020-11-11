import React from "react";
import Checkbox from "@material-ui/core/Checkbox";
import Check from "@material-ui/icons/Check";
import CheckBoxOutlineBlank from "@material-ui/icons/CheckBoxOutlineBlank";

import { theme } from "../../theme/theme";

interface TableCheckboxProps {
  // onClick: Function;
  checked: boolean;
}

export const TableCheckbox: React.FC<TableCheckboxProps> = ({
  checked,
  // onClick
}) => {
  return (
    <Checkbox
      color="primary"
      checked={checked}
      onClick={() => {
        // onClick(e);
      }}
      icon={
        <CheckBoxOutlineBlank
          style={{
            color: theme.palette.primary.main,
            borderRadius: "3px",
            fontSize: "16px",
          }}
        />
      }
      checkedIcon={
        <Check
          style={{
            color: "#fff",
            background: theme.palette.primary.main,
            borderRadius: "3px",
            fontSize: "16px",
          }}
        />
      }
    />
  );
};
