import LaunchIcon from "@material-ui/icons/Launch";
import React from "react";
import { formatDate, formatDateTime, openInNewTab } from ".";
import { GPSPicker } from "../components/gpsPicker";
import {
  STATUS_ERROR_COUNT_CHANGE,
  STATUS_ERROR_TEXT_SET,
  STATUS_LOADING_COUNT_CHANGE,
} from "../context/reducers/status";
import { AttributeType } from "../enums";
import { AttributeProps } from "../types/attribute";
import { api } from "../utils/api";

export const parseAttribute = (
  attribute: AttributeProps,
  onClick: Function = () => {}
) => {
  const { value, type } = attribute;
  let parsed;
  switch (type) {
    case AttributeType.BOOLEAN:
      parsed = value ? "Ano" : "Ne";
      break;
    case AttributeType.DATE:
      parsed = formatDate(value);
      break;
    case AttributeType.DATETIME:
      parsed = formatDateTime(value);
      break;
    case AttributeType.URL:
      parsed = (
        <span>
          <span onClick={() => onClick()}>{value}</span>
          <LaunchIcon
            style={{ marginLeft: 8, marginBottom: -4 }}
            onClick={() => openInNewTab(`${value}`)}
          />
        </span>
      );
      break;
    case AttributeType.GEOLOCATION:
      parsed =
        typeof value === "string" ? (
          <div onClick={() => onClick()}>
            <GPSPicker {...{ value, disabled: true }} />
          </div>
        ) : (
          ""
        );
      break;
    default:
      parsed = value;
  }

  return type === AttributeType.URL ||
    (type === AttributeType.GEOLOCATION && typeof value === "string") ? (
    parsed
  ) : (
    <span style={{ whiteSpace: "pre" }} onClick={() => onClick()}>
      {parsed}
    </span>
  );
};

export const parseAttributeForApi = (att: AttributeProps) => {
  if (att.type === AttributeType.DATE || att.type === AttributeType.DATETIME) {
    att = {
      ...att,
      // @ts-ignore
      value: att.value instanceof Date ? att.value.toISOString() : att.value,
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
  external_files: "Příloha", // eslint-disable-line @typescript-eslint/camelcase
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
    .post(`card/status`, {
      json: {
        ids: [id],
      },
    })
    .then(() => {
      dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
      dispatch({
        type: STATUS_ERROR_TEXT_SET,
        payload: `Karta byla umístěna do koše`,
      });
      dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: -1 });
      afterDelete();
    })
    .catch(() => {
      dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: -1 });
      dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
    });
};
