import React from "react";
import { isEmpty } from "lodash";

import { Print } from "./Print";
import { TableToPrint } from "./TableToPrint";

interface PrintTableProps {
  items: any[];
  columns: any[];
}

export const PrintTable: React.FC<PrintTableProps> = props => (
  <Print
    ComponentToPrint={TableToPrint}
    componentToPrintProps={props}
    disabled={isEmpty(props.items)}
  />
);
