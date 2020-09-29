import React, { useRef, useEffect } from "react";
import InputLabel from "@material-ui/core/InputLabel";
import moment from "moment";
import "moment/locale/cs";
import MomentUtils from "@date-io/moment";
import {
  DatePicker as MaterialDatePicker,
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
  dateOnly = false
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
      <MuiPickersUtilsProvider utils={MomentUtils} locale={"cs"}>
        <Component
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
          format={dateOnly ? "DD. MM. YYYY" : "DD. MM. YYYY, HH:mm"}
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
