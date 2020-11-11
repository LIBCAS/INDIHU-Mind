import React, { useState } from "react";
import classNames from "classnames";
import { TextField, InputLabel } from "@material-ui/core";

import { useStyles } from "./_styles";
import { useStyles as useFormStyles } from "../form/_styles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { parseLatitudeLongitude } from "./_utils";
import { Error } from "../form/Error";
import { GPSPickerModalButton } from "./GPSPickerModalButton";

interface GPSPickerProps {
  value?: string;
  onChange?: (value: string) => void;
  label?: string | JSX.Element;
  disabled?: boolean;
  error?: any;
}

export const GPSPicker: React.FC<GPSPickerProps> = ({
  label,
  error: outterError,
  ...props
}) => {
  const classes = useStyles();
  const classesForm = useFormStyles();
  const classesLayout = useLayoutStyles();

  // selectedPosition moved out of state to stop re-rendering map on position of picker change
  const { latitude, longitude } = parseLatitudeLongitude(props.value);

  const [error, setError] = useState(false);

  return (
    <>
      {label && <InputLabel className={classesForm.label}>{label}</InputLabel>}
      <div>
        <div
          className={classNames(classesLayout.flex, classesLayout.alignCenter)}
        >
          <TextField
            type="text"
            fullWidth={true}
            InputProps={{
              autoComplete: "off",
              disableUnderline: true,
            }}
            inputProps={{
              className: classNames(classesForm.default, classes.textField),
            }}
            value={latitude && longitude ? `${latitude}, ${longitude}` : ""}
            disabled={true}
          />
          <GPSPickerModalButton
            {...{
              ...props,
              latitude,
              longitude,
              error,
              setError,
            }}
          />
        </div>
        <Error
          {...{
            show: error || outterError,
            error: error ? "Zadejte platné souřadnice." : outterError,
          }}
        />
      </div>
    </>
  );
};
