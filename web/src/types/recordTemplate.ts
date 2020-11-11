export interface RecordTemplateProps {
  created?: string;
  deleted?: string;
  fields?: FieldsEntity[] | null;
  id: string;
  name: string;
  pattern: string;
  updated?: string;
}
export interface FieldsEntity {
  type: string;
  tag?: string;
  code?: string;
  indicator1?: string;
  indicator2?: string;
  customizations?: string[] | null;
}
