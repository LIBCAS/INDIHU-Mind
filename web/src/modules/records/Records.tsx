import React, { useState, useRef, useMemo, useCallback } from "react";
import { get } from "lodash";
import moment from "moment";
import classNames from "classnames";

import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { Table } from "../../components/table/Table";
import { ColumnProps } from "../../components/table/_types";
import { RecordDetailContent } from "../recordDetail/RecordDetailContent";
import { Button } from "@material-ui/core";
import { RecordsMenu } from "./RecordsMenu";
import { RecordsForm } from "./RecordsForm";
import { RecordsPdf } from "./RecordsPdf";

const baseUrl = "record";
const query = {};

const columns: ColumnProps[] = [
  {
    id: "1",
    path: "name",
    name: "Název",
    format: (row: any) => {
      return <span style={{ fontWeight: 800 }}>{row.name}</span>;
    }
  },
  {
    id: "2",
    path: "updated",
    name: "Poslední úprava",
    format: (row: any) => {
      return moment(row.updated).format("DD. MM. YYYY");
    }
  }
];

export const Records: React.FC = () => {
  const anchorEl = useRef(null);
  const classesSpacing = useSpacingStyles();
  const [open, setOpen] = useState(false);
  const Toolbar = useCallback(
    (checkboxRows: any) => (
      <>
        <Button
          onClick={() => setOpen(true)}
          color="primary"
          className={classNames(classesSpacing.mlAuto, classesSpacing.mr1)}
          ref={anchorEl}
          variant="outlined"
          disabled={checkboxRows.length === 0}
        >
          Generovat PDF
        </Button>
        <RecordsPdf
          anchorEl={anchorEl}
          open={open}
          setOpen={setOpen}
          checkboxRows={checkboxRows}
        />
      </>
    ),
    [open, anchorEl, setOpen]
  );
  return (
    <>
      <Table
        title="Citace"
        createLabel="Nová citace"
        CreateForm={RecordsForm}
        createModalProps={{ fullSize: true }}
        baseUrl={baseUrl}
        query={query}
        columns={columns}
        Menu={RecordsMenu}
        ComponentDetail={({ selectedRow }: any) => (
          <>
            <RecordDetailContent record={selectedRow} />
          </>
        )}
        Toolbar={({ checkboxRows }) => Toolbar(checkboxRows)}
      />
    </>
  );
};
