import React, { useState, useEffect } from "react";
import ReactDOM from "react-dom";
import { ChromePicker } from "react-color";
import ClickAwayListener from "@material-ui/core/ClickAwayListener";
import InputLabel from "@material-ui/core/InputLabel";
import Button from "@material-ui/core/Button";
import Fade from "@material-ui/core/Fade";

import { useStyles } from "./_formStyles";

interface ColorPickerProps {
  onChange: Function;
  defaultColor?: string;
}

export const ColorPicker: React.FC<ColorPickerProps> = ({
  onChange,
  defaultColor
}) => {
  const classes = useStyles();
  const [show, setShow] = useState<boolean>(false);
  const [previousColor, setPreviousColor] = useState("");
  const [color, setColor] = useState();
  const randomColor = () =>
    "#" + ((Math.random() * (1 << 24)) | 0).toString(16);
  const setRandom = () => {
    const newColor = randomColor();
    setColor(newColor);
    setPreviousColor(newColor);
    onChange(newColor);
  };
  useEffect(() => {
    if (defaultColor) {
      setPreviousColor(defaultColor);
      setColor(defaultColor);
      onChange(defaultColor);
    } else {
      setRandom();
    }
  }, []);

  const handleClick = () => {
    setShow(prev => !prev);
    setColor(previousColor);
  };

  const handleChange = (color: any) => {
    setColor(color.hex);
  };

  const onSubmit = () => {
    setPreviousColor(color);
    onChange(color);
    setShow(false);
  };

  const onCancel = () => {
    setColor(previousColor);
    setShow(false);
  };

  return (
    <>
      <InputLabel className={classes.label}>Barva</InputLabel>
      <div className={classes.colorPickerInput} onClick={() => setShow(true)}>
        <div
          className={classes.colorPickerColor}
          style={{
            background: `${color}`
          }}
        />
      </div>
      {show
        ? ReactDOM.createPortal(
            <Fade in={show}>
              <div className={classes.chromePickerWrapper}>
                <div className={classes.chromePickerContent}>
                  <ClickAwayListener onClickAway={handleClick}>
                    <div>
                      <ChromePicker color={color} onChange={handleChange} />
                      <div className={classes.chromePickerActions}>
                        <div>
                          <Button color="primary" onClick={onSubmit}>
                            Zvolit barvu
                          </Button>
                        </div>
                        <div>
                          <Button color="secondary" onClick={onCancel}>
                            Zrušit
                          </Button>
                        </div>
                      </div>
                    </div>
                  </ClickAwayListener>
                </div>
              </div>
            </Fade>,
            document.getElementById("root") as HTMLElement
          )
        : null}
    </>
  );
};
