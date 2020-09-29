import * as React from "react";
import classNames from "classnames";
import { map, get, filter } from "lodash";

interface TableToPrintProps {
  items: any[];
  columns: any[];
  classes: any;
}

export const TableToPrint: React.FC<TableToPrintProps> = ({
  items,
  columns,
  classes
}) => {
  const filteredColumns = filter(
    columns,
    ({ printable }) => printable !== false
  );

  return (
    <div className={classes.printTableWrapper}>
      <table className={classes.printTable}>
        <thead className={classes.printTableHead}>
          <tr>
            {map(filteredColumns, (column, key) => (
              <th
                key={key}
                className={classNames(
                  classes.printTableCell,
                  classes.printTableHeadCell
                )}
              >
                {get(column, "name", "")}
              </th>
            ))}
          </tr>
        </thead>
        <tbody style={{ maxWidth: "100%" }}>
          {map(items, (item, key) => (
            <tr key={key} style={{ maxWidth: "100%" }}>
              {map(filteredColumns, ({ format, path }, index) => (
                <td key={`${path}-${index}`} className={classes.printTableCell}>
                  {format ? format(item) : get(item, path, "")}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};
