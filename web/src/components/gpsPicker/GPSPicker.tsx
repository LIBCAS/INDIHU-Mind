import React, { useState, useEffect } from "react";
import { get, noop, round } from "lodash";
import classNames from "classnames";
// @ts-ignore
import LocationPicker from "react-location-picker";
import { Button, TextField } from "@material-ui/core";

import { useStyles } from "./_styles";
import { useStyles as useFormStyles } from "../form/_formStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { parseLatitudeLongitude } from "./_utils";
import { ModalButton } from "../portal/ModalButton";
import { usePrevious } from "../../hooks/usePrevious";

interface GPS {
  lat: number;
  lng: number;
}

interface GPSPickerProps {
  value?: string;
  onChange?: (value: string) => void;
}

export const GPSPicker: React.FC<GPSPickerProps> = ({
  value,
  onChange = noop
}) => {
  const classes = useStyles();
  const classesForm = useFormStyles();
  const classesLayout = useLayoutStyles();

  const [selectedPosition, setSelectedPosition] = useState({
    lat: 49.190782,
    lng: 16.612511
  });
  const [locationPickerKey, setLocationPickerKey] = useState(false);

  const prevValue = usePrevious(value);

  useEffect(() => {
    if (value !== prevValue) {
      const { latitude, longitude } = parseLatitudeLongitude(value);

      if (latitude !== undefined && longitude !== undefined) {
        setSelectedPosition({ lat: latitude, lng: longitude });
        setLocationPickerKey(!locationPickerKey);
      }
    }
  }, [value]);

  return (
    <div className={classNames(classesLayout.flex, classesLayout.alignCenter)}>
      <TextField
        type="text"
        fullWidth={true}
        InputProps={{
          autoComplete: "off",
          disableUnderline: true
        }}
        inputProps={{
          className: classesForm.default
        }}
        value={value}
        onChange={e => onChange(e.target.value)}
      />
      <ModalButton
        label="Vybrat"
        size="small"
        variant="outlined"
        className={classes.button}
        modalProps={{ fullSize: true }}
        Content={({ close }: { close: () => void }) => {
          const handleSubmit = () => {
            onChange(
              `${round(get(selectedPosition, "lat", 0), 4)}, ${round(
                get(selectedPosition, "lng", 0),
                4
              )}`
            );
            close();
          };

          return (
            <div className={classes.modal}>
              <LocationPicker
                {...{
                  key: `location-picker-${locationPickerKey}`,
                  defaultPosition: selectedPosition,
                  containerElement: <div style={{ height: "100%" }} />,
                  mapElement: (
                    <div
                      style={{
                        height:
                          (window.innerWidth > 960 ? 0.85 : 0.9) *
                            window.innerHeight -
                          72
                      }}
                    />
                  ),
                  onChange: ({ position }: { position: GPS }) =>
                    setSelectedPosition(position)
                }}
              />
              <div
                className={classNames(
                  classes.buttons,
                  classesLayout.flex,
                  classesLayout.justifyEnd,
                  classesLayout.alignCenter
                )}
              >
                <Button
                  color="primary"
                  variant="contained"
                  onClick={handleSubmit}
                >
                  Potvrdit
                </Button>
              </div>
            </div>
          );
        }}
      />
    </div>
  );
};
