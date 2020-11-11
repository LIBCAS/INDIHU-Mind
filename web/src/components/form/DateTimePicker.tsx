import React, { useRef, useEffect } from "react";
import InputLabel from "@material-ui/core/InputLabel";
import DateFnsUtils from "@date-io/date-fns";
import csLocale from "date-fns/locale/cs";
import {
  DatePicker as MaterialDatePicker,
  DateTimePicker as MaterialDateTimePicker,
  MuiPickersUtilsProvider,
} from "@material-ui/pickers";
import classNames from "classnames";

import { useStyles as useFormStyles } from "./_styles";
import { toDate } from "date-fns";

interface DateTimePickerProps {
  field: any;
  form: any;
  label?: string | JSX.Element;
  onChange?: any;
  autoFocus?: boolean;
  onClose?: () => void;
  onAccept?: (date: any) => void;
  dateOnly?: boolean;
}

export const DateTimePicker: React.FC<DateTimePickerProps> = ({
  label,
  field,
  form,
  onChange,
  autoFocus,
  onClose,
  onAccept,
  dateOnly = false,
}) => {
  const classesForm = useFormStyles();
  const inputRef = useRef<HTMLElement>(null);
  useEffect(() => {
    if (autoFocus && inputRef.current) {
      inputRef.current.click();
    }
  }, [autoFocus, inputRef]);
  const Component = dateOnly ? MaterialDatePicker : MaterialDateTimePicker;
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
      <MuiPickersUtilsProvider utils={DateFnsUtils} locale={csLocale}>
        <Component
          {...field}
          onChange={
            onChange
              ? onChange
              : (date) => {
                  form.setFieldValue(field.name, date ? toDate(date) : null);
                }
          }
          onAccept={onAccept}
          onClose={onClose}
          cancelLabel="Zrušit"
          format={dateOnly ? "dd. MM. yyyy" : "dd. MM. yyyy, HH:mm"}
          ampm={false}
          InputProps={{
            disableUnderline: true,
          }}
          inputRef={inputRef}
          inputProps={{
            name: field.name,
            id: field.name,
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
