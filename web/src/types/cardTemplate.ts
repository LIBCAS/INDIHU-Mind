export type CardTemplateAttributeType =
  | ""
  | "INTEGER"
  | "DATETIME"
  | "BOOLEAN"
  | "STRING"
  | "DOUBLE";

export interface CardTemplateAttribute {
  id: string;
  name: string;
  type: CardTemplateAttributeType;
  ordinalNumber: number;
  [key: string]: string | number;
}

export interface CardTemplateProps {
  id: string;
  name: string;
  attributeTemplates: CardTemplateAttribute[];
  created: string;
  deleted: string;
  updated: string;
  // owner if template is not common
  owner?: any;
}
