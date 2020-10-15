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
import { recordGet } from "../../../context/actions/record";

interface CardDetailContentAddRecordFormProps {
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
  records: OptionType[];
}

export const CardDetailContentAddRecordForm: React.FC<
  CardDetailContentAddRecordFormProps
> = ({ card, setCardContent, openForm, setOpenForm, anchorEl }) => {
  const classes = useStyles();
  const classesSpacing = useSpacingStyles();
  const classesText = useTextStyles();
  const [initialValues, setInitialValues] = useState<{
    records: OptionType[];
  }>({
    records: []
  });
  const context: any = useContext(GlobalContext);
  const state: StateProps = context.state;
  const dispatch: Function = context.dispatch;
  const [options, setOptions] = useState<OptionType[]>([]);
  const [open, setOpen] = useState(false);
  useEffect(() => {
    recordGet(dispatch);
  }, []);
  useEffect(() => {
    if (initialValues.records.length === 0) {
      setInitialValues({ records: card.card.records.map(parseLabel) });
    }
  }, [card.card.records, state.record.records]);

  useEffect(() => {
    const flatten = state.record.records.map(parseLabel);
    setOptions(flatten);
  }, [state.record.records]);

  const onSubmit = (values: FormValues) => {
    // to transform option to normal record
    const resultTransformed = state.record.records.filter(o =>
      values.records.some(c => c.value === o.id)
    );
    onEditCard("records", resultTransformed, card, setCardContent);
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
                      Citace karty {card.card.name}
                    </Typography>
                    <Field
                      name="records"
                      render={({ field, form }: FieldProps<any>) => {
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
                              autoFocus
                              menuIsOpen
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
