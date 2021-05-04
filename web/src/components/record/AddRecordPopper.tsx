import Button from "@material-ui/core/Button";
import classNames from "classnames";
import { Field, Formik, FormikProps } from "formik";
import React, { useState } from "react";
import { recordGet } from "../../context/actions/record";
import { RecordsForm } from "../../modules/records/RecordsForm";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { theme } from "../../theme/theme";
import { AsyncSelect } from "../form/AsyncSelect";
import { AutoClosingPopper } from "../portal/AutoClosingPopper";
import { Modal } from "../portal/Modal";
import { useStyles } from "./_recordStyles";

interface AddRecordProps {
  open: boolean;
  setOpen: Function;
  anchorEl: any;
  onSubmit: Function;
  afterEdit?: () => void;
}

export const AddRecordPopper: React.FC<AddRecordProps> = ({
  open,
  setOpen,
  anchorEl,
  onSubmit,
  afterEdit,
}) => {
  const [formStage, setFormStage] = useState<"initial" | "add-existing">(
    "initial"
  );
  const [addRecordModalOpen, setAddRecordModalOpen] = useState(false);

  const classesLayout = useLayoutStyles();
  const classes = useStyles();

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
              [classes.recordPopperExistingStage]: formStage === "add-existing",
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
                      onSubmit(formikBag.values.records);
                      handleClose();
                    }}
                  >
                    <Field
                      name="records"
                      label="Citace"
                      isMulti={true}
                      component={AsyncSelect}
                      loadOptions={async (text?: string) =>
                        await recordGet(text)
                      }
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
                      >
                        Potvrdit
                      </Button>
                    </div>
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
