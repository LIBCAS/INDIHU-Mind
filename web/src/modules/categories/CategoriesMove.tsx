import React, { useState, useContext, useEffect, useCallback } from "react";
import { Formik } from "../../components/form/Formik";
import { Form, Field, FormikProps } from "formik";
import { CategoryProps } from "../../types/category";
import { Button } from "@material-ui/core";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { GlobalContext } from "../../context/Context";
import { Select } from "../../components/select/Select";
import { OptionType } from "../../components/select/_types";
import { api } from "../../utils/api";
import {
  STATUS_ERROR_TEXT_SET,
  STATUS_ERROR_COUNT_CHANGE,
} from "../../context/reducers/status";
import { Loader } from "../../components/loader/Loader";
import { MessageSnackbar } from "../../components/messages/MessageSnackbar";
import classNames from "classnames";

interface CategoriesMoveProps {
  category: CategoryProps;
  setOpen: (value: boolean) => void;
  loadCategories: Function;
  parentCategory?: CategoryProps;
}

interface CategoriesMoveFormValues {
  parentCategory: string | undefined;
}

export const CategoriesMove: React.FC<CategoriesMoveProps> = ({
  category,
  setOpen,
  loadCategories,
  parentCategory,
}) => {
  const context: any = useContext(GlobalContext);
  const state: any = context.state;
  const dispatch: Function = context.dispatch;

  const [options, setOptions] = useState<any[]>([]);
  const [selectInput, setSelectInput] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<boolean | string>(false);

  const classesLayout = useLayoutStyles();
  const classesSpacing = useSpacingStyles();

  // parses out HTML tags from label
  const parseLabel = (label: string) => {
    let div = document.createElement("div");
    div.innerHTML = label;
    return div.textContent || div.innerText;
  };

  const loadOptions = useCallback(
    (text = ""): any[] => {
      let categories: CategoryProps[] = [];
      // groups categories that matches search text and their parents into categories array
      const filterRecursively = (cat: CategoryProps, level = 0): boolean => {
        if (cat.id === category.id) {
          return false;
        }
        const name = `<span style="margin-left: ${level * 16}px;">${
          cat.name
        }</span>`;
        let contains = cat.name.includes(text);
        categories.push({ ...cat, name });
        const subCategoryContains: boolean =
          cat.subCategories !== undefined &&
          cat.subCategories.reduce(
            (accumulator: boolean, subCategory) =>
              filterRecursively(subCategory, level + 1) || accumulator,
            false
          );
        !contains && !subCategoryContains && categories.pop();
        return contains || subCategoryContains;
      };
      state.category.categories.forEach((c: CategoryProps) =>
        filterRecursively(c)
      );
      return categories;
    },
    [state.category.categories, category.id]
  );

  useEffect(() => {
    const opts: OptionType[] = loadOptions(
      selectInput
    ).map((c: CategoryProps) => ({ label: c.name, value: c.id }));
    setOptions(opts);
  }, [selectInput, loadOptions]);

  const handleSubmit = (values: CategoriesMoveFormValues) => {
    const parent = values.parentCategory;
    setLoading(true);
    api()
      .put(`category/${category.id}`, {
        json: {
          ...category,
          parent: parent,
        },
      })
      .json<CategoryProps>()
      .then((res: any) => {
        setLoading(false);
        dispatch({
          type: STATUS_ERROR_TEXT_SET,
          payload: "Kategorie byla úspěšně přesunuta",
        });
        dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
        loadCategories(res);
        setOpen(false);
      })
      .catch(() => {
        setLoading(false);
        setError("Kategorii se nepodařilo přesunout");
      });
  };
  return (
    <>
      <Loader loading={loading} />
      {error && <MessageSnackbar setVisible={setError} message={error} />}
      <Formik
        initialValues={{
          parentCategory: parentCategory ? parentCategory.id : null,
        }}
        onSubmit={handleSubmit}
        render={(formikBag: FormikProps<CategoriesMoveFormValues>) => (
          <Form
            className={classNames(
              classesSpacing.mb1,
              classesSpacing.ml2,
              classesSpacing.mr2
            )}
          >
            <Field
              name="parentCategory"
              render={() => (
                <Select
                  value={formikBag.values.parentCategory}
                  loading={false}
                  label="Vyberte nadřazenou kategorii"
                  placeholder="Žádná nadřazená kategorie"
                  options={options}
                  onInputChangeCallback={(value: string) =>
                    setSelectInput(value)
                  }
                  //filter keeps all options, because they are already filtered by input value
                  customFilter={() => true}
                  onChange={(value: any) =>
                    formikBag.setFieldValue("parentCategory", value)
                  }
                  parseSelectedLabel={parseLabel}
                />
              )}
            ></Field>
            <div
              className={classNames(
                classesLayout.flex,
                classesLayout.spaceBetween,
                classesSpacing.pt1
              )}
            >
              <Button onClick={() => setOpen(true)}>Zrušit</Button>
              <Button
                disabled={!parentCategory}
                color="primary"
                onClick={() => handleSubmit({ parentCategory: undefined })}
              >
                Osamostatnit
              </Button>
              <Button type="submit" color="primary">
                Potvrdit
              </Button>
            </div>
          </Form>
        )}
      />
    </>
  );
};
