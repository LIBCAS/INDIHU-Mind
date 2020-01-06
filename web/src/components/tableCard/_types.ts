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
  baseUrl: string;
  // JSON object to filter data in POST request
  query: any;
  columns: ColumnProps[];
  setSelectedRow: Function;
  selectedRow: any;
  handleDelete: (id: string, afterEdit: Function) => void;
  Menu: any;
}

export interface DataProps {
  count: number;
  items: any[];
}
