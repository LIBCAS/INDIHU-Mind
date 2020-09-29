import React from "react";
import moment from "moment";
import { get, pick, compact } from "lodash";

import { ColumnProps } from "../../components/tableCard/_types";

import {
  STATUS_ERROR_TEXT_SET,
  STATUS_ERROR_COUNT_CHANGE,
  STATUS_LOADING_COUNT_CHANGE
} from "../../context/reducers/status";
import { api } from "../../utils/api";
import { RecordProps } from "../../types/record";
import { RecordRequest } from "./RecordsForm";
import { RecordType } from "../../enums";

const url = "record";

export const getRecords = async (text?: string, page = 0, pageSize = 10) => {
  try {
    const response = await api().post(`${url}/parametrized`, {
      json: {
        page,
        pageSize,
        ...(text
          ? { filter: [{ field: "name", operation: "EQ", value: text }] }
          : {})
      }
    });

    return await response.json();
  } catch (e) {
    console.log(e);
    return null;
  }
};

export const onSubmitRecord = (
  values: RecordRequest,
  setShowModal: Function,
  setError: Function,
  setLoading: Function,
  dispatch: any,
  history: any,
  record?: RecordProps,
  afterEdit?: () => void,
  redirect?: boolean,
  onSubmitCallback?: Function
) => {
  const { document, isBrief, type } = values;
  const func = record ? api().put : api().post;
  func(url, {
    json: {
      ...pick(
        values,
        compact([
          "id",
          "name",
          isBrief || type === RecordType.BRIEF ? "content" : "dataFields"
        ])
      ),
      ...(values.id
        ? {}
        : { type: isBrief ? RecordType.BRIEF : RecordType.MARC }),
      ...(document ? { documentId: document.id } : {}),
      linkedCards: (values.linkedCards || []).map(({ id }) => id)
    }
  })
    .json()
    .then((res: any) => {
      const newId = res ? res.id : null;
      dispatch({
        type: STATUS_ERROR_TEXT_SET,
        payload: record
          ? `Citace ${values.name} byla úspěšně změněna`
          : `Citace ${values.name} byla úspěšně vytvořena`
      });
      if (afterEdit) {
        afterEdit();
      }
      dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
      setLoading(false);
      redirect && newId && history.push(`/record/${newId}`);
      setError(false);
      setShowModal(false);
      onSubmitCallback && onSubmitCallback([{ id: newId, name: values.name }]);
    })
    .catch(err => {
      setLoading(false);
      setError(
        get(err, "response.errorType") === "ERR_NAME_ALREADY_EXISTS"
          ? "Citace se zvoleným názvem již existuje."
          : true
      );
    });
};

export const onDeleteRecord = (
  id: string,
  dispatch: Function,
  afterDelete: Function
) => {
  dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: 1 });
  api()
    .delete(`${url}/${id}`)
    .then(() => {
      dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
      dispatch({
        type: STATUS_ERROR_TEXT_SET,
        payload: "Citace byla úspěšně odstraněna"
      });
      dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: -1 });
      afterDelete();
    })
    .catch(() => {
      dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: -1 });
      dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
    });
};

export const columns: ColumnProps[] = [
  {
    id: "1",
    path: "name",
    name: "Název",
    format: (row: any) => {
      return <span style={{ fontWeight: 800 }}>{row.name}</span>;
    }
  },
  {
    id: "2",
    path: "updated",
    name: "Poslední úprava",
    format: (row: any) => {
      return moment(row.updated).format("DD. MM. YYYY");
    }
  }
];
