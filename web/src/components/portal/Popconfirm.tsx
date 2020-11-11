import React, { useRef, useState } from "react";
import MuiTooltip from "@material-ui/core/Tooltip";

import { Popoverconfirm } from "./Popoverconfirm";

interface PopconfirmProps {
  Button: React.ReactNode;
  confirmText: string;
  onConfirmClick: any;
  tooltip?: string;
  acceptText?: string;
  cancelText?: string;
  onOpenCallback?: Function;
}

export const Popconfirm: React.FC<PopconfirmProps> = ({
  Button,
  tooltip,
  confirmText,
  acceptText,
  cancelText,
  onConfirmClick,
  onOpenCallback,
}) => {
  const [open, setOpen] = useState(false);

  const ref = useRef(null);

  return (
    <React.Fragment>
      <span
        style={{ display: "flex", alignItems: "center" }}
        ref={ref}
        onClick={(e) => {
          e.stopPropagation();
          setOpen((prev) => !prev);
          onOpenCallback && onOpenCallback();
        }}
      >
        {(tooltip && (
          <MuiTooltip title={tooltip}>
            <div>{Button}</div>
          </MuiTooltip>
        )) || <React.Fragment>{Button}</React.Fragment>}
      </span>

      <Popoverconfirm
        {...{
          ref,
          open,
          setOpen,
          confirmText,
          acceptText,
          cancelText,
          onConfirmClick,
        }}
      />
    </React.Fragment>
  );
};
