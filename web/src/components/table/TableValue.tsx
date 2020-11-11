import React from "react";
import { get } from "lodash";
import MaterialSwitch from "@material-ui/core/Switch";

import { useStyles as useFormStyles } from "../form/_styles";

import { ColumnType } from "./_enums";
import { Column } from "./_types";
import { openInNewTab } from "../../utils";
import { formatDate, formatDateTime } from "../../utils/dateTime";

interface TableValueProps {
  row: any;
  column: Column;
}

export const TableValue: React.FC<TableValueProps> = ({
  row,
  column: { field, type, format, bold, enum: Enum },
}) => {
  const classesForm = useFormStyles();

  const value = get(row, field);

  const formatValue = () => {
    if (format) {
      return format(row);
    }

    switch (type) {
      case ColumnType.BOOLEAN:
        return (
          <MaterialSwitch
            disabled
            size="small"
            color="primary"
            checked={value}
            classes={{
              switchBase: classesForm.switchBase,
              disabled: classesForm.disabled,
              track: classesForm.track,
              checked: classesForm.checked,
              colorPrimary: classesForm.disabled,
            }}
          />
        );
      case ColumnType.DATE:
      case ColumnType.DATETIME:
        return type === ColumnType.DATETIME
          ? formatDateTime(value)
          : formatDate(value);
      case ColumnType.ENUM:
        return get(Enum, value, "Neznámý");
      case ColumnType.LINK:
        return (
          <span
            className={classesForm.link}
            onClick={() => value && openInNewTab(value)}
          >
            {value}
          </span>
        );
      default:
        return get(row, field, "?");
    }
  };

  return <span style={bold ? { fontWeight: 800 } : {}}>{formatValue()}</span>;
};
