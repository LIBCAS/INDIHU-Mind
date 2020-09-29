import React from "react";
import moment from "moment";
import LaunchIcon from "@material-ui/icons/Launch";

import {
  STATUS_LOADING_COUNT_CHANGE,
  STATUS_ERROR_COUNT_CHANGE,
  STATUS_ERROR_TEXT_SET
} from "../context/reducers/status";
import { api } from "../utils/api";
import { openInNewTab } from ".";
import { AttributeProps } from "../types/attribute";
import { AttributeType } from "../enums";

export const parseAttribute = (
  attribute: AttributeProps,
  onClick: Function = () => {}
) => {
  let parsed;
  switch (attribute.type) {
    case AttributeType.BOOLEAN:
      parsed = attribute.value ? "Ano" : "Ne";
      break;
    case AttributeType.DATE:
      // @ts-ignore
      parsed = moment(attribute.value).format("D. M. YYYY");
      break;
    case AttributeType.DATETIME:
      // @ts-ignore
      parsed = moment(attribute.value).format("D. M. YYYY HH:mm");
      break;
    case AttributeType.URL:
      parsed = (
        <span>
          <span onClick={() => onClick()}>{attribute.value}</span>
          <LaunchIcon
            style={{ marginLeft: 8, marginBottom: -4 }}
            onClick={() => openInNewTab(`${attribute.value}`)}
          />
        </span>
      );
      break;
    default:
      parsed = attribute.value;
  }

  return attribute.type === AttributeType.URL ? (
    parsed
  ) : (
    <span onClick={() => onClick()}>{parsed}</span>
  );
};

export const parseAttributeForApi = (att: AttributeProps) => {
  if (att.type === AttributeType.DATE || att.type === AttributeType.DATETIME) {
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
    .post(`card/set-softdelete`, {
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
