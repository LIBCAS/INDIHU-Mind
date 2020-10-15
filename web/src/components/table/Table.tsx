import React, { useState, useEffect, useCallback } from "react";
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
import { Theme } from "@material-ui/core/styles/createMuiTheme";
import { useTheme } from "@material-ui/styles";
import { Grid, Typography, Button } from "@material-ui/core";

import { ConditionalWrapper } from "../conditionalWrapper/ConditionalWrapper";
import { Modal } from "../../components/portal/Modal";

import { TableGroupEdit } from "./TableGroupEdit";
import { Loader } from "../loader/Loader";
import { TableActions } from "./TableActions";
import { TableCheckbox } from "./TableCheckbox";
import { TableHeader } from "./TableHeader";
import { TableProps, OrderProps, DataProps } from "./_types";
import { changeData, changeFilter } from "./_utils";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles } from "./_tableStyles";

const TableView: React.FC<TableProps & RouteComponentProps> = ({
  title,
  createLabel,
  CreateForm,
  baseUrl,
  query,
  columns,
  Menu,
  history,
  ComponentDetail,
  Toolbar
}) => {
  const classes = useStyles();

  const classesLayout = useLayoutStyles();

  const classesSpacing = useSpacingStyles();

  const theme: Theme = useTheme();

  const matchesMd = useMediaQuery(theme.breakpoints.up("md"));

  // show modal with create form
  const [showForm, setShowForm] = useState(false);

  // currently selected row
  const [selectedRow, setSelectedRow] = useState<any>(undefined);

  // checkboxed items
  const [checkboxRows, setCheckboxRows] = useState<any[]>([]);

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
  const loadData = () => {
    changeData(filter, baseUrl, setData, loading, setLoading);
  };
  // after the item is edited / deleted
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
      history.push(`/${baseUrl}/${row.id}`);
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
    if (checkboxRows.findIndex(c => c.id === row.id) !== -1) {
      setCheckboxRows(prev => prev.filter(c => row.id !== c.id));
    } else {
      setCheckboxRows(prev => [...prev, row]);
    }
  };

  const isCheckboxSelected = (row: any) =>
    checkboxRows.findIndex(c => c.id === row.id) !== -1;

  const checkboxSelectAll = () => {
    if (checkboxRows.length > 0) {
      setCheckboxRows([]);
    } else {
      setCheckboxRows(data.items);
    }
  };

  const isCheckboxSelectedAll = () =>
    checkboxRows.length === data.items.length && data.items.length !== 0;

  const onCancel = () => setSelectedRow(undefined);
  return (
    <>
      <Modal
        open={showForm}
        setOpen={setShowForm}
        content={<CreateForm setShowModal={setShowForm} />}
      />
      <div
        className={classNames(
          classesLayout.flex,
          classesLayout.flexWrap,
          classesLayout.alignCenter,
          classesLayout.spaceBetween,
          classesSpacing.mt1,
          classesSpacing.mb2
        )}
      >
        <Typography variant="h5">{title}</Typography>
        {Toolbar && <Toolbar checkboxRows={checkboxRows} />}
        <Button
          className={classes.createButton}
          variant="contained"
          color="primary"
          onClick={() => setShowForm(true)}
        >
          {createLabel}
        </Button>
      </div>
      <Grid container spacing={0}>
        <Grid
          item
          style={{
            flexGrow: 1,
            maxWidth: "100%",
            width: selectedRow ? (matchesMd ? "55%" : "auto") : "auto"
          }}
        >
          <TableGroupEdit
            baseUrl={baseUrl}
            checkboxRows={checkboxRows}
            selectedRow={selectedRow}
            loadData={loadData}
            setCheckboxRows={setCheckboxRows}
          />
          <Paper className={classes.root}>
            <Loader loading={loading} local className={classes.loader} />
            <div className={classes.tableWrapper}>
              <MaterialTable
                className={classes.table}
                aria-labelledby="tableTitle"
              >
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
                          history.push(`/${baseUrl}/${row.id}`);
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
                                      baseUrl={baseUrl}
                                      row={row}
                                      setMenuOpen={setMenuOpen}
                                      selectRow={selectRow}
                                      loading={loading}
                                      setLoading={setLoading}
                                      setCheckboxRows={setCheckboxRows}
                                      loadData={loadData}
                                    />
                                  )}
                                </ConditionalWrapper>
                              </TableCell>
                            )
                          )
                        ) : (
                          <>
                            <TableCell
                              key={row.id}
                              align="left"
                              className={classes.tableCell}
                              style={{
                                whiteSpace: "normal",
                                paddingRight: "0"
                              }}
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
                                  baseUrl={baseUrl}
                                  row={row}
                                  setMenuOpen={setMenuOpen}
                                  selectRow={selectRow}
                                  loading={loading}
                                  setLoading={setLoading}
                                  setCheckboxRows={setCheckboxRows}
                                  loadData={loadData}
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
            afterEdit={afterEdit}
          />
        </Grid>
        {selectedRow && matches1400 && (
          <Grid
            item
            style={{
              width: "45%",
              marginTop: "3px",
              paddingLeft: "15px"
            }}
          >
            <Typography variant="h5" className={classNames(classesSpacing.mb1)}>
              {selectedRow.name}
            </Typography>
            <div className={classNames(classesLayout.flex)}>
              <Button
                variant="outlined"
                className={classesSpacing.mb1}
                size="small"
                color="primary"
                onClick={() => history.push(`/${baseUrl}/${selectedRow.id}`)}
              >
                Přejít na detail
              </Button>
            </div>
            <ComponentDetail selectedRow={selectedRow} />
          </Grid>
        )}
      </Grid>
    </>
  );
};

export const Table = withRouter(TableView);
