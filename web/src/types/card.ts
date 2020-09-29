import { RecordProps } from "./record";
import { LabelProps } from "./label";
import { CategoryProps } from "./category";
import { AttributeProps } from "./attribute";
import { FileProps } from "./file";

export interface LinkedCardProps {
  id: string;
  name: string;
  note: string;
}

export interface CardProps {
  id: string;
  pid: number;
  name: string;
  note: string | null;
  created: string;
  updated: string;
  deleted: string;
  labels: LabelProps[];
  categories: CategoryProps[];
  attributes: AttributeProps[];
  linkedCards: LinkedCardProps[];
  linkingCards: LinkedCardProps[];
  documents: FileProps[];
  records: RecordProps[];
  [key: string]:
    | number
    | null
    | string
    | LabelProps[]
    | CategoryProps[]
    | AttributeProps[]
    | LinkedCardProps[]
    | FileProps[]
    | RecordProps[];
}

export interface CardContentProps {
  id: string;
  card: CardProps;
  created: string;
  updated: string;
  deleted: string;
  attributes: AttributeProps[];
  // linkedCards: LinkedCardProps[];
  // linkingCards: LinkedCardProps[];
  // documents: FileProps[];
  lastVersion: boolean;
  [key: string]:
    | CardProps
    | null
    | string
    | LabelProps[]
    | CategoryProps[]
    | AttributeProps[]
    | LinkedCardProps[]
    | FileProps[]
    | boolean;
}
