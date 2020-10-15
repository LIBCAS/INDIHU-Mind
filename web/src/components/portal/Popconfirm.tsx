import React, { useRef, useState } from "react";
import Tooltip from "@material-ui/core/Tooltip";
import MaterialButton from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import classNames from "classnames";

import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";

import { Popover } from "./Popover";

interface PopconfirmProps {
  Button: any;
  confirmText: string;
  onConfirmClick: any;
  tooltip?: string;
  acceptText?: string;
  cancelText?: string;
}

export const Popconfirm: React.FC<PopconfirmProps> = ({
  Button,
  tooltip,
  confirmText,
  acceptText,
  cancelText,
  onConfirmClick
}) => {
  const classesLayout = useLayoutStyles();
  const [open, setOpen] = useState(false);
  const buttonRef = useRef(null);
  return (
    <>
      <span
        style={{ display: "flex", alignItems: "center" }}
        ref={buttonRef}
        onClick={e => {
          e.stopPropagation();
          setOpen(prev => !prev);
        }}
      >
        {tooltip ? (
          <Tooltip title={tooltip}>
            <div>
              <Button />
            </div>
          </Tooltip>
        ) : (
          <Button />
        )}
      </span>

      <Popover
        open={open}
        setOpen={setOpen}
        anchorEl={buttonRef.current}
        autoWidth
        content={
          <div style={{ padding: "5px" }}>
            <Typography
              style={{ marginTop: "5px" }}
              align="center"
              gutterBottom
            >
              {confirmText}
            </Typography>
            <div
              className={classNames(
                classesLayout.flex,
                classesLayout.justifyCenter,
                classesLayout.halfItems
              )}
            >
              <MaterialButton
                color="secondary"
                onClick={e => {
                  setOpen(false);
                  onConfirmClick(e);
                }}
                size="small"
              >
                {acceptText ? acceptText : "Smazat"}
              </MaterialButton>
              <MaterialButton
                onClick={e => {
                  e.stopPropagation();
                  setOpen(false);
                }}
                size="small"
              >
                {cancelText ? cancelText : "Zrušit"}
              </MaterialButton>
            </div>
          </div>
        }
      />
    </>
  );
};
