import React, { useRef, useState } from "react";

import { useStyles } from "./_fileStyles";
import { FileUploadPopper, FileUploadPopperProps } from "./FileUploadPopper";

interface FileUploadPickerProps extends FileUploadPopperProps {
  ButtonComponent: ({ onClick }: { onClick: () => void }) => JSX.Element;
}

export const FileUploadPicker: React.FC<FileUploadPickerProps> = ({
  ButtonComponent,
  ...props
}) => {
  const classes = useStyles();

  const buttonRef = useRef(null);

  const [open, setOpen] = useState(false);

  return (
    <div ref={buttonRef} className={classes.fileUploadPickerWrapper}>
      <ButtonComponent onClick={() => setOpen(true)} />
      <FileUploadPopper {...{ ...props, buttonRef, open, setOpen }} />
    </div>
  );
};
