import { Button, Typography } from "@material-ui/core";
import classNames from "classnames";
import { Field, FieldProps, Form, Formik, FormikProps } from "formik";
import React, { useContext, useEffect, useRef, useState } from "react";
import { Select } from "../../components/form/Select";
import { Loader } from "../../components/loader/Loader";
import { Popover } from "../../components/portal/Popover";
import { OptionType } from "../../components/select/_types";
import { GlobalContext, StateProps } from "../../context/Context";
import { parseLabel } from "../../modules/cardCreate/_utils";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { generateFile } from "../../utils/file";
import { notEmpty } from "../../utils/form/validate";

interface RecordsPdfProps {
  checkboxRows: any[];
  variant: "cards" | "citations";
}

export const FileFromTemplateGenerator: React.FC<RecordsPdfProps> = React.memo(
  ({ checkboxRows, variant }) => {
    const classesText = useTextStyles();
    const classesSpacing = useSpacingStyles();
    const context: any = useContext(GlobalContext);
    const state: StateProps = context.state;
    const dispatch: Function = context.dispatch;

    const [loading, setLoading] = useState(false);
    const [options, setOptions] = useState<OptionType[]>([]);
    const [open, setOpen] = useState(false);

    const anchorEl = useRef(null);

    useEffect(() => {
      const templatesParsed = state.recordTemplate.recordsTemplates.map(
        parseLabel
      );
      setOptions(templatesParsed);
    }, [state.recordTemplate.recordsTemplates]);

    const onSubmit = (values: any) => {
      const id = values.templates;
      const ids = checkboxRows.map((c) => c.id);
      generateFile(
        `template/generate-pdf/with-${variant}`,
        { templateId: id, ids },
        dispatch,
        setLoading,
        "PDF"
      );
    };

    return (
      <>
        <Button
          onClick={() => setOpen(true)}
          ref={anchorEl}
          variant="contained"
          disabled={checkboxRows.length === 0}
        >
          {variant === "cards" ? "Generovat citace" : "Generovat PDF"}
        </Button>
        <Popover
          open={open}
          setOpen={setOpen}
          overflowVisible
          anchorEl={anchorEl.current}
          cancelButton={false}
          content={
            <div>
              <Formik
                initialValues={{
                  templates: null,
                }}
                enableReinitialize
                onSubmit={onSubmit}
                render={(formikBag: FormikProps<any>) => (
                  <Form>
                    <div className={classesSpacing.p2}>
                      <Loader loading={loading} />
                      <Typography
                        className={classNames(
                          classesText.textCenter,
                          classesSpacing.mt1,
                          classesSpacing.mr1,
                          classesSpacing.ml1
                        )}
                        variant="h5"
                      >
                        {variant === "cards"
                          ? "Generovat citace vybraných karet"
                          : "Generovat PDF pro vybrané citace"}
                      </Typography>
                      <Field
                        name="templates"
                        validate={notEmpty}
                        render={({ field, form }: FieldProps<any>) => {
                          return (
                            <div className={classNames(classesSpacing.mt1)}>
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
                                  {variant === "cards"
                                    ? "Vygenerovat citace"
                                    : "Vygenerovat PDF"}
                                </Button>
                              </div>

                              <Select
                                form={form}
                                field={field}
                                loading={false}
                                label="Citační šablona"
                                options={options}
                                autoFocus={false}
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
  }
);
