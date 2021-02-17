import React from "react";
import { ColumnType, Table } from "../../components/table";
import { TemplateActions } from "./TemplateActions";
import { TemplatesForm } from "./TemplatesForm";
import { TemplatesItem } from "./TemplatesItem";

const columns = [
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

export const Templates: React.FC = () => {
  return (
    <Table
      title="Šablony"
      createLabel="Nová šablona"
      Form={TemplatesForm}
      onSubmitFormRefresh={true}
      columns={columns}
      baseUrl="card/template/all"
      deleteUrl="card/template"
      requestType="GET"
      enableRowActions={true}
      enableRowClick={false}
      enableGroupDelete={true}
      enableSort={false}
      TableActionsComponent={(props) => <TemplateActions {...props} />}
      parametrized={false}
      TileComponent={TemplatesItem}
      createModalProps={{ fullSize: true, overflowVisible: true }}
      onRefreshLoadData={true}
    />
  );
};

export { Templates as default };
