import React from "react";
import ReactDOM from "react-dom";
import Fade from "@material-ui/core/Fade";
import LinearProgress from "@material-ui/core/LinearProgress";
import classNames from "classnames";

import { useStyles } from "./LoaderStyles";

interface LoaderProps {
  loading: boolean;
  local?: boolean;
  className?: any;
}

export const Loader: React.FC<LoaderProps> = ({
  loading,
  local,
  className
}) => {
  const classes = useStyles();
  return (
    <>
      {local ? (
        <Fade in={loading}>
          <LinearProgress
            className={classNames(classes.root, {
              [classes.local]: local,
              [className]: className
            })}
          />
        </Fade>
      ) : (
        ReactDOM.createPortal(
          <Fade in={loading}>
            <LinearProgress className={classes.root} />
          </Fade>,
          document.getElementById("root") as HTMLElement
        )
      )}
    </>
  );
};
