import React from "react";

import { TemplatesForm } from "./TemplatesForm";
import { TemplatesItem } from "./TemplatesItem";
import { Table, ColumnType } from "../../components/table";
import { TemplateActions } from "./TemplateActions";

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
    />
  );
};

export { Templates as default };
