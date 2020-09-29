import React, { useState, useEffect, useContext } from "react";
import { Field, FieldProps } from "formik";

import { GlobalContext, StateProps } from "../../context/Context";
import { categoryGet } from "../../context/actions/category";
import { CreateCategory } from "../../components/tabContent/CreateCategory";
import { Modal } from "../../components/portal/Modal";
import { Select } from "../../components/form/Select";

import { CategoryProps } from "../../types/category";
import { OptionType } from "../../components/select/_types";

import { flattenCategory, parseCategory } from "./_utils";

interface CardCreateAddCategoryProps {
  formikBag: any;
}

export const CardCreateAddCategory: React.FC<CardCreateAddCategoryProps> = ({
  formikBag
}) => {
  const context: any = useContext(GlobalContext);

  const state: StateProps = context.state;

  const dispatch: Function = context.dispatch;

  const [options, setOptions] = useState<OptionType[]>([]);

  const [createValue, setCreateValue] = useState<string>("");

  const [open, setOpen] = useState(false);

  const loadCategories = (created: CategoryProps) => {
    categoryGet(dispatch);
    formikBag.setFieldValue("categories", [
      ...formikBag.values.categories,
      ...flattenCategory([created])
    ]);
  };

  const onCreate = (inputValue: string) => {
    setCreateValue(inputValue);
    setOpen(true);
  };

  useEffect(() => {
    const flatten = state.category.categoryActive
      ? flattenCategory([
          ...state.category.categories,
          state.category.categoryActive
        ])
      : flattenCategory([...state.category.categories]);
    setOptions(flatten);

    if (state.category.categoryActive !== undefined) {
      formikBag.setFieldValue(
        "categories",
        parseCategory(state.category.categoryActive, state.category.categories)
      );
    }
  }, [state.category.categories]);

  return (
    <Field
      name="categories"
      render={({ field, form }: FieldProps<any>) => {
        return (
          <div>
            <Select
              form={form}
              field={field}
              loading={false}
              isMulti={true}
              label="Kategorie"
              options={options}
              onCreate={onCreate}
            />
            <Modal
              open={open}
              setOpen={setOpen}
              content={
                <CreateCategory
                  setOpen={setOpen}
                  activeCategory={undefined}
                  loadCategories={loadCategories}
                  name={createValue}
                />
              }
            />
          </div>
        );
      }}
    />
  );
};
