import uuid from "uuid/v4";
import { get } from "lodash";

import {
  STATUS_ERROR_TEXT_SET,
  STATUS_ERROR_COUNT_CHANGE,
  STATUS_LOADING_COUNT_CHANGE
} from "./../../context/reducers/status";
import { RecordTemplateProps } from "./../../types/recordTemplate";
import { api } from "./../../utils/api";

export const onSubmitRecordTemplate = (
  values: RecordTemplateProps,
  setShowModal: Function,
  setError: Function,
  setLoading: Function,
  recordTemplateGet: Function,
  dispatch: any,
  record?: RecordTemplateProps
) => {
  let id = uuid();
  if (record) {
    id = record.id;
  }
  const body = values;
  api()
    .put(`template/${id}`, { json: body })
    .json<any[]>()
    .then(() => {
      dispatch({
        type: STATUS_ERROR_TEXT_SET,
        payload: record
          ? `Šablona ${values.name} byla úspěšně změněna`
          : `Šablona ${values.name} byla úspěšně vytvořena`
      });
      dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
      setLoading(false);
      setShowModal(false);
      setError(false);
      recordTemplateGet(dispatch);
    })
    .catch(() => {
      setLoading(false);
      setError(true);
    });
};

export const onDeleteRecordTemplate = (
  recordTemplateId: string,
  recordTemplateGet: Function,
  dispatch: Function
) => {
  dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: 1 });
  api()
    .delete(`template/${recordTemplateId}`)
    .then(() => {
      dispatch({
        type: STATUS_ERROR_TEXT_SET,
        payload: "Citační šablona byla úspěšně odstraněna"
      });
      dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
      dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: -1 });
      recordTemplateGet(dispatch);
    })
    .catch(() => {
      dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: -1 });
      dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
    });
};

function indexes(source: string, find: string) {
  if (!source) {
    return [];
  }
  let result = [];

  for (let i = 0; i < source.length; ++i) {
    if (source.substring(i, i + find.length) == find) {
      result.push(i);
    }
  }
  return result;
}

export const parseTemplate = (recordTemplate: RecordTemplateProps) => {
  let counts: any = {};
  let cardsInit: any[] = [];
  if (recordTemplate.fields) {
    recordTemplate.fields.forEach(f => {
      const { tag, code, customizations } = f;
      counts[tag] = get(counts, tag, -1) + 1;
      cardsInit.push({
        id: tag,
        count: counts[tag],
        text: tag,
        code,
        customizations
      });
    });
  }

  const { pattern } = recordTemplate;
  const text = pattern.split("${?}");
  let count = 0;
  let cardIndex = -1;
  let isPrevTag = false;
  let isPrevText = false;
  let isFirst = true;
  text.forEach((t, i) => {
    if (t === "") {
      isPrevTag = true;
      cardIndex += 1;
      isPrevText = false;
    } else {
      if (isFirst && isPrevTag) {
        cardIndex -= 1;
      }
      isFirst = false;
      if (isPrevText) {
        cardIndex += 1;
      }
      if (isPrevTag) {
        cardIndex += 1;
      }
      isPrevTag = false;
      isPrevText = true;
      cardIndex += 1;
      cardsInit.splice(cardIndex, 0, {
        id: "customizations",
        text: t,
        count
      });
      count += 1;
    }
  });
  let initValuesParsed: any = {};
  cardsInit.forEach(c => {
    if (c.id === "customizations") {
      initValuesParsed[c.id + c.count] = c.text;
    } else {
      initValuesParsed[c.id + c.count + "code"] = c.code;
      initValuesParsed[c.id + c.count + "customizations"] = c.customizations;
    }
  });
  initValuesParsed.name = recordTemplate.name;
  return {
    cardsInit,
    initValuesParsed
  };
};
