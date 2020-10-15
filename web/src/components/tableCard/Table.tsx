import React, { useState, useEffect } from "react";
import MaterialTable from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TablePagination from "@material-ui/core/TablePagination";
import TableRow from "@material-ui/core/TableRow";
import Paper from "@material-ui/core/Paper";
import { get } from "lodash";
import moment from "moment";
import { withRouter, RouteComponentProps } from "react-router-dom";
import classNames from "classnames";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import Chip from "@material-ui/core/Chip";

import { CardProps } from "../../types/card";
import { ConditionalWrapper } from "../conditionalWrapper/ConditionalWrapper";

import { TableGroupEdit } from "./TableGroupEdit";
import { Loader } from "../loader/Loader";
import { TableActions } from "./TableActions";
import { TableCheckbox } from "./TableCheckbox";
import { TableHeader } from "./TableHeader";
import { TableProps, OrderProps, DataProps } from "./_types";
import { changeData, changeFilter } from "./_utils";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles } from "./_tableStyles";

const TableView: React.FC<TableProps & RouteComponentProps> = ({
  baseUrl,
  query,
  columns,
  selectedRow,
  setSelectedRow,
  handleDelete,
  Menu,
  history
}) => {
  const classes = useStyles();
  const classesLayout = useLayoutStyles();
  // checkboxed cards
  const [checkboxRows, setCheckboxRows] = useState<CardProps[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  // filter for POST request - combined filter, sorting, page, pageSize
  const [filter, setFilter] = useState<any>({});
  const [order, setOrder] = useState<OrderProps>({
    column: "",
    direction: "ASC"
  });
  const [data, setData] = useState<DataProps>({ count: 0, items: [] });
  const [page, setPage] = useState<number>(0);
  const [rowsPerPage, setrowsPerPage] = useState<number>(10);
  // open custom menu to edit row
  const [menuOpen, setMenuOpen] = useState<boolean>(false);
  const matches1400 = useMediaQuery("(min-width:1400px)");
  // change http request - set POST params to load data from api in the right order & page
  useEffect(() => {
    changeFilter(setFilter, query, order, page, rowsPerPage);
  }, []);

  useEffect(() => {
    changeFilter(setFilter, query, order, page, rowsPerPage);
  }, [setFilter, query, order, page, rowsPerPage]);
  // load data from api with changed request
  const loadData = () =>
    changeData(filter, baseUrl, setData, loading, setLoading);
  // after the card is edited / deleted from CardCreateForm
  const afterEdit = () => {
    setSelectedRow(undefined);
    loadData();
  };
  useEffect(() => {
    loadData();
  }, [filter]);

  const handleRequestSort = (event: any, property: string) => {
    const column = property;
    let direction: "ASC" | "DESC" = "DESC";
    if (order.column === column && order.direction === "DESC") {
      direction = "ASC";
    }
    const newOrder = { column, direction };
    setOrder(newOrder);
  };

  const handleClick = (event: any, row: any) => {
    if (!matches1400) {
      history.push(`/card/${row.id}`);
    } else {
      setSelectedRow((prev: any) =>
        prev && prev.id === row.id ? undefined : row
      );
    }
  };

  const selectRow = (e: any, row: any) => {
    e.stopPropagation();
    setSelectedRow(row);
  };

  const handleChangePage = (event: any, page: any) => {
    setPage(page);
  };

  const handleChangeRowsPerPage = (event: any) => {
    setrowsPerPage(event.target.value);
    setPage(0);
  };

  const isSelected = (id: any) => selectedRow && selectedRow.id === id;

  const checkboxSelect = (e: any, row: any) => {
    e.stopPropagation();
    if (checkboxRows.findIndex(card => card.id === row.id) !== -1) {
      setCheckboxRows(prev => prev.filter(card => row.id !== card.id));
    } else {
      setCheckboxRows(prev => [...prev, row]);
    }
  };

  const isCheckboxSelected = (row: any) =>
    checkboxRows.findIndex(card => card.id === row.id) !== -1;

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
        selectedRow={selectedRow}
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
              selectedRow={selectedRow}
              matches1400={matches1400}
              checkboxSelectAll={checkboxSelectAll}
              isCheckboxSelectedAll={isCheckboxSelectedAll}
            />
            <TableBody>
              {data.items.map((row: any) => {
                const isRowSelected = isSelected(row.id);
                return (
                  <TableRow
                    key={row.id}
                    hover
                    onClick={event => {
                      handleClick(event, row);
                    }}
                    role="checkbox"
                    aria-checked={isRowSelected}
                    tabIndex={-1}
                    selected={isRowSelected}
                    className={classes.contentRow}
                    onDoubleClick={() => {
                      history.push(`/card/${row.id}`);
                    }}
                  >
                    <TableCell
                      onClick={(e: any) => {
                        checkboxSelect(e, row);
                      }}
                      padding="checkbox"
                      className={classes.checkbox}
                    >
                      <TableCheckbox checked={isCheckboxSelected(row)} />
                    </TableCell>
                    {matches1400 ? (
                      columns.map(column =>
                        selectedRow && column.path === "note" ? null : (
                          <TableCell
                            key={column.id + row.id}
                            align="left"
                            className={classes.tableCell}
                          >
                            {(column.path === "labels" &&
                              row.labels.map((label: any) => (
                                <Chip
                                  key={label.id}
                                  label={label.name}
                                  style={{
                                    backgroundColor: label.color,
                                    margin: "0 3px"
                                  }}
                                />
                              ))) || (
                              <ConditionalWrapper
                                condition={column.path === "name"}
                                wrap={(children: any) => (
                                  <div
                                    className={classNames(
                                      classesLayout.flex,
                                      classesLayout.alignCenter
                                    )}
                                  >
                                    {children}
                                  </div>
                                )}
                              >
                                {column.format
                                  ? column.format(row)
                                  : get(row, column.path, "?")}
                                {column.path === "name" && (
                                  <TableActions
                                    row={row}
                                    setMenuOpen={setMenuOpen}
                                    afterEdit={afterEdit}
                                    selectRow={selectRow}
                                    handleDelete={handleDelete}
                                    history={history}
                                  />
                                )}
                              </ConditionalWrapper>
                            )}
                          </TableCell>
                        )
                      )
                    ) : (
                      <>
                        <TableCell
                          key={row.id}
                          align="left"
                          className={classes.tableCell}
                          style={{ whiteSpace: "normal", paddingRight: "0" }}
                          colSpan={2}
                        >
                          <div
                            style={{ fontWeight: 800 }}
                            className={classNames(
                              classesLayout.flex,
                              classesLayout.alignCenter
                            )}
                          >
                            {get(row, "name", "?")}
                            <TableActions
                              row={row}
                              setMenuOpen={setMenuOpen}
                              afterEdit={afterEdit}
                              selectRow={selectRow}
                              handleDelete={handleDelete}
                              history={history}
                            />
                          </div>
                          <div>
                            {moment(row.updated).format("DD. MM. YYYY")}
                          </div>
                        </TableCell>
                      </>
                    )}
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
      <Menu
        selectedRow={selectedRow}
        showModal={menuOpen}
        setShowModal={setMenuOpen}
        edit
        afterEdit={afterEdit}
      />
    </>
  );
};

export const Table = withRouter(TableView);
