import React, { useState } from "react";
import MuiTooltip from "@material-ui/core/Tooltip";
import { FormikProps, Field, Form } from "formik";
import * as Yup from "yup";
import ClickAwayListener from "@material-ui/core/ClickAwayListener";
import Button from "@material-ui/core/Button";
import Add from "@material-ui/icons/Add";

import { ButtonGrey } from "../../../components/control/ButtonGrey";
import { Formik } from "../../../components/form/Formik";
import { CardContentProps } from "../../../types/card";

import { onEditCard, isNoteTextEmpty } from "./_utils";
import { useStyles as useTextStyles } from "../../../theme/styles/textStyles";
import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";
import { useTooltipStyles } from "./_cardStyles";
import { Editor } from "../../../components/editor";
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
  note: Yup.string(),
});

export const CardDetailContentNote: React.FC<CardDetailContentNoteProps> = ({
  card,
  setCardContent,
  note,
}) => {
  const [edit, setEdit] = useState<boolean>(false);
  const classesText = useTextStyles();
  const classesSpacing = useSpacingStyles();
  const classesTooltip = useTooltipStyles();
  const onSubmit = (values: FormValues) => {
    isNoteTextEmpty(JSON.parse(values.note))
      ? onEditCard("note", "", card, setCardContent)
      : onEditCard("note", values.note, card, setCardContent);
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
                      onChange={(value: string) =>
                        formikBag.setFieldValue("note", value)
                      }
                      value={note}
                      component={Editor}
                    />
                    <div className={classesSpacing.mt2}>
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
                  </div>
                </ClickAwayListener>
              </Form>
            );
          }}
        />
      ) : note ? (
        <MuiTooltip
          title="Kliknutím můžete editovat popis."
          enterDelay={500}
          leaveDelay={250}
          arrow={true}
          placement="bottom-start"
          classes={classesTooltip}
        >
          <div
            className={classesText.cursor}
            onClick={() => (card.lastVersion ? setEdit(true) : undefined)}
          >
            <Editor value={note} readOnly />
          </div>
        </MuiTooltip>
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
