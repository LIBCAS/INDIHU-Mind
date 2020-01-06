import React, { useState } from "react";
import { Form, Field, FieldProps } from "formik";
import Button from "@material-ui/core/Button";
import * as Yup from "yup";
import classNames from "classnames";

import {
  STATUS_ERROR_COUNT_CHANGE,
  STATUS_ERROR_TEXT_SET
} from "../../context/reducers/status";
import { Formik } from "../../components/form/Formik";
import { api } from "../../utils/api";
import { InputText } from "../../components/form/InputText";
import { Loader } from "../../components/loader/Loader";
import { MessageSnackbar } from "../../components/messages/MessageSnackbar";

import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { CategoryProps } from "../../types/category";

interface CategoriesRenameValues {
  name: string;
}

interface CategoiresRenameProps {
  category: CategoryProps;
  setContent: Function;
  loadCategories: Function;
  setOpen: Function;
  dispatch: any;
}

const initialValues = {
  name: ""
};

const CategoriesRenameSchema = Yup.object().shape({
  name: Yup.string().required("Povinné")
});

export const CategoriesRename: React.FC<CategoiresRenameProps> = ({
  category,
  setContent,
  loadCategories,
  setOpen,
  dispatch
}) => {
  const classesSpacing = useSpacingStyles();
  const classesLayout = useLayoutStyles();
  const classesText = useTextStyles();
  const [loading, setLoading] = useState<boolean>(false);
  const [errorShow, setErrorShow] = useState<boolean>(false);
  return (
    <>
      <Loader loading={loading} />
      {errorShow && <MessageSnackbar setVisible={setErrorShow} />}
      <Formik
        initialValues={initialValues}
        validationSchema={CategoriesRenameSchema}
        onSubmit={(values: CategoriesRenameValues) => {
          if (loading) return false;
          setLoading(true);
          let transformedCategory = {
            ...category,
            name: values.name
          };
          if (category.parentId) {
            transformedCategory.parent = { id: category.parentId };
          }
          api()
            .put(`category/${category.id}`, {
              json: transformedCategory
            })
            .then(() => {
              dispatch({
                type: STATUS_ERROR_TEXT_SET,
                payload: `Kategorie ${category.name} byla úspěšně přejmenována`
              });
              dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
              setLoading(false);
              loadCategories();
              setContent("menu");
              setOpen(false);
            })
            .catch(() => {
              setLoading(false);
              setErrorShow(true);
            });
        }}
        render={() => (
          <Form>
            <div
              style={{ maxWidth: "300px" }}
              className={classNames(
                classesLayout.flex,
                classesLayout.flexWrap,
                classesLayout.justifyCenter,
                classesSpacing.m1
              )}
            >
              <Field
                name="name"
                render={({
                  field,
                  form
                }: FieldProps<CategoriesRenameValues>) => (
                  <InputText
                    label="Přejmenovat kategorii"
                    field={field}
                    form={form}
                    autoFocus
                  />
                )}
              />
              <div
                className={classNames(
                  classesSpacing.mt2,
                  classesLayout.flex,
                  classesLayout.spaceBetween,
                  classesLayout.flexGrow,
                  classesLayout.directionRowReverse
                )}
              >
                <Button variant="text" color="primary" type="submit">
                  Přejmenovat
                </Button>
                <Button
                  className={classNames(classesText.textGrey)}
                  variant="text"
                  onClick={() => setContent("menu")}
                >
                  Zrušit
                </Button>
              </div>
            </div>
          </Form>
        )}
      />
    </>
  );
};
