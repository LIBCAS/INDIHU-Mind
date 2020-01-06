import React from "react";
import moment from "moment";

import { ColumnProps } from "../../components/tableCard/_types";
import { get } from "lodash";
import uuid from "uuid/v4";

import {
  STATUS_ERROR_TEXT_SET,
  STATUS_ERROR_COUNT_CHANGE,
  STATUS_LOADING_COUNT_CHANGE
} from "../../context/reducers/status";
import { api } from "../../utils/api";
import { RecordProps } from "../../types/record";
import {
  RecordRequest,
  DataFieldsEntity,
  SubfieldsEntity
} from "./RecordsForm";

export const onSubmitRecord = (
  values: RecordRequest,
  setShowModal: Function,
  setError: Function,
  setLoading: Function,
  dispatch: any,
  history: any,
  record?: RecordProps,
  afterEdit?: () => void
) => {
  let id = uuid();
  if (record) {
    id = record.id;
  }
  const body = values;
  const { leader } = values;
  api()
    .put(`record/${id}`, {
      json: { ...body, id, leader: leader ? leader : "" }
    })
    .json()
    .then(() => {
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
      history.push(`record/${id}`);
      setError(false);
      setShowModal(false);
    })
    .catch(() => {
      setLoading(false);
      setError(true);
    });
};

export const onDeleteRecord = (
  id: string,
  dispatch: Function,
  afterDelete: Function
) => {
  dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: 1 });
  api()
    .delete(`record/${id}`)
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
