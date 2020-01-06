import moment from "moment";

import {
  STATUS_LOADING_COUNT_CHANGE,
  STATUS_ERROR_COUNT_CHANGE,
  STATUS_ERROR_TEXT_SET
} from "../context/reducers/status";
import { api } from "../utils/api";
import { AttributeProps } from "../types/attribute";

export const parseAttribute = (attribute: AttributeProps) => {
  switch (attribute.type) {
    case "DOUBLE":
    case "STRING":
      return attribute.value;
    case "BOOLEAN":
      return attribute.value ? "Ano" : "Ne";
    case "DATETIME":
      // @ts-ignore
      return moment(attribute.value).format("D. M. YYYY hh:mm");
  }
};

export const parseAttributeForApi = (att: AttributeProps) => {
  if (att.type === "DATETIME") {
    att = {
      ...att,
      // @ts-ignore
      value: att.value instanceof Date ? att.value.toISOString() : att.value
    };
  }
  return att;
};

interface TranslationProps {
  name: string;
  note: string;
  categories: string;
  labels: string;
  [key: string]: string;
}

const translation: TranslationProps = {
  name: "Název",
  note: "Popis",
  categories: "Kategorie",
  labels: "Štítek",
  external_files: "Příloha" // eslint-disable-line @typescript-eslint/camelcase
};

export const translate = (key: string): string => {
  return translation[key] ? translation[key] : key;
};

export const onDeleteCard = (
  id: string,
  dispatch: Function,
  afterDelete: Function
) => {
  dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: 1 });
  api()
    .post(`card/set_softdelete`, {
      json: {
        ids: [id],
        value: true
      }
    })
    .then(() => {
      dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
      dispatch({
        type: STATUS_ERROR_TEXT_SET,
        payload: `Karta byla umístěna do koše`
      });
      dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: -1 });
      afterDelete();
    })
    .catch(() => {
      dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: -1 });
      dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
    });
};

export type FileErrors =
  | "USER_QUOTA_REACHED"
  | "FILE_TOO_BIG"
  | "FILE_EXTENSION_FORBIDDEN";

const fileErrors: Record<FileErrors, string> = {
  USER_QUOTA_REACHED: "Překročen celkový limit na soubory",
  FILE_TOO_BIG: "Soubor je příliš velký",
  FILE_EXTENSION_FORBIDDEN: "Typ souboru je zakázaný"
};

export const translateFileError = (err: FileErrors) => {
  return fileErrors[err];
};
