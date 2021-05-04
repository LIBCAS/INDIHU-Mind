import { ComponentClass } from "react";
import { ColumnType, FormType, Order } from "./_enums";

export interface Column {
  name: string;
  field: string;
  type?: ColumnType;
  format?: Function;
  unsortable?: boolean;
  bold?: boolean;
  enum?: any;
  printable?: boolean;
}

export interface Filter {
  field: string;
  operation: string;
  value: string | number;
}

export interface Params {
  page: number;
  pageSize: number;
  order: Order;
  sort?: string;
  filter?: Filter[];
}

export type TGroupActionsComponent = (props: {
  selectedRow: any;
  formikBag: any;
}) => JSX.Element;

export type GroupEditMapper = (row: any, values: any) => any;

export type GroupEditCallback = (
  checkboxRows: any[],
  values: any
) => Promise<any>;

export type GroupDeleteCallback = (checkboxRows: any[]) => Promise<any>;

export type TileComponent = React.FC<{ item: any; refresh: Function }>;

interface FormProps {
  item?: any;
  setShowModal: Function;
}

interface TablePropsBase {
  baseUrl: string;
  redirectOnEdit?: boolean;
}

interface TableActionsPropsBase extends TablePropsBase {
  setShowForm: (x: FormType | boolean) => void;
}

export interface TableActionsProps extends TableActionsPropsBase {
  selectRow: (event: any, row: any) => void;
  row: any;
  handleDelete: Function;
  enableOpenInNewTab: boolean;
}

interface ComponentBase {
  items: any[];
  count: number;
  params: Params;
  updateParams: (newParams: any) => void;
}

interface TableComponentPropsBase {
  filter?: Filter[];
  columns: Column[];
  Form?: React.FC<FormProps> | ComponentClass<any>;
  FormModal?: React.FC<any>;
  Toolbar?: React.FC<{ checkboxRows: any[] }>;
  ComponentDetail?: React.FC<{ item: any }>;
  enableRowClick?: boolean;
  enableRowActions?: boolean;
  enableGroupEdit?: boolean;
  enableGroupDelete?: boolean;
  enableCardsExports?: boolean;
  enablePrint?: boolean;
  enableSort?: boolean;
  enableOpenInNewTab?: boolean;
  TableActionsComponent?: (props: {
    item: any;
    refresh: Function;
  }) => JSX.Element;
  GroupActionsComponent?: TGroupActionsComponent;
  groupEditMapper?: GroupEditMapper;
  onGroupEdit?: GroupEditCallback;
  onGroupDelete?: GroupDeleteCallback;
  onDelete?: (id: string, refresh: Function) => any;
  onSubmitFormRefresh?: boolean;
  deleteUrl?: string;
}

interface TilesComponentPropsBase {
  TileComponent?: TileComponent;
}

export interface TableComponentProps
  extends TableActionsPropsBase,
    TableComponentPropsBase,
    ComponentBase {
  checkboxRows: any[];
  setCheckboxRows: React.Dispatch<React.SetStateAction<any[]>>;
  selectedRow: any;
  setSelectedRow: Function;
  navigateToDetail: Function;
  refresh: Function;
  loading: boolean;
  setLoading: (flag: boolean) => void;
}

export interface TilesComponentProps
  extends TilesComponentPropsBase,
    ComponentBase {
  loading: boolean;
  refresh: Function;
}

export interface TableProps
  extends TableComponentPropsBase,
    TilesComponentPropsBase,
    TablePropsBase {
  name?: string;
  title: string | JSX.Element;
  createLabel: string;
  createModalProps?: any;
  parametrized?: boolean;
  enableSearch?: boolean;
  getItems?: (params: Params, searchText: string) => Promise<DataProps>;
  onCreate?: Function;
  requestType?: string;
  onRefreshLoadData?: boolean;
}

export interface DataProps {
  count: number;
  items: any[];
}
