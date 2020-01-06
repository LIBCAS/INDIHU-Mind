import { LinkedCardProps } from "./card";

export interface RecordProps {
  id: string;
  name: string;
  leader: string;
  dataFields?: (DataFieldsEntity)[] | null;
  deleted?: string;
  linkedCards?: LinkedCardProps[];
}
export interface DataFieldsEntity {
  tag: string;
  indicator1: string;
  indicator2: string;
  subfields?: (SubfieldsEntity)[] | null;
}
export interface SubfieldsEntity {
  code: string;
  data: string;
}
