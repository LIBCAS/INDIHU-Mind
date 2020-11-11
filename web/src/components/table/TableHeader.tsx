import React from "react";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import TableCell from "@material-ui/core/TableCell";
import Tooltip from "@material-ui/core/Tooltip";
import TableSortLabel from "@material-ui/core/TableSortLabel";

import { TableCheckbox } from "./TableCheckbox";
import { Order } from "./_enums";
import { useStyles } from "./_styles";

interface TableHeadProps {
  columns: any;
  order: Order;
  sort: string | undefined;
  onRequestSort: Function;
  checkboxSelectAll: (e: any) => void;
  isCheckboxSelectedAll: () => boolean;
  enableSort?: boolean;
}

export const TableHeader: React.FC<TableHeadProps> = ({
  columns,
  order,
  sort,
  onRequestSort,
  checkboxSelectAll,
  isCheckboxSelectedAll,
  enableSort = true,
}) => {
  const classes = useStyles();
  const createSortHandler = (property: any) => (event: any) => {
    onRequestSort(event, property);
  };
  const direction = order.toLocaleLowerCase() as "asc" | "desc";
  return (
    <TableHead>
      <TableRow style={{ cursor: "auto" }}>
        <TableCell
          className={classes.checkbox}
          padding="checkbox"
          onClick={checkboxSelectAll}
        >
          <TableCheckbox checked={isCheckboxSelectedAll()} />
        </TableCell>
        {columns.map((column: any) => {
          const active = sort === column.field;
          return (
            <TableCell
              key={column.id}
              align={column.numeric ? "right" : "left"}
              padding={column.disablePadding ? "none" : undefined}
              sortDirection={active ? direction : undefined}
            >
              {column.unsortable || !enableSort ? (
                <span
                  style={{
                    textTransform: "uppercase",
                    fontWeight: 800,
                    whiteSpace: "nowrap",
                  }}
                >
                  {column.name}
                </span>
              ) : (
                <Tooltip
                  title="Seřadit"
                  placement={column.numeric ? "bottom-end" : "bottom-start"}
                  enterDelay={300}
                >
                  <TableSortLabel
                    style={{
                      textTransform: "uppercase",
                      fontWeight: 800,
                      whiteSpace: "nowrap",
                    }}
                    active={active}
                    direction={direction}
                    onClick={createSortHandler(column.field)}
                  >
                    {column.name}
                  </TableSortLabel>
                </Tooltip>
              )}
            </TableCell>
          );
        })}
      </TableRow>
    </TableHead>
  );
};
