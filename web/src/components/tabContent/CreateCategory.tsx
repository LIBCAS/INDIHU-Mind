import React, { useState, useContext, useEffect } from "react";
import { FormikProps, Field, FieldProps } from "formik";
import Typography from "@material-ui/core/Typography";
import Button from "@material-ui/core/Button";
import * as Yup from "yup";
import { v4 as uuid } from "uuid";
import { get } from "lodash";

import { GlobalContext } from "../../context/Context";
import {
  STATUS_ERROR_COUNT_CHANGE,
  STATUS_ERROR_TEXT_SET,
} from "../../context/reducers/status";
import { Formik } from "../form/Formik";
import { api } from "../../utils/api";
import { InputText } from "../form/InputText";
import { MessageSnackbar } from "../messages/MessageSnackbar";
import { Loader } from "../loader/Loader";

import { CategoryProps } from "../../types/category";

import { useStyles } from "./_tabContentStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";

interface CreateCategoryProps {
  activeCategory: CategoryProps | undefined;
  loadCategories: any;
  setOpen: Function;
  name?: string;
}

const CategorySchema = Yup.object().shape({
  name: Yup.string().max(150, "Příliš dlouhé").required("Povinné"),
});

export const CreateCategory: React.FC<CreateCategoryProps> = ({
  setOpen,
  activeCategory,
  loadCategories,
  name,
}) => {
  const classes = useStyles();
  const classesSpacing = useSpacingStyles();
  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;
  const [initialValues, setInitialValues] = useState({
    id: "",
    name: "",
    ordinalNumber: 0,
    owner: {},
    subCategories: [],
    cardsCount: 0,
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<boolean | string>(false);
  useEffect(() => {
    if (name) {
      setInitialValues({
        id: "",
        name: name,
        ordinalNumber: 0,
        owner: {},
        subCategories: [],
        cardsCount: 0,
      });
    }
  }, [name]);

  const onSubmit = (values: CategoryProps) => {
    if (loading) return false;
    setLoading(true);
    const id = uuid();
    const { name } = values;
    let category: CategoryProps = {
      id,
      name,
      ordinalNumber: 0,
      owner: {},
      subCategories: [],
      cardsCount: 0,
    };
    let request;
    if (activeCategory) {
      request = {
        ...category,
        parent: activeCategory,
      };
    } else {
      request = category;
    }
    api()
      .put(`category/${id}`, {
        json: request,
      })
      .json<CategoryProps>()
      .then((res: any) => {
        setLoading(false);
        dispatch({
          type: STATUS_ERROR_TEXT_SET,
          payload: `Nová kategorie ${name} byla úspěšně vytvořena`,
        });
        dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
        loadCategories(res);
        setOpen(false);
      })
      .catch((err) => {
        setLoading(false);
        setError(
          get(err, "response.errorType") === "ERR_NAME_ALREADY_EXISTS"
            ? "Kategorie se zvoleným názvem již existuje."
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
        render={(formikBag: FormikProps<CategoryProps>) => (
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
                {activeCategory
                  ? "Vytvoření podkategorie"
                  : "Vytvoření kategorie"}
              </Typography>
              {activeCategory && (
                <Typography
                  variant="subtitle1"
                  color="inherit"
                  align="center"
                  gutterBottom
                >
                  pro {activeCategory.name}
                </Typography>
              )}
              <Field
                name="name"
                render={({ field, form }: FieldProps<CategoryProps>) => (
                  <InputText
                    label="Název"
                    field={field}
                    form={form}
                    autoFocus={false}
                  />
                )}
              />
              <Button
                className={classesSpacing.mt3}
                variant="contained"
                color="primary"
                type="submit"
              >
                {activeCategory
                  ? "Vytvořit podkategorii"
                  : "Vytvořit kategorii"}
              </Button>
            </div>
          </form>
        )}
      />
    </>
  );
};
