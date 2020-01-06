import React, { useRef, useState, useContext } from "react";
import Add from "@material-ui/icons/Add";
import classNames from "classnames";
import { FormikProps, Field, FieldProps, FormikActions } from "formik";
import Button from "@material-ui/core/Button";
import uuid from "uuid/v4";

import { GlobalContext } from "../../../context/Context";
import {
  STATUS_ERROR_COUNT_CHANGE,
  STATUS_ERROR_TEXT_SET
} from "../../../context/reducers/status";
import { Formik } from "../../../components/form/Formik";
import { notEmpty } from "../../../utils/form/validate";
import {
  FileProps,
  FileDropboxProps,
  FileGoogleDriveActionProps
} from "../../../types/file";
import { translateFileError, FileErrors } from "../../../utils/card";
import { Loader } from "../../../components/loader/Loader";
import { InputFile } from "../../../components/form/InputFile";
import { CardContentProps } from "../../../types/card";
import { InputText } from "../../../components/form/InputText";
import { DropboxChooser } from "../../../components/file/DropboxChooser";
import { GoogleDrivePicker } from "../../../components/file/GoogleDrivePicker";
import { ButtonGrey } from "../../../components/control/ButtonGrey";
import { Popover } from "../../../components/portal/Popover";

import { useStyles as useLayoutStyles } from "../../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../../theme/styles/spacingStyles";

import { updateCardContent, fileUpload } from "./_utils";
import { useStyles } from "./_cardStyles";

interface CardDetailContentAddFileProps {
  card: CardContentProps;
  cardContent: CardContentProps[] | undefined;
  setCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
}

const defaultValues: FileProps = {
  id: "",
  name: "",
  type: "",
  link: "",
  ordinalNumber: 0
};

export const CardDetailContentAddFile: React.FC<
  CardDetailContentAddFileProps
> = ({ card, setCardContent }) => {
  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;
  const classes = useStyles();
  const classesSpacing = useSpacingStyles();
  const classesLayout = useLayoutStyles();
  const AddAttributeRef = useRef(null);

  const [initValues, setInitValues] = useState<FileProps>(defaultValues);
  const [popoverOpen, setPopoverOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const onAddFile = () => {
    setInitValues(defaultValues);
    setPopoverOpen(prev => !prev);
  };
  const onDropboxSuccess = (files: FileDropboxProps[]) => {
    files.forEach(file => {
      setInitValues({
        id: "",
        providerId: file.id,
        providerType: "DROPBOX",
        name: file.name.substr(0, file.name.lastIndexOf(".")),
        type: file.name.substr(file.name.lastIndexOf(".") + 1),
        link: file.link,
        ordinalNumber: 0,
        content: undefined
      });
    });
  };
  const onGoogleDriveChange = (response: FileGoogleDriveActionProps) => {
    if (response.action === "picked" && response.docs.length > 0) {
      const file = response.docs[0];
      const isDriveDoc = file.type === "document";
      setInitValues({
        id: "",
        providerId: file.id,
        providerType: "GOOGLE_DRIVE",
        name: isDriveDoc
          ? file.name
          : file.name.substr(0, file.name.lastIndexOf(".")),
        type: isDriveDoc
          ? file.type
          : file.name.substr(file.name.lastIndexOf(".") + 1),
        link: file.url,
        ordinalNumber: 0,
        content: undefined
      });
    }
  };
  const onSubmit = (values: FileProps, actions: FormikActions<FileProps>) => {
    setLoading(true);
    const orderedFiles = card.card.files.map((f, i) => ({
      ...f,
      ordinalNumber: i
    }));
    values = { ...values, ordinalNumber: orderedFiles.length };
    const value = [...orderedFiles, values];
    fileUpload(values, card.card.id)
      .then(res => {
        setLoading(false);
        dispatch({
          type: STATUS_ERROR_TEXT_SET,
          payload: `Soubor přidán`
        });
        dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
        updateCardContent("files", value, card, setCardContent);
        setPopoverOpen(false);
      })
      .catch((err: any) => {
        setLoading(false);
        actions.setSubmitting(false);
        if (err.response && err.response.errorType) {
          const translated = `${translateFileError(err.response
            .errorType as FileErrors)}: ${
            err.response.errorType == "FILE_TOO_BIG" ? `${values.name} ` : ""
          }${err.response.errorMessage}`;
          dispatch({
            type: STATUS_ERROR_TEXT_SET,
            payload: translated
          });
        }
        dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
      });
  };
  return (
    <>
      <div ref={AddAttributeRef} className={classes.addWrapper}>
        <ButtonGrey
          text="Přidat přílohu"
          onClick={onAddFile}
          bold
          Icon={<Add fontSize="small" />}
        />
        <Popover
          open={popoverOpen}
          setOpen={setPopoverOpen}
          anchorEl={AddAttributeRef.current}
          content={
            <Formik
              initialValues={initValues}
              enableReinitialize
              onSubmit={onSubmit}
              render={(formikBag: FormikProps<FileProps>) => (
                <form
                  onReset={formikBag.handleReset}
                  onSubmit={(e: any) => {
                    e.preventDefault();
                    e.stopPropagation();
                    if (formikBag.isSubmitting) return false;
                    formikBag.setFieldValue("id", uuid());
                    formikBag.submitForm();
                  }}
                >
                  <Loader loading={loading} local />
                  <div className={classes.attributeWrapper}>
                    <Field
                      name="name"
                      validate={notEmpty}
                      render={({ field, form }: FieldProps<FileProps>) => (
                        <InputText
                          field={field}
                          form={form}
                          label="Název"
                          inputProps={{ autoFocus: true }}
                        />
                      )}
                    />
                    {formikBag.values.providerType !== "LOCAL" && (
                      <Field
                        name="link"
                        validate={notEmpty}
                        render={({ field, form }: FieldProps<FileProps>) => (
                          <InputText field={field} form={form} label="Odkaz" />
                        )}
                      />
                    )}

                    <Field
                      name="type"
                      validate={notEmpty}
                      render={({ field, form }: FieldProps<FileProps>) => (
                        <InputText
                          field={field}
                          form={form}
                          label="Typ souboru"
                        />
                      )}
                    />
                    <div
                      className={classNames(
                        classesSpacing.mt1,
                        classesLayout.flex
                      )}
                    >
                      <InputFile formikBag={formikBag} />
                    </div>
                    <div
                      className={classNames(
                        classesSpacing.mt1,
                        classesLayout.flex,
                        classesLayout.spaceBetween
                      )}
                    >
                      <DropboxChooser
                        appKey={process.env.REACT_APP_DROPBOX_KEY as string}
                        success={onDropboxSuccess}
                      />
                      <GoogleDrivePicker
                        clientId={
                          process.env.REACT_APP_GOOGLE_DRIVE_CLIENT_ID as string
                        }
                        developerKey={
                          process.env.REACT_APP_GOOGLE_DRIVE_API_KEY as string
                        }
                        onChange={onGoogleDriveChange}
                      />
                    </div>
                    <div className={classes.actionWrapper}>
                      <Button size="small" color="primary" type="submit">
                        Přidat soubor
                      </Button>
                      <Button
                        size="small"
                        color="inherit"
                        onClick={() => setPopoverOpen(false)}
                      >
                        Zrušit
                      </Button>
                    </div>
                  </div>
                </form>
              )}
            />
          }
        />
      </div>
    </>
  );
};
