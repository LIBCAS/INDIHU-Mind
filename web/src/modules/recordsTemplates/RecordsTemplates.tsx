import React from "react";

import { RecordsTemplatesForm } from "./RecordsTemplatesForm";

import { Table, Column, ColumnType } from "../../components/table";
import { RecordTemplateDetailContent } from "../recordTemplateDetail/RecordTemplateDetailContent";

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

export const RecordsTemplates: React.FC = () => (
  <Table
    title="Citační šablony"
    createLabel="Nová citační šablona"
    Form={RecordsTemplatesForm}
    createModalProps={{ fullSize: true }}
    baseUrl="template"
    columns={columns}
    enableRowActions={true}
    enableGroupDelete={true}
    ComponentDetail={RecordTemplateDetailContent}
  />
);
