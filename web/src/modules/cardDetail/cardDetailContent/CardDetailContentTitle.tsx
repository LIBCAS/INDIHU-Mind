import React, { useState } from "react";
import Typography from "@material-ui/core/Typography";
import { FormikProps, FieldProps, Field, Form } from "formik";
import * as Yup from "yup";
import ClickAwayListener from "@material-ui/core/ClickAwayListener";
import Button from "@material-ui/core/Button";
import classNames from "classnames";

import { Formik } from "../../../components/form/Formik";
import { InputText } from "../../../components/form/InputText";
import { CardContentProps } from "../../../types/card";

import { onEditCard } from "./_utils";
import { useStyles as useTextStyles } from "../../../theme/styles/textStyles";
import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";

interface CardDetailContentTitleProps {
  card: CardContentProps;
  cardContent: CardContentProps[] | undefined;
  setCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
  title: string;
}

interface FormValues {
  title: string;
}

const validationSchema = Yup.object().shape({
  title: Yup.string().required("Povinné")
});

export const CardDetailContentTitle: React.FC<CardDetailContentTitleProps> = ({
  card,
  setCardContent,
  title
}) => {
  const [edit, setEdit] = useState<boolean>(false);
  const classesText = useTextStyles();
  const classesSpacing = useSpacingStyles();
  const onSubmit = (values: FormValues) => {
    onEditCard("name", values.title, card, setCardContent);
    setEdit(false);
  };
  return (
    <>
      {edit ? (
        <Formik
          initialValues={{ title }}
          enableReinitialize
          onSubmit={onSubmit}
          validationSchema={validationSchema}
          render={(formikBag: FormikProps<FormValues>) => {
            return (
              <Form>
                <ClickAwayListener
                  onClickAway={() => {
                    if (formikBag.isSubmitting) return;
                    formikBag.submitForm();
                  }}
                >
                  <div className={classesSpacing.mb1}>
                    <Field
                      name="title"
                      render={({ field, form }: FieldProps<FormValues>) => (
                        <InputText
                          field={field}
                          form={form}
                          inputProps={{ autoFocus: true }}
                        />
                      )}
                    />
                    <Button
                      className={classNames(
                        classesSpacing.mr2,
                        classesSpacing.mt1
                      )}
                      size="small"
                      color="primary"
                      variant="contained"
                      type="submit"
                    >
                      OK
                    </Button>
                    <Button
                      className={classesSpacing.mt1}
                      type="button"
                      size="small"
                      onClick={() => setEdit(false)}
                      color="secondary"
                      variant="outlined"
                    >
                      Zrušit
                    </Button>
                  </div>
                </ClickAwayListener>
              </Form>
            );
          }}
        />
      ) : (
        <Typography
          onClick={() => (card.lastVersion ? setEdit(true) : undefined)}
          variant="h5"
          className={classNames(classesSpacing.mb1, {
            [classesText.cursor]: card.lastVersion
          })}
        >
          Detail karty {title}
        </Typography>
      )}
    </>
  );
};
