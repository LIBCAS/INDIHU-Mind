import React, { useState, useContext } from "react";
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
import { OptionType } from "../../components/select/_types";
import { Formik } from "../../components/form/Formik";
import { Select } from "../../components/form/Select";

import { Loader } from "../loader/Loader";
import { useStyles } from "./_tableStyles";

interface TableGroupEditProps {
  checkboxRows: any[];
  selectedRow: any;
  loadData: Function;
  setCheckboxRows: React.Dispatch<React.SetStateAction<any[]>>;
  baseUrl: string;
}

type TypeProps = "delete" | "";

const types: { value: TypeProps; label: string }[] = [
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
  setCheckboxRows,
  baseUrl
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
    if (values.type === "delete") {
      const ids = checkboxRows.map(c => c.id);
      for (const id of ids) {
        const request = api().delete(`${baseUrl}/${id}`);
        requests.push(request);
      }
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
          payload: `Smazáno`
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
            </div>
          </Form>
        )}
      />
    </>
  );
};
