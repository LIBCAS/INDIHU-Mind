import { Field, FieldProps } from "formik";
import React, { useContext, useEffect, useState } from "react";
import { Select } from "../../components/form/Select";
import { Modal } from "../../components/portal/Modal";
import { OptionType } from "../../components/select/_types";
import { CreateCategory } from "../../components/tabContent/CreateCategory";
import { categoryGet } from "../../context/actions/category";
import { GlobalContext, StateProps } from "../../context/Context";
import { CategoryProps } from "../../types/category";
import { flattenCategory } from "./_utils";

interface CardCreateAddCategoryProps {
  formikBag: any;
  compact?: boolean;
}

export const CardCreateAddCategory: React.FC<CardCreateAddCategoryProps> = ({
  formikBag,
  compact = false,
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
      created.id,
    ]);
  };

  const onCreate = (inputValue: string) => {
    setCreateValue(inputValue);
    setOpen(true);
  };

  useEffect(() => {
    if (state.category.categoryActive !== undefined) {
      setTimeout(() =>
        formikBag.setFieldValue("categories", [
          ...formikBag.values.categories,
          state.category.categoryActive && state.category.categoryActive.id,
        ])
      );
    }
    // eslint-disable-next-line
  }, [state.category.categoryActive]);

  useEffect(() => {
    const flatten = state.category.categoryActive
      ? flattenCategory([
          ...state.category.categories,
          state.category.categoryActive,
        ])
      : flattenCategory([...state.category.categories]);
    setOptions(flatten);
  }, [state.category.categories, state.category.categoryActive]);

  const label = "Kategorie";

  return (
    <Field
      name="categories"
      value={[{ label: "O", value: "2" }]}
      render={({ field, form }: FieldProps<any>) => {
        return (
          <div>
            <Select
              form={form}
              field={field}
              loading={false}
              isMulti={true}
              label={compact ? undefined : label}
              placeholder={compact ? label : undefined}
              options={options}
              onCreate={onCreate}
              createLabel="Vytvořit kategorii: "
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
