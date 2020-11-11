import React from "react";
import Typography from "@material-ui/core/Typography";
import Add from "@material-ui/icons/Add";

import { ButtonGrey } from "../../components/control/ButtonGrey";
import { FileUploadPicker } from "../../components/file/FileUploadPicker";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useStylesText } from "../../theme/styles/textStyles";
import { FileProps } from "../../types/file";
import { CardCreateFile } from "./CardCreateFile";

interface CardCreateAddFileProps {
  formikBag: any;
}

export const CardCreateAddFile: React.FC<CardCreateAddFileProps> = ({
  formikBag,
}) => {
  const classesText = useStylesText();
  const classesSpacing = useSpacingStyles();

  const onSubmit = (values: FileProps, close: () => void) => {
    const documents: FileProps[] | undefined = formikBag.values.documents;
    if (documents) {
      const orderedDocuments = documents.map((f, i) => ({
        ...f,
        ordinalNumber: i,
      }));
      values = { ...values, ordinalNumber: orderedDocuments.length };
      formikBag.setFieldValue("documents", [...orderedDocuments, values]);
    } else {
      formikBag.setFieldValue("documents", [values]);
    }
    close();
  };

  return (
    <>
      <div className={classesSpacing.mt2} />
      <Typography className={classesText.subtitle}>Soubory</Typography>
      <div>
        <CardCreateFile formikBag={formikBag} />
      </div>
      <FileUploadPicker
        onSubmit={onSubmit}
        enableExisting={true}
        position={"top"}
        ButtonComponent={({ onClick }) => (
          <ButtonGrey
            text="Přidat přílohu"
            onClick={onClick}
            bold
            Icon={<Add fontSize="small" />}
            style={{ marginTop: 12 }}
          />
        )}
      />
    </>
  );
};
