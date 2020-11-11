import { FileType } from "../../enums";

export interface Attachment {
  id: string;
  created?: Date;
  updated?: Date;
  deleted?: Date;
  name: string;
  type: string;
  ordinalNumber: number;
  linkedCards: string[];
  records?: string[];
  // card: {
  //   id: string;
  //   name: string;
  //   note: string;
  //   pid: number;
  // };
  providerType: FileType;
  contentType: string;
  size: number;
  link: string;
}

export interface AttachmentUpdateProps {
  id: string;
  name: string;
  linkedCards: string[];
  records?: string[];
}

export interface AttachmentEditProps {
  id: string;
  name: string;
  linkedCards: { id: string }[];
  records?: { id: string }[];
}
