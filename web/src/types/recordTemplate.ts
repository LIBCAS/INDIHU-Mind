export interface RecordTemplateProps {
  created: string;
  deleted: string;
  fields?: (FieldsEntity)[] | null;
  id: string;
  name: string;
  pattern: string;
  updated: string;
}
export interface FieldsEntity {
  code: string;
  customizations?: (string)[] | null;
  data?: (string)[] | null;
  id: string;
  tag: string;
}
