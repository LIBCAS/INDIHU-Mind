import React, { useState, useRef, useCallback } from "react";

import { Table, Column, ColumnType } from "../../components/table";
import { RecordDetailContent } from "../recordDetail/RecordDetailContent";
import { Button } from "@material-ui/core";
import { RecordsForm } from "./RecordsForm";
import { RecordsPdf } from "./RecordsPdf";

const columns: Column[] = [
  {
    field: "name",
    name: "Název",
    bold: true,
  },
  {
    field: "updated",
    name: "Poslední úprava",
    type: ColumnType.DATE,
  },
];

export const Records: React.FC = () => {
  const anchorEl = useRef(null);
  const [open, setOpen] = useState(false);
  const Toolbar = useCallback(
    (checkboxRows: any) => (
      <>
        <Button
          onClick={() => setOpen(true)}
          ref={anchorEl}
          variant="contained"
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
    <Table
      title="Citace"
      createLabel="Nová citace"
      Form={RecordsForm}
      createModalProps={{ fullSize: true }}
      baseUrl="record"
      columns={columns}
      enableRowActions={true}
      enableGroupDelete={true}
      ComponentDetail={RecordDetailContent}
      Toolbar={({ checkboxRows }) => Toolbar(checkboxRows)}
    />
  );
};
