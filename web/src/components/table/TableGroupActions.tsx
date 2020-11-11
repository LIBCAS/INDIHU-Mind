import React, { useState, useContext } from "react";
import { compact } from "lodash";
import Button from "@material-ui/core/Button";
import { FormikProps, Form, Field } from "formik";
import classNames from "classnames";

import { GlobalContext } from "../../context/Context";
import {
  STATUS_ERROR_COUNT_CHANGE,
  STATUS_ERROR_TEXT_SET,
} from "../../context/reducers/status";
import { api } from "../../utils/api";
import { Formik } from "../../components/form/Formik";
import { Select } from "../../components/form/Select";

import { Loader } from "../loader/Loader";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles } from "./_styles";
import {
  TGroupActionsComponent,
  GroupEditMapper,
  GroupEditCallback,
  GroupDeleteCallback,
} from "./_types";

interface TableGroupActionsProps {
  checkboxRows: any[];
  selectedRow: any;
  refresh: Function;
  baseUrl: string;
  GroupActionsComponent: TGroupActionsComponent;
  groupEditMapper: GroupEditMapper;
  onGroupEdit?: GroupEditCallback;
  onGroupDelete?: GroupDeleteCallback;
  enableGroupEdit: boolean;
  enableGroupDelete: boolean;
}

enum ActionTypeEnum {
  EDIT = "EDIT",
  DELETE = "DELETE",
}

type ActionType = ActionTypeEnum | undefined | null;

export interface FormValues {
  _tableGroupActionType: ActionType;
}

const initialValues = {};

export const TableGroupActions: React.FC<TableGroupActionsProps> = ({
  checkboxRows,
  selectedRow,
  refresh,
  baseUrl,
  enableGroupEdit,
  enableGroupDelete,
  GroupActionsComponent,
  groupEditMapper,
  onGroupEdit,
  onGroupDelete,
}) => {
  const classes = useStyles();
  const classesSpacing = useSpacingStyles();

  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;
  const [loading, setLoading] = useState(false);

  const onSubmit = async (values: FormValues, actions: any) => {
    if (loading) return;
    actions.resetForm({ values: initialValues });
    setLoading(true);

    let requests: Promise<any>[] = [];
    const { _tableGroupActionType, ...rest } = values;
    const isEdit = _tableGroupActionType === ActionTypeEnum.EDIT;

    if (isEdit) {
      if (onGroupEdit) {
        requests.push(onGroupEdit(checkboxRows, values));
      } else {
        checkboxRows.forEach((row) => {
          requests.push(
            api().put(`${baseUrl}/${row.id}`, {
              json: groupEditMapper(row, rest),
            })
          );
        });
      }
    } else {
      if (onGroupDelete) {
        requests.push(onGroupDelete(checkboxRows));
      } else {
        checkboxRows.map((row) =>
          requests.push(api().delete(`${baseUrl}/${row.id}`))
        );
      }
    }

    try {
      await Promise.all(requests);

      refresh();
      dispatch({
        type: STATUS_ERROR_TEXT_SET,
        payload: isEdit ? "Položky upraveny." : "Položky smazány.",
      });
      dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
      setLoading(false);
    } catch {
      dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
      setLoading(false);
    }
  };

  const types: { value: ActionTypeEnum; label: string }[] = compact([
    enableGroupEdit && { value: ActionTypeEnum.EDIT, label: "Upravit" },
    enableGroupDelete && { value: ActionTypeEnum.DELETE, label: "Odstranit" },
  ]);

  return enableGroupEdit || enableGroupDelete ? (
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
                [classes.groupEditWrapperNoCheckbox]: checkboxRows.length === 0,
              })}
            >
              <div className={classes.groupTitleWrapper}>
                <div className={classes.groupTitle}>
                  <Field
                    name="_tableGroupActionType"
                    component={Select}
                    options={types}
                    placeholder="Hromadné úpravy"
                  />
                </div>
                {checkboxRows.length > 0 &&
                formikBag.values._tableGroupActionType ===
                  ActionTypeEnum.EDIT ? (
                  <div className={classesSpacing.mr1}>
                    <GroupActionsComponent {...{ selectedRow, formikBag }} />
                  </div>
                ) : (
                  <></>
                )}
                <div className={classes.groupSubmit}>
                  <Button
                    color="primary"
                    variant="contained"
                    type="submit"
                    disabled={
                      checkboxRows.length === 0 ||
                      !formikBag.values._tableGroupActionType
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
  ) : (
    <></>
  );
};
