import React, { useState, useContext } from "react";
import { AutoClosingPopper } from "../portal/AutoClosingPopper";
import Button from "@material-ui/core/Button";
import { Field, FieldProps, Formik, FormikProps } from "formik";
import { OptionType } from "../select/_types";
import { Modal } from "../portal/Modal";
import { RecordsForm } from "../../modules/records/RecordsForm";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles } from "./_recordStyles";
import classNames from "classnames";
import { theme } from "../../theme/theme";
import { Select } from "../form/Select";
import { GlobalContext } from "../../context/Context";
import { RecordProps } from "../../types/record";

interface AddRecordProps {
  open: boolean;
  setOpen: Function;
  options: OptionType[];
  anchorEl: any;
  onSubmit: Function;
  afterEdit?: () => void;
}

export const AddRecordPopper: React.FC<AddRecordProps> = ({
  open,
  setOpen,
  options,
  anchorEl,
  onSubmit,
  afterEdit
}) => {
  const [formStage, setFormStage] = useState<"initial" | "add-existing">(
    "initial"
  );
  const [addRecordModalOpen, setAddRecordModalOpen] = useState(false);

  const classesLayout = useLayoutStyles();
  const classes = useStyles();

  const context: any = useContext(GlobalContext);

  const handleClose = () => {
    setOpen(false);
    setTimeout(() => setFormStage("initial"), 300);
  };
  return (
    <>
      <AutoClosingPopper
        open={open}
        setOpen={setOpen}
        onClickAwayCallback={handleClose}
        anchorEl={anchorEl}
        position="top"
        content={
          <div
            className={classNames(classes.recordPopperWrapper, {
              [classes.recordPopperExistingStage]: formStage === "add-existing"
            })}
          >
            {formStage === "initial" && (
              <>
                <Button
                  fullWidth
                  variant="outlined"
                  style={{ marginBottom: theme.spacing(2) }}
                  onClick={() => setFormStage("add-existing")}
                >
                  Přidat existujíci citaci
                </Button>
                <Button
                  fullWidth
                  variant="outlined"
                  onClick={() => {
                    handleClose();
                    setAddRecordModalOpen(true);
                  }}
                >
                  Přidat novou citaci
                </Button>
              </>
            )}
            {formStage === "add-existing" && (
              <Formik
                initialValues={{ records: [] }}
                onSubmit={() => {}}
                render={(formikBag: FormikProps<{ records: any }>) => (
                  <form
                    autoComplete="off"
                    onReset={formikBag.handleReset}
                    onSubmit={(e: any) => {
                      e.preventDefault();
                      e.stopPropagation();
                      if (formikBag.isSubmitting) return false;
                      const records = context.state.record.records.filter(
                        (rec: RecordProps) =>
                          formikBag.values.records.some(
                            (id: string) => id === rec.id
                          )
                      );
                      onSubmit(records);
                      handleClose();
                    }}
                  >
                    <Field
                      name="records"
                      render={({ field, form }: FieldProps<any>) => (
                        <>
                          <Select
                            form={form}
                            field={field}
                            loading={false}
                            isMulti={true}
                            label="Citace"
                            options={options}
                          />
                          <div
                            className={classNames(
                              classesLayout.flex,
                              classesLayout.spaceBetween
                            )}
                          >
                            <Button
                              style={{ marginTop: theme.spacing(1) }}
                              onClick={() => handleClose()}
                            >
                              Zrušit
                            </Button>
                            <Button
                              style={{ marginTop: theme.spacing(1) }}
                              color="primary"
                              type="submit"
                              disabled={!form.isValid}
                            >
                              Potvrdit
                            </Button>
                          </div>
                        </>
                      )}
                    />
                  </form>
                )}
              />
            )}
          </div>
        }
      />
      <Modal
        fullSize={true}
        open={addRecordModalOpen}
        setOpen={setAddRecordModalOpen}
        content={
          <RecordsForm
            setShowModal={setAddRecordModalOpen}
            redirect={false}
            onSubmitCallback={onSubmit}
            afterEdit={afterEdit}
          />
        }
      />
    </>
  );
};
