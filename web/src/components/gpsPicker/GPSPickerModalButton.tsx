import React, { useState } from "react";
import { get, noop, round } from "lodash";
import classNames from "classnames";
import LocationPicker from "react-location-picker";
import {
  Button,
  TextField,
  List,
  ListItem,
  LinearProgress,
} from "@material-ui/core";

import PlacesAutocomplete, {
  geocodeByAddress,
  getLatLng,
} from "react-places-autocomplete";

import { useStyles } from "./_styles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { ModalButton } from "../portal/ModalButton";

interface GPS {
  lat: number;
  lng: number;
}

interface GPSPickerModalButtonProps {
  latitude?: number;
  longitude?: number;
  onChange?: (value: string) => void;
  label?: string | JSX.Element;
  disabled?: boolean;
  error?: any;
  setError: (error: boolean) => void;
}

interface ContentProps {
  close: () => void;
}

const Content = ({
  latitude,
  longitude,
  onChange = noop,
  error,
  setError,
  disabled,
  close,
}: GPSPickerModalButtonProps & ContentProps) => {
  const classes = useStyles();
  const classesLayout = useLayoutStyles();
  const classesText = useTextStyles();
  const classesSpacing = useSpacingStyles();

  const [selectedPosition, setSelectedPosition] = useState(
    latitude && longitude
      ? {
          lat: latitude,
          lng: longitude,
        }
      : {
          lat: 50.086468,
          lng: 14.415776,
        }
  );
  const [searchText, setSearchText] = useState("");

  const handleSubmit = () => {
    if (error) {
      setError(false);
    }
    onChange(
      `${round(get(selectedPosition, "lat", 0), 4)},${round(
        get(selectedPosition, "lng", 0),
        4
      )}`
    );
    close();
  };

  const handleSelect = (address: string) => {
    geocodeByAddress(address)
      .then((results: any[]) => getLatLng(results[0]))
      .then((latLng: any) => setSelectedPosition(latLng))
      .catch((error: any) => console.error("Error", error));
    setSearchText("");
  };
  return (
    <div className={classes.modal}>
      {disabled ? (
        <></>
      ) : (
        <div
          className={classNames(
            classes.buttons,
            classesLayout.flex,
            classesLayout.spaceBetween,
            classesLayout.alignCenter
          )}
        >
          <div className={classNames(classesSpacing.mr2, classes.search)}>
            <PlacesAutocomplete
              {...{
                value: searchText,
                onChange: (value: string) => setSearchText(value),
                onSelect: handleSelect,
                debounce: 300,
              }}
            >
              {({
                getInputProps,
                getSuggestionItemProps,
                suggestions,
                loading,
              }: any) => (
                <div>
                  <TextField
                    {...getInputProps({
                      placeholder: "Hledat...",
                    })}
                  />
                  <LinearProgress
                    className={classes.progressBar}
                    style={{ display: loading ? "block" : "none" }}
                  />
                  <List className={classes.searchDropdown}>
                    {suggestions.map((suggestion: any, index: number) => {
                      return (
                        <ListItem
                          key={`${suggestion.description}-${index}`}
                          button
                          {...getSuggestionItemProps(suggestion)}
                        >
                          {suggestion.description}
                        </ListItem>
                      );
                    })}
                  </List>
                </div>
              )}
            </PlacesAutocomplete>
          </div>
          <div className={classNames(classesText.textBold)}>
            Místo na mapě zvolte přetažením červené značky na požadovanou
            pozici.
          </div>
          <Button color="primary" variant="contained" onClick={handleSubmit}>
            Potvrdit
          </Button>
        </div>
      )}
      <LocationPicker
        {...{
          defaultPosition: selectedPosition,
          containerElement: <div style={{ height: "100%" }} />,
          mapElement: (
            <div
              style={{
                height:
                  (window.innerWidth > 960 ? 0.85 : 0.9) * window.innerHeight -
                  (disabled ? 0 : 72),
              }}
            />
          ),
          onChange: ({ position }: { position: GPS }) =>
            setSelectedPosition(position),
        }}
      />
    </div>
  );
};

export const GPSPickerModalButton: React.FC<GPSPickerModalButtonProps> = (
  props
) => {
  const classes = useStyles();

  return (
    <div onClick={(e) => e.stopPropagation()}>
      <ModalButton
        label={props.disabled ? "Mapa" : "Zvolit na mapě"}
        size="small"
        variant="outlined"
        className={classes.button}
        modalProps={{ fullSize: true }}
        Content={Content}
        contentProps={props}
      />
    </div>
  );
};
