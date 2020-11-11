import React from "react";
import { isEmpty } from "lodash";

import { Print } from "./Print";
import { TableToPrint } from "./TableToPrint";
import { Column } from "../table";

interface PrintTableProps {
  items: any[];
  columns: Column[];
}

export const PrintTable: React.FC<PrintTableProps> = (props) => (
  <Print
    ComponentToPrint={TableToPrint}
    componentToPrintProps={props}
    disabled={isEmpty(props.items)}
  />
);
