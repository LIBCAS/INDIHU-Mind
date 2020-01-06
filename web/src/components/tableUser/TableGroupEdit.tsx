import React, { useState, useContext } from "react";
import Button from "@material-ui/core/Button";
import { FormikProps, Form, Field } from "formik";
import classNames from "classnames";

import { GlobalContext } from "../../context/Context";
import {
  STATUS_ERROR_COUNT_CHANGE,
  STATUS_ERROR_TEXT_SET
} from "../../context/reducers/status";
import { usersUpdated } from "../../context/actions/users";
import { api } from "../../utils/api";
import { notEmpty } from "../../utils/form/validate";
import { UserProps } from "../../types/user";
import { Formik } from "../form/Formik";
import { Select } from "../form/Select";
import { Switch } from "../form/Switch";

import { Loader } from "../loader/Loader";

import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles } from "./_tableStyles";

interface TableGroupEditProps {
  checkboxRows: UserProps[];
  loadData: Function;
  setCheckboxRows: React.Dispatch<React.SetStateAction<UserProps[]>>;
}

type TypeProps = "edit" | "delete" | "";

const types: { value: TypeProps; label: string }[] = [
  { value: "edit", label: "Upravit" }
];

export interface FormValues {
  type: TypeProps;
  state: boolean;
}

const initialValues = {
  type: "",
  state: true
};

export const TableGroupEdit: React.FC<TableGroupEditProps> = ({
  checkboxRows,
  loadData,
  setCheckboxRows
}) => {
  const classes = useStyles();
  const classesSpacing = useSpacingStyles();
  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;
  const [loading, setLoading] = useState(false);
  const onSubmit = (values: FormValues, actions: any) => {
    if (loading) return;
    actions.resetForm(initialValues);
    setLoading(true);
    const request = api().post(`admin/set_allowance`, {
      json: {
        ids: checkboxRows.map(checkbox => checkbox.id),
        value: values.state
      }
    });
    request
      .then(() => {
        usersUpdated(dispatch, true);
        setCheckboxRows([]);
        loadData();
        setLoading(false);
        dispatch({
          type: STATUS_ERROR_TEXT_SET,
          payload: `Změny uloženy`
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
                <div className={classesSpacing.mt1}>
                  <Field
                    name="state"
                    component={Switch}
                    label={`Stav registrace ${
                      formikBag.values.state ? "(povoleno)" : "(zakázáno)"
                    }`}
                    secondary
                  />
                </div>
              )}
            </div>
          </Form>
        )}
      />
    </>
  );
};
