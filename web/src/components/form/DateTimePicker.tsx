import React, { useRef, useEffect } from "react";
import InputLabel from "@material-ui/core/InputLabel";
import moment from "moment";
import "moment/locale/cs";
import MomentUtils from "@date-io/moment";
import {
  DateTimePicker as MaterialDateTimePicker,
  MuiPickersUtilsProvider
} from "@material-ui/pickers";
import classNames from "classnames";

import { useStyles as useFormStyles } from "./_formStyles";

moment.locale("cs");

interface DateTimePickerProps {
  field: any;
  form: any;
  label?: string | JSX.Element;
  onChange?: any;
  autoFocus?: boolean;
  onClose?: () => void;
  onAccept?: (date: any) => void;
}

export const DateTimePicker: React.FC<DateTimePickerProps> = ({
  label,
  field,
  form,
  onChange,
  autoFocus,
  onClose,
  onAccept
}) => {
  const classesForm = useFormStyles();
  const inputRef = useRef<HTMLElement>(null);
  useEffect(() => {
    if (autoFocus && inputRef.current) {
      inputRef.current.click();
    }
  }, [autoFocus, inputRef]);
  return (
    <>
      {label && (
        <InputLabel
          className={classesForm.label}
          //
          // htmlFor={field.name}
        >
          {label}
        </InputLabel>
      )}
      <MuiPickersUtilsProvider utils={MomentUtils} locale={"cs"}>
        <MaterialDateTimePicker
          {...field}
          onChange={
            onChange
              ? onChange
              : date => {
                  form.setFieldValue(field.name, date ? date.toDate() : null);
                }
          }
          onAccept={onAccept}
          onClose={onClose}
          cancelLabel="Zrušit"
          format="DD. MM. YYYY, HH:mm"
          ampm={false}
          InputProps={{
            disableUnderline: true
          }}
          inputRef={inputRef}
          inputProps={{
            name: field.name,
            id: field.name
          }}
          className={classNames(
            classesForm.default,
            classesForm.dateTimePickerInput
          )}
        />
      </MuiPickersUtilsProvider>
    </>
  );
};
