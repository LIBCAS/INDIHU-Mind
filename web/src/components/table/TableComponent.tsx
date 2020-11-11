import React, { useContext } from "react";
import MaterialTable from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TablePagination from "@material-ui/core/TablePagination";
import TableRow from "@material-ui/core/TableRow";
import Paper from "@material-ui/core/Paper";
import classNames from "classnames";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import { Theme } from "@material-ui/core/styles/createMuiTheme";
import { useTheme } from "@material-ui/styles";
import { Grid, Typography, Button } from "@material-ui/core";
import Delete from "@material-ui/icons/Delete";

import { GlobalContext } from "../../context/Context";
import { ConditionalWrapper } from "../conditionalWrapper/ConditionalWrapper";
import { api } from "../../utils/api";
import {
  STATUS_ERROR_TEXT_SET,
  STATUS_ERROR_COUNT_CHANGE,
} from "../../context/reducers/status";
import { TableGroupActions } from "./TableGroupActions";
import { TableActions } from "./TableActions";
import { TableCheckbox } from "./TableCheckbox";
import { TableHeader } from "./TableHeader";
import { TableValue } from "./TableValue";
import { Popconfirm } from "../../components/portal/Popconfirm";
import { TableComponentProps } from "./_types";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles } from "./_styles";
import { PrintTable } from "../print/PrintTable";
import { pageSizeOptions } from "./_enums";

