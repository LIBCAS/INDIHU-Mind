import React from "react";

import { FileProps } from "../../types/file";
import { FileItem } from "../../components/file/FileItem";

import { useStyles } from "./_cardCreateStyles";
import { FileType } from "../../enums";

interface CardCreateFileProps {
  formikBag: any;
}

export const CardCreateFile: React.FC<CardCreateFileProps> = ({
  formikBag
}) => {
  const classes = useStyles();
  const onDelete = (file: FileProps) => {
    const newDocuments = formikBag.values.documents.filter(
      (f: FileProps) => f.id !== file.id
    );
    formikBag.setFieldValue("documents", newDocuments);
  };
  return (
    <div className={classes.fileItemsContainer}>
      {formikBag.values.documents &&
        formikBag.values.documents.map((file: FileProps) => (
          <div key={file.id} className={classes.fileItemWrapper}>
            <FileItem
              file={file}
              onDelete={onDelete}
              disableDownload={file.providerType === FileType.LOCAL}
            />
          </div>
        ))}
    </div>
  );
};
