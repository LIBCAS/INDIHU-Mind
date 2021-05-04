import { Checkbox, InputLabel } from "@material-ui/core";
import Button from "@material-ui/core/Button";
import classNames from "classnames";
import { Field, FieldProps, FormikProps } from "formik";
import { get } from "lodash";
import React, { useState } from "react";
import { FileType } from "../../enums";
import { getFiles } from "../../modules/attachments/_utils";
import { getCards } from "../../modules/cards/_utils";
import { getRecords } from "../../modules/records/_utils";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import {
  FileDropboxProps,
  FileGoogleDriveActionProps,
  FileProps,
} from "../../types/file";
import { notEmpty, notLongerThan255 } from "../../utils/form/validate";
import { AsyncSelect } from "../asyncSelect";
import { DropboxChooser } from "../file/DropboxChooser";
import { GoogleDrivePicker } from "../file/GoogleDrivePicker";
import { Formik } from "../form/Formik";
import { InputFile } from "../form/InputFile";
import { InputText } from "../form/InputText";
import { Loader } from "../loader/Loader";
import { AutoClosingPopper } from "../portal/AutoClosingPopper";
import { useStyles } from "./_fileStyles";

interface FileUploadPopperInnerProps {
  open: boolean;
  setOpen: (open: boolean) => void;
  buttonRef: any;
}

export interface FileUploadPopperProps {
  onSubmit: (values: FileProps, close: () => void, formikBag: any) => void;
  loading?: boolean;
  enableExisting?: boolean;
  isNew?: boolean;
  position?: "bottom" | "top";
}

type Items = { id: string }[];

const defaultValues: FileProps = {
  id: "",
  name: "",
  type: "",
  link: "",
  ordinalNumber: 0,
  location: "WEB",
};

export const FileUploadPopper: React.FC<
  FileUploadPopperProps & FileUploadPopperInnerProps
