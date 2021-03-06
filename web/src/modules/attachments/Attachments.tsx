import { get } from "lodash";
import React from "react";
import { Column, ColumnType, Table } from "../../components/table";
import { FileTypeText } from "../../enums";
import { formatBytes } from "../../utils";
import { AttachmentCard } from "./AttachmentCard";
import { AttachmentCardMenu } from "./AttachmentCardMenu";
import { AttachmentsAdd } from "./AttachmentsAdd";
import { Attachment } from "./_types";
import { baseUrl, getFiles } from "./_utils";

const columns: Column[] = [
  {
    field: "name",
    name: "Název",
    bold: true,
  },
  {
    field: "linkedCards",
    name: "Počet karet",
    format: (item: Attachment) => get(item, "linkedCards.length", 0),
  },
  {
    field: "linkedCards",
    name: "Počet citací",
    format: (item: Attachment) => get(item, "records.length", 0),
  },
  {
    field: "type",
    name: "Typ souboru",
  },
  {
    field: "size",
    name: "Velikost",
    format: (item: Attachment) => formatBytes(item.size),
  },
  {
    field: "providerType",
    name: "Původ",
    enum: FileTypeText,
    type: ColumnType.ENUM,
  },
  {
    field: "link",
    name: "Odkaz",
    type: ColumnType.LINK,
  },
];

export const Attachments: React.FC = () => {
  return (
    <Table
      name="documents"
      title="Seznam dokumentů"
      createLabel="Přidat dokument"
      baseUrl={baseUrl}
      enableSearch={true}
      enableRowClick={false}
      enableSort={false}
      columns={columns}
      getItems={(params, text) => getFiles(text, params.page, params.pageSize)}
      TileComponent={AttachmentCard}
      FormModal={AttachmentsAdd}
      TableActionsComponent={(props) => <AttachmentCardMenu {...props} />}
    />
  );
};
