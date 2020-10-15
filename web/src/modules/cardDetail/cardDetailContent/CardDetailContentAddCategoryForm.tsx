import React, { useState, useEffect, useContext } from "react";
import Typography from "@material-ui/core/Typography";
import Button from "@material-ui/core/Button";
import classNames from "classnames";

import { Form, Field, FieldProps, FormikProps } from "formik";
import { Formik } from "../../../components/form/Formik";
import { GlobalContext, StateProps } from "../../../context/Context";
import { categoryGet } from "../../../context/actions/category";
import { CreateCategory } from "../../../components/tabContent/CreateCategory";

import { CategoryProps } from "../../../types/category";
import { OptionType } from "../../../components/form/reactSelect/_reactSelectTypes";
import { ReactSelect } from "../../../components/form/reactSelect/ReactSelect";
import { flattenCategory, getFlatCategories } from "../../cardCreate/_utils";
import { CardContentProps } from "../../../types/card";
import { Modal } from "../../../components/portal/Modal";
import { Popover } from "../../../components/portal/Popover";
import { onEditCard } from "./_utils";

import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";
import { useStyles as useTextStyles } from "../../../theme/styles/textStyles";
import { useStyles } from "./_cardStyles";

interface CardDetailContentAddCategoryFormProps {
  card: CardContentProps;
  cardContent: CardContentProps[] | undefined;
  setCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
  openForm: any;
  setOpenForm: any;
  anchorEl: any;
}

interface FormValues {
  categories: OptionType[];
}

export const CardDetailContentAddCategoryForm: React.FC<
  CardDetailContentAddCategoryFormProps
> = ({ card, setCardContent, openForm, setOpenForm, anchorEl }) => {
  const classes = useStyles();
  const classesSpacing = useSpacingStyles();
  const classesText = useTextStyles();
  const [initialValues, setInitialValues] = useState<{
    categories: OptionType[];
  }>({
    categories: []
  });
  const context: any = useContext(GlobalContext);
  const state: StateProps = context.state;
  const dispatch: Function = context.dispatch;
  const [options, setOptions] = useState<OptionType[]>([]);
  const [createValue, setCreateValue] = useState<string>("");
  const [open, setOpen] = useState(false);
  const onCreate = (inputValue: string) => {
    setCreateValue(inputValue);
    setOpen(true);
  };
  useEffect(() => {
    if (initialValues.categories.length === 0) {
      // to get whole category path (subcategory > subsubcategory)
      const allFlatten = flattenCategory(state.category.categories);
      const cardFlatten = flattenCategory(card.card.categories);
      const resultFlatten = allFlatten.filter(o =>
        cardFlatten.some(c => c.value === o.value)
      );
      setInitialValues({ categories: resultFlatten });
    }
  }, [card.card.categories, state.category.categories]);

  useEffect(() => {
    const flatten = flattenCategory(state.category.categories);
    setOptions(flatten);
  }, [state.category.categories]);

  const onSubmit = (values: FormValues) => {
    // to get transform option to normal category
    const allFlatten = getFlatCategories(state.category.categories);
    const resultFlatten = allFlatten.filter(o =>
      values.categories.some(c => c.value === o.id)
    );
    onEditCard(
      "categories",
      // values.categories.map(v => ({ id: v.value })),
      resultFlatten,
      card,
      setCardContent
    );
    setOpenForm(false);
  };
  return (
    <>
      <Popover
        open={openForm}
        setOpen={setOpenForm}
        anchorEl={anchorEl.current}
        overflowVisible
        content={
          <div>
            <Formik
              initialValues={initialValues}
              enableReinitialize
              onSubmit={onSubmit}
              render={(formikBag: FormikProps<FormValues>) => (
                <Form>
                  <div className={classes.categoryFormWrapper}>
                    <Typography
                      className={classNames(
                        classesText.textCenter,
                        classesSpacing.mt1,
                        classesSpacing.mr1,
                        classesSpacing.ml1
                      )}
                      variant="h5"
                    >
                      Kategorie karty {card.card.name}
                    </Typography>
                    <Field
                      name="categories"
                      render={({ field, form }: FieldProps<any>) => {
                        const loadCategories = (created: CategoryProps) => {
                          formikBag.setFieldValue("categories", [
                            ...formikBag.values.categories,
                            ...flattenCategory([created])
                          ]);
                          categoryGet(dispatch);
                        };
                        return (
                          <div className={classNames(classesSpacing.m1)}>
                            <div className={classesText.textCenter}>
                              <Button
                                type="submit"
                                color="primary"
                                variant="contained"
                                className={classNames(
                                  classesSpacing.mt2,
                                  classesSpacing.mb1
                                )}
                              >
                                Uložit změny
                              </Button>
                            </div>
                            <ReactSelect
                              form={form}
                              field={field}
                              loading={false}
                              options={options}
                              onCreate={onCreate}
                              autoFocus
                              menuIsOpen
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
                  </div>
                </Form>
              )}
            />
          </div>
        }
      />
    </>
  );
};
