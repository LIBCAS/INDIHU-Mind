import React from "react";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import TableCell from "@material-ui/core/TableCell";
import Tooltip from "@material-ui/core/Tooltip";
import TableSortLabel from "@material-ui/core/TableSortLabel";

import { TableCheckbox } from "./TableCheckbox";
import { OrderProps } from "./_types";
import { useStyles } from "./_tableStyles";

interface TableHeadProps {
  columns: any;
  order: OrderProps;
  onRequestSort: Function;
  selectedRow: any;
  matches1400: boolean;
  checkboxSelectAll: (e: any) => void;
  isCheckboxSelectedAll: () => boolean;
}

export const TableHeader: React.FC<TableHeadProps> = ({
  columns,
  order,
  onRequestSort,
  selectedRow,
  matches1400,
  checkboxSelectAll,
  isCheckboxSelectedAll
}) => {
  const classes = useStyles();
  const createSortHandler = (property: any) => (event: any) => {
    onRequestSort(event, property);
  };
  return (
    <TableHead>
      <TableRow>
        <TableCell
          className={classes.checkbox}
          padding="checkbox"
          onClick={checkboxSelectAll}
        >
          <TableCheckbox checked={isCheckboxSelectedAll()} />
        </TableCell>
        {matches1400 ? (
          columns.map((column: any) =>
            selectedRow && column.path === "note" ? null : (
              <TableCell
                key={column.id}
                align={column.numeric ? "right" : "left"}
                padding={column.disablePadding ? "none" : undefined}
                sortDirection={
                  order.column === column.path
                    ? (order.direction.toLocaleLowerCase() as "asc" | "desc")
                    : undefined
                }
              >
                {column.unsortable ? (
                  <span
                    style={{
                      textTransform: "uppercase",
                      fontWeight: 800,
                      whiteSpace: "nowrap"
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
                        whiteSpace: "nowrap"
                      }}
                      active={order.column === column.path}
                      direction={
                        order.direction.toLocaleLowerCase() as "asc" | "desc"
                      }
                      onClick={createSortHandler(column.path)}
                    >
                      {column.name}
                    </TableSortLabel>
                  </Tooltip>
                )}
              </TableCell>
            )
          )
        ) : (
          <>
            <TableCell
              key={"name"}
              align={"left"}
              style={{ paddingRight: 0 }}
              sortDirection={
                order.column === "name"
                  ? (order.direction.toLocaleLowerCase() as "asc" | "desc")
                  : undefined
              }
            >
              <Tooltip title="Seřadit" enterDelay={300}>
                <TableSortLabel
                  classes={{
                    root: classes.tableSortLabel
                    // icon: classes.tableSortLabelIcon
                  }}
                  active={order.column === "name"}
                  direction={
                    order.direction.toLocaleLowerCase() as "asc" | "desc"
                  }
                  onClick={createSortHandler("name")}
                >
                  Název
                </TableSortLabel>
              </Tooltip>
            </TableCell>

            <TableCell
              key={"updated"}
              align={"left"}
              style={{ paddingRight: 0, maxWidth: "110px" }}
              sortDirection={
                order.column === "updated"
                  ? (order.direction.toLocaleLowerCase() as "asc" | "desc")
                  : undefined
              }
            >
              <Tooltip title="Seřadit" enterDelay={300}>
                <TableSortLabel
                  style={{
                    textTransform: "uppercase",
                    fontWeight: 800,
                    whiteSpace: "nowrap"
                  }}
                  active={order.column === "updated"}
                  direction={
                    order.direction.toLocaleLowerCase() as "asc" | "desc"
                  }
                  onClick={createSortHandler("updated")}
                >
                  Poslední úprava
                </TableSortLabel>
              </Tooltip>
            </TableCell>
          </>
        )}
      </TableRow>
    </TableHead>
  );
};
