import React, { useState, useContext } from "react";
import Typography from "@material-ui/core/Typography";
import Button from "@material-ui/core/Button";
import { FormikProps, Form, Field } from "formik";
import classNames from "classnames";
import { ResponsePromise } from "ky";

import { GlobalContext } from "../../context/Context";
import {
  STATUS_ERROR_COUNT_CHANGE,
  STATUS_ERROR_TEXT_SET
} from "../../context/reducers/status";
import { api } from "../../utils/api";
import { notEmpty } from "../../utils/form/validate";
import { CardProps } from "../../types/card";
import { OptionType } from "../form/reactSelect/_reactSelectTypes";
import { Formik } from "../form/Formik";
import { Select } from "../form/Select";

import { Loader } from "../loader/Loader";
import { CardCreateAddCategory } from "../../modules/cardCreate/CardCreateAddCategory";
import { CardCreateAddLabel } from "../../modules/cardCreate/CardCreateAddLabel";

import { getUpdatedCard } from "./_utils";
import { useStyles } from "./_tableStyles";

interface TableGroupEditProps {
  checkboxRows: CardProps[];
  selectedRow: any;
  loadData: Function;
  setCheckboxRows: React.Dispatch<React.SetStateAction<CardProps[]>>;
}

type TypeProps = "edit" | "delete" | "";

const types: { value: TypeProps; label: string }[] = [
  { value: "edit", label: "Upravit" },
  { value: "delete", label: "Odstranit" }
];

export interface FormValues {
  categories: OptionType[];
  labels: OptionType[];
  type: TypeProps;
}

const initialValues = {
  categories: [],
  labels: [],
  type: ""
};

export const TableGroupEdit: React.FC<TableGroupEditProps> = ({
  checkboxRows,
  selectedRow,
  loadData,
  setCheckboxRows
}) => {
  const classes = useStyles();
  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;
  const [loading, setLoading] = useState(false);
  const onSubmit = (values: FormValues, actions: any) => {
    if (loading) return;
    actions.resetForm(initialValues);
    setLoading(true);
    let requests: ResponsePromise[] = [];
    if (values.type === "edit") {
      checkboxRows.forEach(card => {
        const payload = getUpdatedCard(card, values);
        const request = api().put(`card/${card.id}`, { json: payload });
        requests.push(request);
      });
    }
    if (values.type === "delete") {
      const ids = checkboxRows.map(c => c.id);
      const request = api().post(`card/set-softdelete`, {
        json: {
          ids,
          value: true
        }
      });
      requests.push(request);
    }
    Promise.all(requests)
      .then(() => {
        // Reload table data so when user adds categories / labels
        // multiple times, then the previous ones are added too
        setCheckboxRows([]);
        loadData();
        setLoading(false);
        dispatch({
          type: STATUS_ERROR_TEXT_SET,
          payload: values.type === "edit" ? `Změny uloženy` : `Karty smazány`
        });
        dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
      })
      .catch(() => {
        dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
        setLoading(false);
      });
  };
  return (
    <>
      <Loader loading={loading} />

      <Formik
        initialValues={initialValues}
        enableReinitialize
        onSubmit={onSubmit}
        render={(formikBag: FormikProps<FormValues>) => (
          <Form>
            <div
              className={classNames(classes.groupEditWrapper, {
                [classes.groupEditWrapperNoCheckbox]: checkboxRows.length === 0
              })}
            >
              <div className={classes.groupTitleWrapper}>
                <div className={classes.groupTitle}>
                  <Field
                    name="type"
                    component={Select}
                    options={types}
                    placeholder="Hromadné úpravy"
                  />
                </div>
                <div className={classes.groupSubmit}>
                  <Button
                    color="primary"
                    variant="contained"
                    type="submit"
                    disabled={
                      checkboxRows.length === 0 || formikBag.values.type === ""
                    }
                    fullWidth
                  >
                    Použít
                  </Button>
                </div>
              </div>
              {checkboxRows.length > 0 && formikBag.values.type === "edit" && (
                <>
                  <div
                    className={classNames(classes.groupInputWrapper, {
                      [classes.groupInputWrapperSelectedRow]: selectedRow
                    })}
                  >
                    <CardCreateAddCategory formikBag={formikBag} />
                  </div>
                  <div
                    className={classNames(classes.groupInputWrapper, {
                      [classes.groupInputWrapperSelectedRow]: selectedRow
                    })}
                  >
                    <CardCreateAddLabel formikBag={formikBag} />
                  </div>
                </>
              )}
            </div>
          </Form>
        )}
      />
    </>
  );
};
