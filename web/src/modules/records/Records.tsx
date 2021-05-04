import React, { useCallback, useContext, useEffect } from "react";
import { FileFromTemplateGenerator } from "../../components/file/FileFromTemplateGenerator";
import { Column, ColumnType, Table } from "../../components/table";
import { recordTemplateGet } from "../../context/actions/recordTemplate";
import { GlobalContext } from "../../context/Context";
import { RecordDetailContent } from "../recordDetail/RecordDetailContent";
import { RecordsForm } from "./RecordsForm";

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
  const context: any = useContext(GlobalContext);

  const dispatch: Function = context.dispatch;
  const Toolbar = useCallback(
    (checkboxRows: any) => (
      <FileFromTemplateGenerator
        variant="citations"
        checkboxRows={checkboxRows}
      />
    ),
    []
  );

  useEffect(() => {
    recordTemplateGet(dispatch);
  }, [dispatch]);

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
