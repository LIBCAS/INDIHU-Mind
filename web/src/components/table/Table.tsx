import { Button, Typography } from "@material-ui/core";
import IconButton from "@material-ui/core/IconButton";
import { Theme } from "@material-ui/core/styles/createMuiTheme";
import Tooltip from "@material-ui/core/Tooltip";
import ViewHeadline from "@material-ui/icons/ViewHeadline";
import ViewModule from "@material-ui/icons/ViewModule";
import { useTheme } from "@material-ui/styles";
import classNames from "classnames";
import { isEqual } from "lodash";
import React, { useCallback, useEffect, useRef, useState } from "react";
import { RouteComponentProps, withRouter } from "react-router-dom";
import { Modal } from "../../components/portal/Modal";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import * as store from "../../utils/store";
import { Loader } from "../loader/Loader";
import { MessageSnackbar } from "../messages/MessageSnackbar";
import { Search } from "./Search";
import { TableComponent } from "./TableComponent";
import { TilesComponent } from "./TilesComponent";
import { FormType, Order } from "./_enums";
import { useStyles } from "./_styles";
import { DataProps, Params, TableProps } from "./_types";
import { getData } from "./_utils";

enum ViewType {
  TILE = "TILE",
  TABLE = "TABLE",
}

const TableView: React.FC<TableProps & RouteComponentProps> = ({
  name = "",
  title,
  createLabel,
  Form = () => <></>,
  FormModal,
  baseUrl,
  filter = [],
  history,
  createModalProps,
  parametrized = true,
  enableSearch = false,
  onCreate,
  TileComponent,
  requestType,
  getItems,
  onSubmitFormRefresh = false,
  onRefreshLoadData = false,
  ...props
}) => {
  const classes = useStyles();
  const classesLayout = useLayoutStyles();
  const classesSpacing = useSpacingStyles();

  const theme: Theme = useTheme();

  const buttonRef = useRef(null);

  const searchValue = new URLSearchParams(window.location.search).get("search");

  // show modal with create form
  const [showForm, setShowForm] = useState<FormType | boolean>(false);

  // currently selected row
  const [selectedRow, setSelectedRow] = useState<any>(undefined);

  // checkboxed items
  const [checkboxRows, setCheckboxRows] = useState<any[]>([]);

  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<boolean>(false);

  const storePath = `table-${name}`;
  const storePathView = `${storePath}-view`;

  const storedView = store.get(storePathView);

  const [view, setView] = useState<ViewType>(
    storedView === ViewType.TILE || storedView === ViewType.TABLE
      ? storedView
      : ViewType.TABLE
  );

  const changeView = (value: ViewType) => {
    store.set(storePathView, value);
    setView(value);
  };

  const [searchText, setSearchText] = useState(
    searchValue ? decodeURI(searchValue) : ""
  );

  const [searchKey, setSearchKey] = useState(false);

  const [params, setParams] = useState<Params>({
    page: 0,
    pageSize: 10,
    filter,
    order: Order.DESC,
  });

  const updateParams = useCallback(
    (newParams: any) => setParams({ ...params, ...newParams }),
    [params]
  );

  const [data, setData] = useState<DataProps>({ count: 0, items: [] });

  // load data from api with changed request
  const loadData = useCallback(async () => {
    setLoading(true);
    const items = await (getItems && searchText
      ? getItems(params, searchText)
      : getData(
          params,
          `${baseUrl}${parametrized ? "/parametrized" : ""}`,
          requestType
        ));
    if (items) {
      setData(items);
    }
    setLoading(false);
  }, [params, searchText, baseUrl, getItems, parametrized, requestType]);

  const refresh = () => {
    setSelectedRow(undefined);
    setCheckboxRows([]);
    updateParams({ page: 0 });
    setSearchText("");
    setSearchKey(!searchKey);
    if (onRefreshLoadData) {
      loadData();
    }
  };

  useEffect(() => {
    if (!isEqual(filter, params.filter)) {
      updateParams({ filter });
    }
  }, [filter, params.filter, updateParams]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const navigateToDetail = (row: any) => history.push(`/${baseUrl}/${row.id}`);

  const isTable = view === ViewType.TABLE;
  const Icon = isTable ? ViewModule : ViewHeadline;
  const Component = isTable || !TileComponent ? TableComponent : TilesComponent;

  return (
    <>
      <Loader loading={loading} className={classes.loader} />
      {error && (
        <MessageSnackbar
          message="Nepodařilo se načíst položky."
          setVisible={setError}
        />
      )}
      {FormModal ? (
        <FormModal
          open={!!showForm}
          setOpen={setShowForm}
          refresh={refresh}
          buttonRef={buttonRef}
        />
      ) : (
        <Modal
          {...createModalProps}
          open={!!showForm}
          setOpen={setShowForm}
          content={
            <Form
              setShowModal={setShowForm}
              item={showForm === FormType.EDIT ? selectedRow : undefined}
              {...(onSubmitFormRefresh ? { refresh: refresh } : null)}
            />
          }
        />
      )}
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
        {typeof title === "string" ? (
          <Typography variant="h5">{title}</Typography>
        ) : (
          title
        )}
        {enableSearch ? (
          <Search
            key={`${searchKey}`}
            searchText={searchText}
            onChange={(text) => setSearchText(text)}
          />
        ) : (
          <></>
        )}
        <div
          className={classNames(
            classesLayout.flex,
            classesLayout.alignCenter,
            classes.createToolbar
          )}
        >
          {TileComponent && (
            <Tooltip title={isTable ? "Dlaždice" : "Tabulka"}>
              <IconButton
                style={{ color: theme.blackIconColor }}
                onClick={() => {
                  changeView(isTable ? ViewType.TILE : ViewType.TABLE);
                }}
              >
                <Icon fontSize="large" color="inherit" />
              </IconButton>
            </Tooltip>
          )}
          <Button
            ref={buttonRef}
            className={classes.createButton}
            variant="contained"
            color="primary"
            onClick={() =>
              onCreate ? onCreate() : setShowForm(FormType.CREATE)
            }
          >
            {createLabel}
          </Button>
        </div>
      </div>
      <Component
        {...{
          ...props,
          ...data,
          baseUrl,
          params,
          updateParams,
          checkboxRows,
          setCheckboxRows,
          selectedRow,
          setSelectedRow,
          loading,
          setLoading,
          refresh,
          navigateToDetail,
          setShowForm,
          TileComponent,
        }}
      />
    </>
  );
};

export const Table = withRouter(TableView);
