import React, { useState, useEffect, useContext } from "react";
import { FormikProps, Field, FieldProps } from "formik";
import Typography from "@material-ui/core/Typography";
import Button from "@material-ui/core/Button";
import * as Yup from "yup";
import uuid from "uuid/v4";
import { get } from "lodash";

import { GlobalContext } from "../../context/Context";
import {
  STATUS_ERROR_COUNT_CHANGE,
  STATUS_ERROR_TEXT_SET
} from "../../context/reducers/status";
import { Formik } from "../form/Formik";
import { api } from "../../utils/api";
import { InputText } from "../form/InputText";
import { ColorPicker } from "../form/ColorPicker";
import { MessageSnackbar } from "../messages/MessageSnackbar";
import { Loader } from "../loader/Loader";

import { LabelProps } from "../../types/label";

import { useStyles } from "./_tabContentStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";

interface CreateLabelProps {
  loadLabels: Function;
  setOpen: Function;
  previousLabel?: LabelProps;
  name?: string;
}

const CategorySchema = Yup.object().shape({
  name: Yup.string()
    .max(50, "Příliš dlouhé")
    .required("Povinné"),
  color: Yup.string().required("Povinné")
});

export const CreateLabel: React.FC<CreateLabelProps> = ({
  loadLabels,
  setOpen,
  previousLabel,
  name
}) => {
  const classes = useStyles();
  const classesSpacing = useSpacingStyles();
  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<boolean | string>(false);
  const [initialValues, setInitialValues] = useState({
    id: uuid(),
    name: name ? name : "",
    owner: {},
    color: ""
  });

  useEffect(() => {
    if (previousLabel) {
      const { id, color, name } = previousLabel;
      setInitialValues({
        id,
        name,
        color,
        owner: {}
      });
    }
  }, [previousLabel]);

  const onSubmit = (values: LabelProps) => {
    if (loading) return false;
    setLoading(true);
    const label = {
      id: values.id,
      name: values.name,
      color: values.color
    };
    api()
      .put(`label/${values.id}`, {
        json: label
      })

      .json<any[]>()
      .then((res: any) => {
        dispatch({
          type: STATUS_ERROR_TEXT_SET,
          payload: previousLabel
            ? `Štítek ${values.name} byl úspěšně změněn`
            : `Nový štítek ${values.name} byl úspěšně vytvořen`
        });
        dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
        setLoading(false);
        loadLabels(res);
        setOpen(false);
      })
      .catch(err => {
        setLoading(false);
        setError(
          get(err, "response.errorType") === "ERR_NAME_ALREADY_EXISTS"
            ? "Štítek se zvoleným názvem již existuje."
            : true
        );
      });
  };

  return (
    <>
      <Loader loading={loading} />
      {error && <MessageSnackbar setVisible={setError} message={error} />}
      <Formik
        initialValues={initialValues}
        enableReinitialize
        validationSchema={CategorySchema}
        onSubmit={onSubmit}
        render={(formikBag: FormikProps<LabelProps>) => (
          <form
            onReset={formikBag.handleReset}
            onSubmit={(e: any) => {
              e.preventDefault();
              e.stopPropagation();
              if (formikBag.isSubmitting) return false;
              formikBag.submitForm();
            }}
          >
            <div className={classes.categoryWrapper}>
              <Typography
                variant="h5"
                color="inherit"
                align="center"
                gutterBottom
              >
                {previousLabel ? "Editace štítku" : "Vytvoření štítku"}
              </Typography>
              <Field
                name="name"
                render={({ field, form }: FieldProps<LabelProps>) => (
                  <InputText
                    field={field}
                    form={form}
                    label="Název"
                    autoFocus={false}
                  />
                )}
              />
              <Field
                name="color"
                render={({ form }: FieldProps<LabelProps>) => (
                  <ColorPicker
                    defaultColor={previousLabel && previousLabel.color}
                    onChange={(color: any) => {
                      form.setFieldValue("color", color);
                    }}
                  />
                )}
              />
              <Button
                className={classesSpacing.mt3}
                variant="contained"
                color="primary"
                type="submit"
              >
                {previousLabel ? "Změnit štítek" : "Vytvořit štítek"}
              </Button>
            </div>
          </form>
        )}
      />
    </>
  );
};
