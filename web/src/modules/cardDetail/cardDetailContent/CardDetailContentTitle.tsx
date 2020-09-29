import React, { useState } from "react";
import MuiTypography from "@material-ui/core/Typography";
import MuiTooltip from "@material-ui/core/Tooltip";
import { FormikProps, FieldProps, Field, Form } from "formik";
import * as Yup from "yup";
import MuiClickAwayListener from "@material-ui/core/ClickAwayListener";
import MuiButton from "@material-ui/core/Button";
import classNames from "classnames";

import { Formik } from "../../../components/form/Formik";
import { InputText } from "../../../components/form/InputText";
import { CardContentProps } from "../../../types/card";

import { onEditCard } from "./_utils";
import { useStyles as useTextStyles } from "../../../theme/styles/textStyles";
import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";
import { useStyles, useTooltipStyles } from "./_cardStyles";

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

  const classes = useStyles();
  const classesTooltip = useTooltipStyles();

  const onSubmit = (values: FormValues) => {
    onEditCard("name", values.title, card, setCardContent);
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
          title="Kliknutím můžete editovat název karty."
          enterDelay={500}
          leaveDelay={250}
          arrow={true}
          placement="bottom-start"
          classes={classesTooltip}
        >
          <MuiTypography
            onClick={() => (card.lastVersion ? setEdit(true) : undefined)}
            variant="h5"
            className={classNames(classesSpacing.mb1, {
              [classesText.cursor]: card.lastVersion
            })}
          >
            {title}
          </MuiTypography>
        </MuiTooltip>
      )}
    </React.Fragment>
  );
};
