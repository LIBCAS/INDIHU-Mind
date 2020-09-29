import React, { useState, useRef } from "react";
import { FormikProps } from "formik";
import { get } from "lodash";
import Typography from "@material-ui/core/Typography";
import Button from "@material-ui/core/Button";
import classNames from "classnames";

import { Attachment, AttachmentEditProps } from "./_types";
import { Formik } from "../../components/form/Formik";
import { MessageSnackbar } from "../../components/messages/MessageSnackbar";
import { Loader } from "../../components/loader/Loader";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { fileUpdate } from "./_utils";
import { AsyncSelect } from "../../components/asyncSelect";
import { Popoverconfirm } from "../../components/portal/Popoverconfirm";

interface AttachmentCardEditProps {
  field: string;
  text: string;
  loadOptions: (text?: string) => Promise<any[]>;
  attachment: Attachment;
  close: () => void;
  submitCheck?: (
    values: AttachmentEditProps,
    options: any[],
    setConfirmData: Function
  ) => boolean;
}

export const AttachmentCardEdit: React.FC<AttachmentCardEditProps> = ({
  field,
  text,
  loadOptions,
  attachment,
  close,
  submitCheck = () => true
}) => {
  const classesSpacing = useSpacingStyles();
  const classesLayout = useLayoutStyles();

  const ref = useRef(null);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<boolean>(false);
  const [confirmData, setConfirmData] = useState<{
    confirmText: string | JSX.Element;
    values: AttachmentEditProps;
  } | null>(null);
  const [items, setItems] = useState<string[]>(get(attachment, field, []));
  const [options, setOptions] = useState<any[]>([]);

  const submitFunction = async (values: AttachmentEditProps) => {
    if (loading) return false;
    setLoading(true);
    const ok = await fileUpdate(values);
    setLoading(false);
    setError(!ok);
    if (ok) {
      close();
    }
  };

  const onSubmit = async (values: AttachmentEditProps) => {
    if (submitCheck(values, options, setConfirmData)) {
      submitFunction(values);
    }
  };

  return (
    <>
      <Loader loading={loading} />
      {error && (
        <MessageSnackbar
          setVisible={setError}
          message={`Nepodařilo se upravit ${text} dokumentu.`}
        />
      )}
      {confirmData && (
        <Popoverconfirm
          ref={ref}
          open={true}
          setOpen={() => setConfirmData(null)}
          onConfirmClick={() => submitFunction(confirmData.values)}
          acceptText="Potvrdit"
          confirmText={confirmData.confirmText}
        />
      )}
      <Formik
        initialValues={{
          ...attachment,
          [`${field}`]: items
        }}
        enableReinitialize
        onSubmit={onSubmit}
        render={(formikBag: FormikProps<AttachmentEditProps>) => (
          <form
            onReset={formikBag.handleReset}
            onSubmit={async (e: any) => {
              e.preventDefault();
              e.stopPropagation();
              if (formikBag.isSubmitting) return false;
              formikBag.submitForm();
            }}
          >
            <div
              className={classNames(
                classesLayout.flex,
                classesLayout.flexWrap,
                classesLayout.directionColumn,
                classesSpacing.m1
              )}
            >
              <Typography
                variant="h5"
                color="inherit"
                align="center"
                gutterBottom
              >
                Upravit {text}
              </Typography>
              <div style={{ marginTop: "20px" }}>
                <AsyncSelect
                  value={items}
                  onChange={value => setItems(value)}
                  loadOptions={async (text, options) => {
                    const newOptions = get(
                      await loadOptions(text),
                      "items",
                      []
                    );
                    setOptions([...options, ...newOptions]);
                    return (
                      newOptions.sort((first: any, second: any) => {
                        const a = first.name.toLowerCase();
                        const b = second.name.toLowerCase();
                        return a > b ? 1 : a < b ? -1 : 0;
                      }) || []
                    );
                  }}
                  isMulti={true}
                />
                <div className={classesSpacing.mb1} />
              </div>
              <Button
                ref={ref}
                className={classesSpacing.mt3}
                variant="contained"
                color="primary"
                type="submit"
                disabled={loading}
              >
                Potvrdit
              </Button>
            </div>
          </form>
        )}
      />
    </>
  );
};
