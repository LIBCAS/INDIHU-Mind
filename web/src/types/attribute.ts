export type AttributeTypeProps =
  | ""
  | "INTEGER"
  | "DATETIME"
  | "BOOLEAN"
  | "STRING"
  | "DOUBLE";

export interface AttributeProps {
  id: string;
  name: string;
  type: AttributeTypeProps;
  value: undefined | string | number | boolean | Date;
  ordinalNumber: number;
  [key: string]: number | string | undefined | boolean | Date;
}
