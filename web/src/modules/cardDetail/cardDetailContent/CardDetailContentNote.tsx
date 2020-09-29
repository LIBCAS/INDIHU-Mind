import React, { useState } from "react";
import Typography from "@material-ui/core/Typography";
import { FormikProps, FieldProps, Field, Form } from "formik";
import * as Yup from "yup";
import ClickAwayListener from "@material-ui/core/ClickAwayListener";
import Button from "@material-ui/core/Button";
import classNames from "classnames";
import Add from "@material-ui/icons/Add";

import { ButtonGrey } from "../../../components/control/ButtonGrey";
import { Formik } from "../../../components/form/Formik";
import { InputText } from "../../../components/form/InputText";
import { CardContentProps } from "../../../types/card";

import { onEditCard } from "./_utils";
import { useStyles as useTextStyles } from "../../../theme/styles/textStyles";
import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";
import { formatMultiline } from "../../../utils";

interface CardDetailContentNoteProps {
  card: CardContentProps;
  cardContent: CardContentProps[] | undefined;
  setCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
  note: string | null;
}

interface FormValues {
  note: string;
}

const validationSchema = Yup.object().shape({
  note: Yup.string()
});

export const CardDetailContentNote: React.FC<CardDetailContentNoteProps> = ({
  card,
  setCardContent,
  note
}) => {
  const [edit, setEdit] = useState<boolean>(false);
  const classesText = useTextStyles();
  const classesSpacing = useSpacingStyles();
  const onSubmit = (values: FormValues) => {
    onEditCard("note", values.note, card, setCardContent);
    setEdit(false);
  };
  return (
    <>
      {edit ? (
        <Formik
          initialValues={{ note }}
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
                      name="note"
                      render={({ field, form }: FieldProps<FormValues>) => (
                        <InputText
                          field={field}
                          form={form}
                          inputProps={{ autoFocus: false, rows: 4 }}
                          multiline={true}
                        />
                      )}
                    />
                    <Button
                      className={classesSpacing.mr2}
                      size="small"
                      color="primary"
                      variant="contained"
                      type="submit"
                    >
                      OK
                    </Button>
                    <Button
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
      ) : note ? (
        <Typography
          onClick={() => (card.lastVersion ? setEdit(true) : undefined)}
          variant="body1"
          className={classNames(classesSpacing.mt2, classesText.cursor)}
        >
          {formatMultiline(note)}
        </Typography>
      ) : card.lastVersion ? (
        <div className={classesSpacing.mt1}>
          <ButtonGrey
            text="Přidat popis"
            onClick={() => setEdit(true)}
            bold
            inline
            Icon={<Add fontSize="small" />}
          />
        </div>
      ) : null}
    </>
  );
};
