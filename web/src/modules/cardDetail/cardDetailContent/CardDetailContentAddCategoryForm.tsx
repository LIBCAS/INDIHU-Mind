import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import classNames from "classnames";
import { Field, FieldProps, Form, FormikProps } from "formik";
import React, { useContext, useEffect, useState } from "react";
import { Formik } from "../../../components/form/Formik";
import { Select } from "../../../components/form/Select";
import { Modal } from "../../../components/portal/Modal";
import { Popover } from "../../../components/portal/Popover";
import { OptionType } from "../../../components/select/_types";
import { CreateCategory } from "../../../components/tabContent/CreateCategory";
import { categoryGet } from "../../../context/actions/category";
import { GlobalContext, StateProps } from "../../../context/Context";
import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";
import { useStyles as useTextStyles } from "../../../theme/styles/textStyles";
import { CardContentProps, CardProps } from "../../../types/card";
import { CategoryProps } from "../../../types/category";
import { flattenCategory, getFlatCategories } from "../../cardCreate/_utils";
import { useStyles } from "./_cardStyles";
import { onEditCard } from "./_utils";

interface CardDetailContentAddCategoryFormProps {
  card: CardProps;
  setCard: React.Dispatch<React.SetStateAction<CardProps | undefined>>;
  currentCardContent: CardContentProps;
  setCardContents: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
  openForm: any;
  setOpenForm: any;
  anchorEl: any;
  refreshCard: () => void;
}

interface FormValues {
  categories: string[];
}

export const CardDetailContentAddCategoryForm: React.FC<CardDetailContentAddCategoryFormProps> = ({
  card,
  setCard,
  currentCardContent,
  setCardContents,
  openForm,
  setOpenForm,
  anchorEl,
  refreshCard,
}) => {
  const classes = useStyles();

  const classesSpacing = useSpacingStyles();

  const classesText = useTextStyles();

  const [initialValues, setInitialValues] = useState<{
    categories: string[];
  }>({
    categories: [],
  });

  const context: any = useContext(GlobalContext);

  const state: StateProps = context.state;

  const dispatch: Function = context.dispatch;

  const [options, setOptions] = useState<OptionType[]>([]);

  const [createValue, setCreateValue] = useState<string>("");

  const [open, setOpen] = useState(false);

  const handleCreate = (inputValue: string) => {
    setCreateValue(inputValue);
    setOpen(true);
  };

  useEffect(() => {
    if (initialValues.categories.length === 0) {
      // to get whole category path (subcategory > subsubcategory)
      setInitialValues({ categories: card.categories.map((c) => c.id) });
    }
  }, [
    card.categories,
    state.category.categories,
    initialValues.categories.length,
  ]);

  useEffect(() => {
    const flatten = flattenCategory(state.category.categories);
    setOptions(flatten);
  }, [state.category.categories]);

  const handleSubmit = (values: FormValues) => {
    // to get transform option to normal category
    const allFlatten = getFlatCategories(state.category.categories);
    const resultFlatten = allFlatten.filter((o) =>
      values.categories.some((id) => id === o.id)
    );
    onEditCard(
      "categories",
      resultFlatten,
      card,
      setCard,
      currentCardContent,
      setCardContents,
      refreshCard
    );
    setOpenForm(false);
  };
  return (
    <React.Fragment>
      <Popover
        open={openForm}
        setOpen={setOpenForm}
        anchorEl={anchorEl.current}
        overflowVisible
        content={
          <div className={classNames(classesSpacing.p2, classesSpacing.pt3)}>
            <Formik
              initialValues={initialValues}
              enableReinitialize
              onSubmit={handleSubmit}
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
                      Kategorie karty {currentCardContent.card.name}
                    </Typography>
                    <Field
                      name="categories"
                      render={({ field, form }: FieldProps<any>) => {
                        const loadCategories = (created: CategoryProps) => {
                          formikBag.setFieldValue("categories", [
                            ...formikBag.values.categories,
                            created.id,
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
                            <Select
                              form={form}
                              field={field}
                              loading={false}
                              isMulti={true}
                              options={options}
                              onCreate={handleCreate}
                              autoFocus={false}
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
    </React.Fragment>
  );
};
