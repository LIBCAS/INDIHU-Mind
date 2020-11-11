import { AttributeType } from "../enums";

export interface CardTemplateAttribute {
  id: string;
  name: string;
  type: AttributeType;
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
