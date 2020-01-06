export interface ColumnProps {
  id: string;
  name: string;
  path: string;
  format?: Function;
  unsortable?: boolean;
}

export interface OrderProps {
  column: string;
  direction: "ASC" | "DESC";
}

export interface TableProps {
  title: string;
  createLabel: string;
  CreateForm: any;
  baseUrl: string;
  // JSON object to filter data in POST request
  query: any;
  columns: ColumnProps[];
  Menu: any;
  ComponentDetail: React.FC<{ selectedRow: any }>;
  Toolbar?: React.FC<{ checkboxRows: any[] }>;
}

export interface DataProps {
  count: number;
  items: any[];
}
