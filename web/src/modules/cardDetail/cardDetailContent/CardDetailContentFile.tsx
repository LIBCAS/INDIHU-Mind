import MuiTypography from "@material-ui/core/Typography";
import React from "react";
import { FileItem } from "../../../components/file/FileItem";
import { CardContentProps, CardProps } from "../../../types/card";
import { FileProps } from "../../../types/file";
import { fileUpdate } from "../../attachments/_utils";
import { useStyles } from "../_cardDetailStyles";
import { updateCardContent } from "./_utils";

interface CardDetailContentFileProps {
  card: CardProps;
  setCard: React.Dispatch<React.SetStateAction<CardProps | undefined>>;
  currentCardContent: CardContentProps;
  setCardContents: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
  disabled?: boolean;
}

export const CardDetailContentFile: React.FC<CardDetailContentFileProps> = ({
  card,
  setCard,
  currentCardContent,
  setCardContents,
  disabled,
}) => {
  const classes = useStyles();

  const onDelete = (file: FileProps) => {
    const documentsFiltered = card.documents.filter(
      (f: FileProps) => f.id !== file.id
    );
    updateCardContent(
      "documents",
      documentsFiltered,
      setCard,
      currentCardContent,
      setCardContents
    );
    const newCards = file.linkedCards
      ? file.linkedCards.filter(
          (lc) =>
            JSON.parse(JSON.stringify(lc)).id !== currentCardContent.card.id
        )
      : [];
    fileUpdate({ ...file, linkedCards: newCards });
  };

  return (
    <div className={classes.columnsWrapper}>
      {(card.documents &&
        card.documents.length > 0 &&
        card.documents.map((file: FileProps) => (
          <FileItem
            key={file.id}
            file={file}
            onDelete={
              !disabled && currentCardContent.lastVersion ? onDelete : undefined
            }
          />
        ))) || (
        <MuiTypography variant="subtitle2">
          Tato karta nemá dokumenty
        </MuiTypography>
      )}
    </div>
  );
};