> = ({
  onSubmit,
  loading = false,
  enableExisting = false,
  isNew = false,
  position = "bottom",
  open,
  setOpen,
  buttonRef,
}) => {
  const classes = useStyles();
  const classesSpacing = useSpacingStyles();
  const classesLayout = useLayoutStyles();

  const [initValues, setInitValues] = useState<FileProps>(defaultValues);
  const [existing, setExisting] = useState(false);
  const [linkedCards, setLinkedCards] = useState<Items>([]);
  const [records, setRecords] = useState<Items>([]);
  const [formStage, setFormStage] = useState<
    "upload-options" | "url-empty" | "file-selected"
  >("upload-options");

  const onDropboxSuccess = (files: FileDropboxProps[]) => {
    const file = files[0];
    setInitValues({
      id: "",
      providerId: file.id,
      providerType: FileType.DROPBOX,
      name: file.name.substr(0, file.name.lastIndexOf(".")),
      type: file.name.substr(file.name.lastIndexOf(".") + 1),
      link: file.link,
      ordinalNumber: 0,
      content: undefined,
    });
    setFormStage("file-selected");
  };
  const onGoogleDriveChange = (response: FileGoogleDriveActionProps) => {
    if (response.action === "picked" && response.docs.length > 0) {
      const file = response.docs[0];
      const isDriveDoc = file.type === "document";
      setInitValues({
        id: "",
        providerId: file.id,
        providerType: FileType.GOOGLE_DRIVE,
        name: isDriveDoc
          ? file.name
          : file.name.substr(0, file.name.lastIndexOf(".")),
        type: isDriveDoc
          ? file.type
          : file.name.substr(file.name.lastIndexOf(".") + 1),
        link: file.url,
        ordinalNumber: 0,
        content: undefined,
      });
      setFormStage("file-selected");
    }
  };
  const onUrlButtonClick = (formikBag: {
    setFieldValue: (arg0: string, arg1: FileType) => any;
  }) => {
    setTimeout(() => formikBag.setFieldValue("providerType", FileType.URL));
    setFormStage("url-empty");
  };
  const resetFormStage = () => {
    setTimeout(() => {
      setFormStage("upload-options");
      setExisting(false);
      setInitValues(defaultValues);
      setLinkedCards([]);
      setRecords([]);
    }, 300);
  };
  return (
    <AutoClosingPopper
      open={open}
      setOpen={setOpen}
      anchorEl={buttonRef.current}
      onClickAwayCallback={resetFormStage}
      position={position}
      content={
        <Formik
          initialValues={initValues}
          enableReinitialize
          onSubmit={() => {}}
          render={(formikBag: FormikProps<FileProps>) => (
            <form
              autoComplete="off"
              onReset={formikBag.handleReset}
              onSubmit={(e: any) => {
                e.preventDefault();
                e.stopPropagation();
                if (formikBag.isSubmitting) return false;
                onSubmit(
                  {
                    ...formikBag.values,
                    linkedCards: linkedCards.map((o) => o.id),
                    records: records.map((o) => o.id),
                  },
                  () => {
                    setOpen(false);
                    resetFormStage();
                  },
                  formikBag
                );
              }}
            >
              <Loader loading={loading} local />
              <div className={classes.fileUploadPickerFormWrapper}>
                {enableExisting && !existing && formStage === "upload-options" && (
                  <label
                    style={{ width: "100%" }}
                    htmlFor="raised-button-url-file-download"
                    className={classNames(classesSpacing.mt1)}
                  >
                    <Button
                      fullWidth
                      variant="outlined"
                      onClick={() => setExisting(true)}
                    >
                      Přidat existujíci soubor
                    </Button>
                  </label>
                )}
                {existing ? (
                  <>
                    <div className={classesSpacing.mt1} />
                    <AsyncSelect
                      value={
                        initValues && initValues.id ? initValues : undefined
                      }
                      onChange={(value) => {
                        formikBag.resetForm({ values: value });
                        setInitValues(value);
                      }}
                      loadOptions={async (text) =>
                        get(await getFiles(text), "items") || []
                      }
                    />
                  </>
                ) : (
                  <>
                    {formStage === "upload-options" && (
                      <>
                        <div
                          className={classNames(
                            classesSpacing.mt1,
                            classesLayout.flex,
                            classesLayout.directionColumn
                          )}
                        >
                          <InputFile
                            formikBag={formikBag}
                            setFormStage={setFormStage}
                          />
                          <label
                            style={{ width: "100%" }}
                            htmlFor="raised-button-url-file-download"
                            className={classNames(classesSpacing.mt1)}
                          >
                            <Button
                              fullWidth
                              variant="outlined"
                              onClick={() => onUrlButtonClick(formikBag)}
                            >
                              Stáhnout soubor z URL
                            </Button>
                          </label>
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
                              process.env
                                .REACT_APP_GOOGLE_DRIVE_CLIENT_ID as string
                            }
                            developerKey={
                              process.env
                                .REACT_APP_GOOGLE_DRIVE_API_KEY as string
                            }
                            onChange={onGoogleDriveChange}
                          />
                        </div>
                      </>
                    )}
                    {formStage === "url-empty" && (
                      <Field
                        name="link"
                        validate={notEmpty}
                        render={({ field, form }: FieldProps<FileProps>) => (
                          <InputText
                            field={{
                              ...field,
                              onChange: (e: React.ChangeEvent<any>) => {
                                const value = e.target.value;
                                if (
                                  formikBag.values.providerType ===
                                    FileType.URL &&
                                  value &&
                                  value.length
                                ) {
                                  const lastPart = value.replace(/^.*\//, "");
                                  formikBag.setFieldValue(
                                    "name",
                                    lastPart.replace(/\..*$/, "")
                                  );
                                  formikBag.setFieldValue(
                                    "type",
                                    /\..+/.test(lastPart)
                                      ? lastPart.replace(/^[^.]*\./, "")
                                      : ""
                                  );
                                }
                                field.onChange(e);
                              },
                            }}
                            form={form}
                            label="Odkaz"
                            disabled={
                              formikBag.values.providerType !== FileType.URL
                            }
                          />
                        )}
                      />
                    )}
                    {formStage === "file-selected" && (
                      <>
                        <Field
                          name="name"
                          validate={(value: any) =>
                            notLongerThan255(value) || notEmpty(value)
                          }
                          disabled={!formikBag.touched}
                          render={({ field, form }: FieldProps<FileProps>) => (
                            <InputText
                              field={field}
                              form={form}
                              label="Název"
                              inputProps={{ autoFocus: true }}
                              disabled={
                                (!form.values.content && !form.values.link) ||
                                formikBag.isSubmitting
                              }
                            />
                          )}
                        />
                        <Field
                          name="type"
                          validate={(value: any) =>
                            notLongerThan255(value) || notEmpty(value)
                          }
                          render={({ field, form }: FieldProps<FileProps>) => (
                            <InputText
                              field={field}
                              form={form}
                              label="Typ souboru"
                              disabled={true}
                            />
                          )}
                        />
                      </>
                    )}
                  </>
                )}
                {isNew && formStage === "file-selected" && (
                  <>
                    {[
                      {
                        label: "Karty",
                        value: linkedCards,
                        onChange: setLinkedCards,
                        loadOptions: getCards,
                      },
                      {
                        label: "Citace",
                        value: records,
                        onChange: setRecords,
                        loadOptions: getRecords,
                      },
                    ].map(({ onChange, loadOptions, ...field }) => (
                      <AsyncSelect
                        {...field}
                        key={field.label}
                        isMulti={true}
                        onChange={(value) => onChange(value || [])}
                        loadOptions={async (text?: string) =>
                          get(await loadOptions(text), "items") || []
                        }
                      />
                    ))}
                    <div className={classesSpacing.mb1} />
                  </>
                )}
                {formStage === "file-selected" &&
                  formikBag.values.providerType &&
                  formikBag.values.providerType === FileType.URL && (
                    <Field
                      name="location"
                      render={() => (
                        <div
                          className={classNames(
                            classesLayout.flex,
                            classesLayout.spaceBetween,
                            classesLayout.alignCenter
                          )}
                        >
                          <InputLabel>Stáhnout na server</InputLabel>
                          <Checkbox
                            color="primary"
                            value={formikBag.values.location === "SERVER"}
                            onChange={(event: any) =>
                              formikBag.setFieldValue(
                                "location",
                                event.target.checked ? "SERVER" : "WEB"
                              )
                            }
                          />
                        </div>
                      )}
                    />
                  )}
                <div
                  className={classNames(classes.fileUploadPickerActionWrapper, {
                    [classes.fileUploadPickerFirstStageButtonWrapper]:
                      formStage === "upload-options" && !existing,
                  })}
                >
                  {(formStage === "file-selected" || existing) && (
                    <Button
                      size="small"
                      color="primary"
                      type="submit"
                      disabled={
                        !formikBag.values ||
                        !formikBag.values.name ||
                        (!formikBag.values.id &&
                          !formikBag.values.content &&
                          !formikBag.values.link) ||
                        formikBag.isSubmitting ||
                        loading
                      }
                    >
                      Přidat soubor
                    </Button>
                  )}
                  {formStage === "url-empty" && (
                    <Button
                      size="small"
                      color="primary"
                      disabled={formikBag.values.link.length === 0}
                      onClick={() => setFormStage("file-selected")}
                    >
                      Načíst soubor z URL
                    </Button>
                  )}
                  <Button
                    size="small"
                    color="inherit"
                    onClick={() => {
                      setOpen(false);
                      resetFormStage();
                    }}
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
  );
};
