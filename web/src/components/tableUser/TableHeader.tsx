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
  checkboxSelectAll: (e: any) => void;
  isCheckboxSelectedAll: () => boolean;
}

export const TableHeader: React.FC<TableHeadProps> = ({
  order,
  onRequestSort,
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
        <>
          <TableCell
            key={"email"}
            align={"left"}
            style={{ paddingRight: 0, width: "300px" }}
            sortDirection={
              order.column === "email"
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
                active={order.column === "email"}
                direction={
                  order.direction.toLocaleLowerCase() as "asc" | "desc"
                }
                onClick={createSortHandler("email")}
              >
                Uživatelské jméno
              </TableSortLabel>
            </Tooltip>
          </TableCell>

          <TableCell
            key={"allowed"}
            align={"left"}
            style={{ paddingRight: 0 }}
            sortDirection={
              order.column === "allowed"
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
                active={order.column === "allowed"}
                direction={
                  order.direction.toLocaleLowerCase() as "asc" | "desc"
                }
                onClick={createSortHandler("allowed")}
              >
                Stav registrace
              </TableSortLabel>
            </Tooltip>
          </TableCell>
        </>
      </TableRow>
    </TableHead>
  );
};
