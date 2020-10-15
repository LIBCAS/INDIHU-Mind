import React, { useState, useEffect, useContext } from "react";
import MaterialTable from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TablePagination from "@material-ui/core/TablePagination";
import TableRow from "@material-ui/core/TableRow";
import Paper from "@material-ui/core/Paper";
import { get } from "lodash";

import { GlobalContext, StateProps } from "../../context/Context";
import { usersUpdated } from "../../context/actions/users";
import { UserProps } from "../../types/user";

import { TableGroupEdit } from "./TableGroupEdit";
import { Loader } from "../loader/Loader";
import { TableCheckbox } from "./TableCheckbox";
import { TableHeader } from "./TableHeader";
import { TableProps, OrderProps, DataProps } from "./_types";
import { changeData, changeRequest } from "./_utils";
import { useStyles } from "./_tableStyles";

export const TableUser: React.FC<TableProps> = ({ baseUrl, columns }) => {
  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;
  const state: StateProps = context.state;
  const classes = useStyles();
  // checkboxed cards
  const [checkboxRows, setCheckboxRows] = useState<UserProps[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [request, setRequest] = useState<string>("");
  const [order, setOrder] = useState<OrderProps>({
    column: "",
    direction: "ASC"
  });
  const [data, setData] = useState<DataProps>({ count: 0, items: [] });
  const [page, setPage] = useState<number>(0);
  const [rowsPerPage, setrowsPerPage] = useState<number>(10);
  // change http request - set URL query params to load data from api in the right order & page
  useEffect(() => {
    changeRequest(setRequest, baseUrl, order, page, rowsPerPage);
  }, []);

  useEffect(() => {
    changeRequest(setRequest, baseUrl, order, page, rowsPerPage);
  }, [setRequest, order, page, rowsPerPage, baseUrl]);
  // load data from api with changed request
  const loadData = () => changeData(request, setData, loading, setLoading);
  useEffect(() => {
    loadData();
  }, [request]);

  // load new data if data are modified outside this component
  useEffect(() => {
    if (state.users.updated) {
      loadData();
      usersUpdated(dispatch, false);
    }
  }, [state.users.updated]);

  const handleRequestSort = (event: any, property: string) => {
    const column = property;
    let direction: "ASC" | "DESC" = "DESC";
    if (order.column === column && order.direction === "DESC") {
      direction = "ASC";
    }
    const newOrder = { column, direction };
    setOrder(newOrder);
  };

  const checkboxSelect = (e: any, row: any) => {
    e.stopPropagation();
    if (checkboxRows.findIndex(user => user.id === row.id) !== -1) {
      setCheckboxRows(prev => prev.filter(user => row.id !== user.id));
    } else {
      setCheckboxRows(prev => [...prev, row]);
    }
  };

  const handleChangePage = (event: any, page: any) => {
    setPage(page);
  };

  const handleChangeRowsPerPage = (event: any) => {
    setrowsPerPage(event.target.value);
    setPage(0);
  };

  const isCheckboxSelected = (row: any) =>
    checkboxRows.findIndex(user => user.id === row.id) !== -1;

  const checkboxSelectAll = () => {
    if (checkboxRows.length > 0) {
      setCheckboxRows([]);
    } else {
      setCheckboxRows(data.items);
    }
  };

  const isCheckboxSelectedAll = () =>
    checkboxRows.length === data.items.length && data.items.length !== 0;
  return (
    <>
      <TableGroupEdit
        checkboxRows={checkboxRows}
        loadData={loadData}
        setCheckboxRows={setCheckboxRows}
      />
      <Paper className={classes.root}>
        <Loader loading={loading} local className={classes.loader} />
        <div className={classes.tableWrapper}>
          <MaterialTable className={classes.table} aria-labelledby="tableTitle">
            <TableHeader
              columns={columns}
              order={order}
              onRequestSort={handleRequestSort}
              checkboxSelectAll={checkboxSelectAll}
              isCheckboxSelectedAll={isCheckboxSelectedAll}
            />
            <TableBody>
              {data.items.map((row: any) => {
                const isRowSelected = checkboxRows.some(c => row.id === c.id);
                return (
                  <TableRow
                    key={row.id}
                    hover
                    onClick={event => {
                      checkboxSelect(event, row);
                    }}
                    role="checkbox"
                    aria-checked={isRowSelected}
                    tabIndex={-1}
                    selected={isRowSelected}
                    className={classes.contentRow}
                  >
                    <TableCell
                      onClick={(e: any) => {
                        checkboxSelect(e, row);
                      }}
                      padding="checkbox"
                    >
                      <TableCheckbox checked={isCheckboxSelected(row)} />
                    </TableCell>
                    {columns.map(column => (
                      <TableCell
                        key={row.id + column.id}
                        align="left"
                        className={classes.tableCell}
                        style={{ whiteSpace: "normal", paddingRight: "0" }}
                      >
                        {column.format
                          ? column.format(row)
                          : get(row, column.path, "?")}
                      </TableCell>
                    ))}
                  </TableRow>
                );
              })}
            </TableBody>
          </MaterialTable>
        </div>
        <TablePagination
          style={{ overflow: "auto" }}
          rowsPerPageOptions={[5, 10, 15, 20, 25]}
          component="div"
          count={data.count}
          labelRowsPerPage="Počet řádků na stránku"
          labelDisplayedRows={({ from, to, count }) =>
            `${from}-${to} z ${count}`
          }
          rowsPerPage={rowsPerPage}
          page={page}
          backIconButtonProps={{
            "aria-label": "Předchozí strana"
          }}
          nextIconButtonProps={{
            "aria-label": "Další strana"
          }}
          onChangePage={handleChangePage}
          onChangeRowsPerPage={handleChangeRowsPerPage}
          classes={{
            toolbar: classes.paginationToolbar,
            selectRoot: classes.paginationSelect,
            caption: classes.paginationCaption,
            actions: classes.paginationActions
          }}
        />
      </Paper>
    </>
  );
};
