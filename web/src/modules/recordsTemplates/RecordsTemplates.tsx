import React from "react";
import moment from "moment";

import { RecordsTemplatesForm } from "./RecordsTemplatesForm";
import { Modal } from "../../components/portal/Modal";

import { Table } from "../../components/table/Table";
import { ColumnProps } from "../../components/table/_types";
import { RecordTemplateDetailContent } from "../recordTemplateDetail/RecordTemplateDetailContent";

const baseUrl = "template";
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

export const RecordsTemplates: React.FC = () => {
  return (
    <>
      <Table
        title="Citační šablony"
        createLabel="Nová citační šablona"
        CreateForm={RecordsTemplatesForm}
        createModalProps={{ fullSize: true }}
        baseUrl={baseUrl}
        query={query}
        columns={columns}
        Menu={({ selectedRow, showModal, setShowModal, afterEdit }: any) => (
          <Modal
            open={showModal}
            setOpen={setShowModal}
            content={
              <RecordsTemplatesForm
                setShowModal={setShowModal}
                recordTemplate={selectedRow}
                afterEdit={afterEdit}
              />
            }
            fullSize={true}
          />
        )}
        ComponentDetail={({ selectedRow }: any) => (
          <>
            <RecordTemplateDetailContent recordTemplate={selectedRow} />
          </>
        )}
      />
    </>
  );
};
