import React, { useState, useEffect, useContext } from "react";
import Typography from "@material-ui/core/Typography";
import Button from "@material-ui/core/Button";
import classNames from "classnames";

import { Form, Field, FieldProps, FormikProps } from "formik";
import { Formik } from "../../../components/form/Formik";
import { GlobalContext, StateProps } from "../../../context/Context";
import { labelGet } from "../../../context/actions/label";
import { CreateLabel } from "../../../components/tabContent/CreateLabel";

import { LabelProps } from "../../../types/label";
import { OptionType } from "../../../components/form/reactSelect/_reactSelectTypes";
import { ReactSelect } from "../../../components/form/reactSelect/ReactSelect";
import { parseLabel } from "../../cardCreate/_utils";
import { CardContentProps } from "../../../types/card";
import { Modal } from "../../../components/portal/Modal";
import { Popover } from "../../../components/portal/Popover";
import { onEditCard } from "./_utils";

import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";
import { useStyles as useTextStyles } from "../../../theme/styles/textStyles";
import { useStyles } from "./_cardStyles";

interface CardDetailContentAddLabelFormProps {
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
  labels: OptionType[];
}

export const CardDetailContentAddLabelForm: React.FC<
  CardDetailContentAddLabelFormProps
> = ({ card, setCardContent, openForm, setOpenForm, anchorEl }) => {
  const classes = useStyles();
  const classesSpacing = useSpacingStyles();
  const classesText = useTextStyles();
  const [initialValues, setInitialValues] = useState<{
    labels: OptionType[];
  }>({
    labels: []
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
    if (initialValues.labels.length === 0) {
      setInitialValues({ labels: card.card.labels.map(parseLabel) });
    }
  }, [card.card.labels, state.label.labels]);

  useEffect(() => {
    const flatten = state.label.labels.map(parseLabel);
    setOptions(flatten);
  }, [state.label.labels]);

  const onSubmit = (values: FormValues) => {
    // to transform option to normal label
    const resultTransformed = state.label.labels.filter(o =>
      values.labels.some(c => c.value === o.id)
    );
    onEditCard("labels", resultTransformed, card, setCardContent);
    setOpenForm(false);
  };
  return (
    <>
      <Popover
        open={openForm}
        setOpen={setOpenForm}
        overflowVisible
        anchorEl={anchorEl.current}
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
                      Štítky karty {card.card.name}
                    </Typography>
                    <Field
                      name="labels"
                      render={({ field, form }: FieldProps<any>) => {
                        const loadLabels = (created: LabelProps) => {
                          formikBag.setFieldValue("labels", [
                            ...formikBag.values.labels,
                            parseLabel(created)
                          ]);
                          labelGet(dispatch);
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
                                <CreateLabel
                                  setOpen={setOpen}
                                  loadLabels={loadLabels}
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
