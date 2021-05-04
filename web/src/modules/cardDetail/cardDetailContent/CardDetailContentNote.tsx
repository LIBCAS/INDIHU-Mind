import Button from "@material-ui/core/Button";
import ClickAwayListener from "@material-ui/core/ClickAwayListener";
import MuiTooltip from "@material-ui/core/Tooltip";
import Add from "@material-ui/icons/Add";
import { Field, Form, FormikProps } from "formik";
import React, { useState } from "react";
import * as Yup from "yup";
import { ButtonGrey } from "../../../components/control/ButtonGrey";
import { Editor } from "../../../components/editor";
import { Formik } from "../../../components/form/Formik";
import { Loader } from "../../../components/loader/Loader";
import { MessageSnackbar } from "../../../components/messages/MessageSnackbar";
import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";
import { useStyles as useTextStyles } from "../../../theme/styles/textStyles";
import { CardContentProps, CardProps } from "../../../types/card";
import { useTooltipStyles } from "./_cardStyles";
import { isNoteTextEmpty, onEditCard, onNoteUploadError } from "./_utils";

interface CardDetailContentNoteProps {
  card: CardProps;
  setCard: React.Dispatch<React.SetStateAction<CardProps | undefined>>;
  currentCardContent: CardContentProps;
  setCardContents: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
  note: string | null;
  setStructuredNote: (any: any) => any;
  disabled?: boolean;
}

interface FormValues {
  note: string;
}

const validationSchema = Yup.object().shape({
  note: Yup.string(),
});

export const CardDetailContentNote: React.FC<CardDetailContentNoteProps> = ({
  card,
  setCard,
  currentCardContent,
  setCardContents,
  note,
  setStructuredNote,
  disabled,
}) => {
  const [edit, setEdit] = useState<boolean>(false);
  const [error, setError] = useState({ isError: false, message: "" });
  const [loading, setLoading] = useState(false);
  const classesText = useTextStyles();
  const classesSpacing = useSpacingStyles();
  const classesTooltip = useTooltipStyles();
  const onSubmit = (values: FormValues) => {
    setLoading(true);
    const onSuccess = () => {
      setLoading(false);
      setEdit(false);
      setStructuredNote((prev: any) => ({ ...prev, data: values.note }));
    };
    isNoteTextEmpty(JSON.parse(values.note))
      ? onEditCard(
          "note",
          "",
          card,
          setCard,
          currentCardContent,
          setCardContents,
          onSuccess,
          onNoteUploadError(setError, () => setLoading(false))
        )
      : onEditCard(
          "note",
          values.note,
          card,
          setCard,
          currentCardContent,
          setCardContents,
          onSuccess,
          onNoteUploadError(setError, () => setLoading(false))
        );
  };
  return (
    <>
      <Loader loading={loading} />
      {error.isError && (
        <MessageSnackbar
          setVisible={(val: boolean) =>
            setError((prev) => ({ ...prev, isError: val }))
          }
          message={error.message}
        />
      )}
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
            onClick={() =>
              !disabled && currentCardContent.lastVersion
                ? setEdit(true)
                : undefined
            }
          >
            <Editor value={note} readOnly />
          </div>
        </MuiTooltip>
      ) : currentCardContent.lastVersion ? (
        <div className={classesSpacing.mt1}>
          <ButtonGrey
            text="Přidat popis"
            disabled={disabled}
            onClick={() => !disabled && setEdit(true)}
            bold
            inline
            Icon={<Add fontSize="small" />}
          />
        </div>
      ) : null}
    </>
  );
};
