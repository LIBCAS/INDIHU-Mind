import MuiButton from "@material-ui/core/Button";
import MuiClickAwayListener from "@material-ui/core/ClickAwayListener";
import MuiTooltip from "@material-ui/core/Tooltip";
import MuiTypography from "@material-ui/core/Typography";
import classNames from "classnames";
import { Field, FieldProps, Form, FormikProps } from "formik";
import React, { useState } from "react";
import * as Yup from "yup";
import { Formik } from "../../../components/form/Formik";
import { InputText } from "../../../components/form/InputText";
import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";
import { useStyles as useTextStyles } from "../../../theme/styles/textStyles";
import { CardContentProps, CardProps } from "../../../types/card";
import { useTooltipStyles } from "./_cardStyles";
import { onEditCard } from "./_utils";

interface CardDetailContentTitleProps {
  card: CardProps;
  setCard: React.Dispatch<React.SetStateAction<CardProps | undefined>>;
  currentCardContent: CardContentProps;
  cardContents: CardContentProps[] | undefined;
  setCardContents: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
  title: string;
  disabled?: boolean;
}

interface FormValues {
  title: string;
}

const validationSchema = Yup.object().shape({
  title: Yup.string()
    .required("Povinné")
    .max(255, "Smí mít maximálně 255 znaků"),
});

export const CardDetailContentTitle: React.FC<CardDetailContentTitleProps> = ({
  card,
  setCard,
  currentCardContent,
  setCardContents,
  title,
  disabled,
}) => {
  const [edit, setEdit] = useState<boolean>(false);

  const classesText = useTextStyles();

  const classesSpacing = useSpacingStyles();

  const classesTooltip = useTooltipStyles();

  const onSubmit = (values: FormValues) => {
    onEditCard(
      "name",
      values.title,
      card,
      setCard,
      currentCardContent,
      setCardContents
    );
    setEdit(false);
  };

  return (
    <React.Fragment>
      {edit ? (
        <Formik
          initialValues={{ title }}
          enableReinitialize
          onSubmit={onSubmit}
          validationSchema={validationSchema}
          render={(formikBag: FormikProps<FormValues>) => {
            return (
              <Form>
                <MuiClickAwayListener
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
                          inputProps={{ autoFocus: false }}
                        />
                      )}
                    />
                    <MuiButton
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
                    </MuiButton>
                    <MuiButton
                      className={classesSpacing.mt1}
                      type="button"
                      size="small"
                      onClick={() => setEdit(false)}
                      color="secondary"
                      variant="outlined"
                    >
                      Zrušit
                    </MuiButton>
                  </div>
                </MuiClickAwayListener>
              </Form>
            );
          }}
        />
      ) : (
        <MuiTooltip
          title={disabled ? "" : "Kliknutím můžete editovat název karty."}
          enterDelay={500}
          leaveDelay={250}
          arrow={true}
          placement="bottom-start"
          classes={classesTooltip}
        >
          <MuiTypography
            onClick={() =>
              !disabled && currentCardContent.lastVersion
                ? setEdit(true)
                : undefined
            }
            variant="h5"
            className={classNames(classesSpacing.mb1, {
              [classesText.cursor]: currentCardContent.lastVersion,
            })}
          >
            {title}
          </MuiTypography>
        </MuiTooltip>
      )}
    </React.Fragment>
  );
};
