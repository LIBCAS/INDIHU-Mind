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
  columns: ColumnProps[];
  setSelectedRow: Function;
  selectedRow: any;
}

export interface DataProps {
  count: number;
  items: any[];
}
