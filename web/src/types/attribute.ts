import { AttributeType } from "../enums";

export interface AttributeProps {
  id: string;
  name: string;
  type: AttributeType;
  value: undefined | string | number | boolean | Date;
  ordinalNumber: number;
  [key: string]: number | string | undefined | boolean | Date;
}
