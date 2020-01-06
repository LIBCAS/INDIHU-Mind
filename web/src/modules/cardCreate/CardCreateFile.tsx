import React from "react";

import { FileProps } from "../../types/file";
import { FileItem } from "../../components/file/FileItem";

import { useStyles } from "./_cardCreateStyles";

interface CardCreateFileProps {
  formikBag: any;
}

export const CardCreateFile: React.FC<CardCreateFileProps> = ({
  formikBag
}) => {
  const classes = useStyles();
  const onDelete = (file: FileProps) => {
    const newFiles = formikBag.values.files.filter(
      (f: FileProps) => f.id !== file.id
    );
    formikBag.setFieldValue("files", newFiles);
  };
  return (
    <div className={classes.wrapperHalfItems}>
      {formikBag.values.files &&
        formikBag.values.files.map((file: FileProps) => (
          <FileItem
            key={file.id}
            file={file}
            onDelete={onDelete}
            disableDownload={file.providerType === "LOCAL"}
          />
        ))}
    </div>
  );
};