export const TableComponent: React.FC<TableComponentProps> = ({
  baseUrl,
  checkboxRows,
  setCheckboxRows,
  selectedRow,
  setSelectedRow,
  refresh,
  enableRowClick = true,
  enableRowActions = false,
  enableGroupEdit = false,
  enableGroupDelete = false,
  redirectOnEdit = false,
  enablePrint = false,
  enableSort = true,
  enableOpenInNewTab = false,
  TableActionsComponent,
  GroupActionsComponent = () => <></>,
  groupEditMapper = (r, v) => ({ ...r, ...v }),
  onGroupEdit,
  onGroupDelete,
  columns,
  ComponentDetail,
  setLoading,
  items,
  count,
  navigateToDetail,
  params,
  updateParams,
  setShowForm,
  Toolbar,
  onDelete,
  deleteUrl,
}) => {
  const classes = useStyles();
  const classesLayout = useLayoutStyles();
  const classesSpacing = useSpacingStyles();

  const theme: Theme = useTheme();

  const context: any = useContext(GlobalContext);

  const dispatch: Function = context.dispatch;

  const matches1400 = useMediaQuery("(min-width:1400px)");

  const matchesMd = useMediaQuery(theme.breakpoints.up("md"));

  const { page, pageSize, sort, order } = params;

  const isSelected = (id: any) => selectedRow && selectedRow.id === id;

  const isCheckboxSelected = (row: any) =>
    checkboxRows.findIndex((c) => c.id === row.id) !== -1;

  const isCheckboxSelectedAll = () =>
    checkboxRows.length === items.length && items.length !== 0;

  const checkRow = (row: any) => {
    if (isCheckboxSelected(row)) {
      setCheckboxRows((prev) => prev.filter((c) => row.id !== c.id));
    } else {
      setCheckboxRows((prev) => [...prev, row]);
    }
  };

  const checkboxSelect = (e: any, row: any) => {
    e.stopPropagation();
    checkRow(row);
  };

  const checkboxSelectAll = () => {
    if (checkboxRows.length > 0) {
      setCheckboxRows([]);
    } else {
      setCheckboxRows(items);
    }
  };

  const handleClick = (row: any) => {
    if (enableRowClick) {
      if (!matches1400) {
        navigateToDetail(row);
      } else {
        setSelectedRow((prev: any) =>
          prev && prev.id === row.id ? undefined : row
        );
      }
    } else {
      checkRow(row);
    }
  };

  const selectRow = (e: any, row: any) => {
    e.stopPropagation();
    setSelectedRow(row);
  };

  const handleSort = (event: any, sort: string) => {
    updateParams({
      sort,
      order: params.sort === sort && params.order === "DESC" ? "ASC" : "DESC",
    });
  };

  const handleChangePage = (event: any, page: any) => {
    updateParams({ page });
  };

  const handleChangeRowsPerPage = (event: any) => {
    updateParams({ page: 0, pageSize: event.target.value });
  };

  const deleteRow = (row: any) => {
    const url = deleteUrl ? deleteUrl : baseUrl;
    if (onDelete) {
      onDelete(row.id, refresh);
    } else {
      const request = api().delete(`${url}/${row.id}`);
      request
        .then(() => {
          refresh();
          setLoading(false);
          dispatch({
            type: STATUS_ERROR_TEXT_SET,
            payload: `Smazáno`,
          });
          dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
        })
        .catch(() => {
          dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
          setLoading(false);
        });
    }
  };

  return (
    <Grid container spacing={0}>
      <Grid
        item
        style={{
          flexGrow: 1,
          maxWidth: "100%",
          width: selectedRow ? (matchesMd ? "55%" : "auto") : "auto",
        }}
      >
        <div className={classes.toolbar}>
          <TableGroupActions
            {...{
              ...(deleteUrl ? { baseUrl: deleteUrl } : { baseUrl }),
              checkboxRows,
              selectedRow,
              refresh,
              enableGroupEdit,
              enableGroupDelete,
              GroupActionsComponent,
              groupEditMapper,
              onGroupEdit,
              onGroupDelete,
            }}
          />
          {enablePrint && (
            <div className={classesSpacing.mr1}>
              <PrintTable items={checkboxRows} columns={columns} />
            </div>
          )}
          {Toolbar && <Toolbar checkboxRows={checkboxRows} />}
        </div>
        <Paper className={classes.root}>
          <div className={classes.tableWrapper}>
            <MaterialTable
              className={classes.table}
              aria-labelledby="tableTitle"
            >
              <TableHeader
                enableSort={enableSort}
                columns={columns}
                order={order}
                sort={sort}
                onRequestSort={handleSort}
                checkboxSelectAll={checkboxSelectAll}
                isCheckboxSelectedAll={isCheckboxSelectedAll}
              />
              <TableBody>
                {items.map((row: any) => {
                  const isRowSelected = isSelected(row.id);
                  return (
                    <TableRow
                      key={row.id}
                      hover
                      onClick={() => handleClick(row)}
                      role="checkbox"
                      aria-checked={isRowSelected}
                      tabIndex={-1}
                      selected={isRowSelected}
                      className={classes.contentRow}
                      onDoubleClick={() =>
                        enableRowClick && navigateToDetail(row)
                      }
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
                      {columns.map((column, i) => {
                        const showRowActions =
                          (enableRowActions || !!TableActionsComponent) &&
                          i === 0;
                        return (
                          <TableCell
                            key={column.field + row.id}
                            align="left"
                            className={classes.tableCell}
                          >
                            <ConditionalWrapper
                              condition={showRowActions}
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
                              <TableValue {...{ row, column }} />
                              {showRowActions ? (
                                TableActionsComponent ? (
                                  <div className={classes.icons}>
                                    <TableActionsComponent
                                      item={row}
                                      refresh={refresh}
                                    />
                                  </div>
                                ) : (
                                  <TableActions
                                    baseUrl={baseUrl}
                                    row={row}
                                    setShowForm={setShowForm}
                                    selectRow={selectRow}
                                    redirectOnEdit={redirectOnEdit}
                                    handleDelete={() => deleteRow(row)}
                                    enableOpenInNewTab={enableOpenInNewTab}
                                  />
                                )
                              ) : (
                                <></>
                              )}
                            </ConditionalWrapper>
                          </TableCell>
                        );
                      })}
                    </TableRow>
                  );
                })}
              </TableBody>
            </MaterialTable>
          </div>
          <TablePagination
            style={{ overflow: "auto" }}
            rowsPerPageOptions={pageSizeOptions}
            component="div"
            count={count}
            labelRowsPerPage="Počet řádků na stránku"
            labelDisplayedRows={({ from, to, count }) =>
              `${from}-${to} z ${count}`
            }
            rowsPerPage={pageSize}
            page={page}
            backIconButtonProps={{
              "aria-label": "Předchozí strana",
            }}
            nextIconButtonProps={{
              "aria-label": "Další strana",
            }}
            onChangePage={handleChangePage}
            onChangeRowsPerPage={handleChangeRowsPerPage}
            classes={{
              toolbar: classes.paginationToolbar,
              selectRoot: classes.paginationSelect,
              caption: classes.paginationCaption,
              actions: classes.paginationActions,
            }}
          />
        </Paper>
      </Grid>
      {ComponentDetail && selectedRow && matches1400 ? (
        <Grid
          item
          style={{
            width: "45%",
            marginTop: "3px",
            paddingLeft: "15px",
          }}
        >
          <Typography variant="h5">{selectedRow.name}</Typography>
          <div className={classNames(classesLayout.flex, classesSpacing.mt1)}>
            <Button
              variant="outlined"
              className={classesSpacing.mb1}
              size="small"
              color="primary"
              onClick={() => navigateToDetail(selectedRow)}
            >
              Přejít na detail
            </Button>
            {enableRowActions && (
              <Popconfirm
                confirmText="Smazat?"
                onConfirmClick={() => deleteRow(selectedRow)}
                Button={
                  <Button
                    variant="outlined"
                    className={classNames(
                      classesSpacing.ml1,
                      classesSpacing.mb1
                    )}
                    size="small"
                    color="secondary"
                  >
                    Smazat{" "}
                    <Delete className={classesSpacing.ml1} fontSize="small" />
                  </Button>
                }
              />
            )}
          </div>
          <ComponentDetail item={selectedRow} />
        </Grid>
      ) : (
        <></>
      )}
    </Grid>
  );
};
