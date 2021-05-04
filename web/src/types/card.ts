import { AttributeProps } from "./attribute";
import { CategoryProps } from "./category";
import { FileProps } from "./file";
import { LabelProps } from "./label";
import { RecordProps } from "./record";

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

interface MinimalCardProps {
  created: string;
  deleted: string;
  id: string;
  name: string;
  pid: number;
  rawNote: string | null;
  status: "AVAILABLE" | "TRASHED";
  updated: string;
}

export interface CardProps extends MinimalCardProps {
  structuredNote: any | null;
  note: string;
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
  card: MinimalCardProps;
  created: string;
  updated: string;
  deleted: string;
  attributes: AttributeProps[];
  // linkedCards: LinkedCardProps[];
  // linkingCards: LinkedCardProps[];
  // documents: FileProps[];
  lastVersion: boolean;
  [key: string]:
    | MinimalCardProps
    | null
    | string
    | LabelProps[]
    | CategoryProps[]
    | AttributeProps[]
    | LinkedCardProps[]
    | FileProps[]
    | boolean;
}
