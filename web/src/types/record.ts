import { RecordType } from "../enums";
import { LinkedCardProps } from "./card";
import { FileProps } from "./file";

export interface RecordProps {
  id: string;
  name: string;
  dataFields?: DataFieldsEntity[] | null;
  content?: string;
  deleted?: string;
  linkedCards?: LinkedCardProps[];
  document?: FileProps;
  type?: RecordType;
}

export interface DataFieldsEntity {
  id?: string;
  indicator1?: string;
  indicator2?: string;
  subfields?: SubfieldsEntity[] | null;
  tag: string;
}

export interface SubfieldsEntity {
  code: string;
  data: string;
  id?: string;
}

export interface FormDataField {
  tag: string;
  code: string;
  indicator1?: string;
  indicator2?: string;
  data?: string;
}

export interface MarcEntity {
  czech: string;
  tag: string;
  code?: string;
  indicator1?: string;
  indicator2?: string;
}
