import React from "react";
import MaterialButton from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import classNames from "classnames";

import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";

import { Popover } from "./Popover";
import { theme } from "../../theme/theme";

interface PopoverconfirmProps {
  ref: React.MutableRefObject<any>;
  confirmText: string | JSX.Element;
  onConfirmClick: any;
  acceptText?: string;
  cancelText?: string;
  open: boolean;
  setOpen: Function;
}

export const Popoverconfirm: React.FC<PopoverconfirmProps> = ({
  ref,
  confirmText,
  acceptText,
  cancelText,
  onConfirmClick,
  setOpen,
  ...rest
}) => {
  const classesLayout = useLayoutStyles();

  return (
    <Popover
      {...rest}
      setOpen={setOpen}
      anchorEl={ref ? ref.current : undefined}
      autoWidth
      anchorReference={"anchorPosition"} //position set to center of screen
      content={
        <div style={{ padding: theme.spacing(1) }}>
          <Typography style={{ margin: "5px" }} align="center" gutterBottom>
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
              onClick={(e) => {
                e.stopPropagation();
                setOpen(false);
              }}
              size="small"
            >
              {cancelText ? cancelText : "Zrušit"}
            </MaterialButton>
            <MaterialButton
              color="primary"
              onClick={(e) => {
                setOpen(false);
                onConfirmClick(e);
              }}
              size="small"
            >
              {acceptText ? acceptText : "Smazat"}
            </MaterialButton>
          </div>
        </div>
      }
    />
  );
};
