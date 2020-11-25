import { RecordProps } from "./record";
import { LabelProps } from "./label";
import { CategoryProps } from "./category";
import { AttributeProps } from "./attribute";
import { FileProps } from "./file";

export interface LinkedCardProps {
  id: string;
  name: string;
  rawNote: string;
}

export interface CardComment {
  id: string;
  ordinalNumber?: number;
  text: string;
  created?: string;
  updated?: string;
}

export interface CardProps {
  id: string;
  pid: number;
  name: string;
  rawNote: string | null;
  structuredNote: any | null;
  note: string;
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
  comments: CardComment[];
  [key: string]:
    | number
    | null
    | string
    | LabelProps[]
    | CategoryProps[]
    | AttributeProps[]
    | LinkedCardProps[]
    | FileProps[]
    | RecordProps[]
    | CardComment[];
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
