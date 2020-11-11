import React from "react";

import { CardContentProps } from "../../../types/card";
import { FileProps } from "../../../types/file";
import { FileItem } from "../../../components/file/FileItem";
import MuiTypography from "@material-ui/core/Typography";

import { useStyles } from "../_cardDetailStyles";
import { updateCardContent } from "./_utils";
import { fileUpdate } from "../../attachments/_utils";

interface CardDetailContentFileProps {
  card: CardContentProps;
  cardContent: CardContentProps[] | undefined;
  setCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
}

export const CardDetailContentFile: React.FC<CardDetailContentFileProps> = ({
  card,
  setCardContent,
}) => {
  const classes = useStyles();

  const onDelete = (file: FileProps) => {
    const documentsFiltered = card.card.documents.filter(
      (f: FileProps) => f.id !== file.id
    );
    updateCardContent("documents", documentsFiltered, card, setCardContent);
    const newCards = file.linkedCards
      ? file.linkedCards.filter(
          (lc) => JSON.parse(JSON.stringify(lc)).id !== card.card.id
        )
      : [];
    fileUpdate({ ...file, linkedCards: newCards });
  };

  return (
    <div className={classes.columnsWrapper}>
      {(card.card.documents &&
        card.card.documents.length > 0 &&
        card.card.documents.map((file: FileProps) => (
          <FileItem
            key={file.id}
            file={file}
            onDelete={card.lastVersion ? onDelete : undefined}
          />
        ))) || (
        <MuiTypography variant="subtitle2">
          Tato karta nemá dokumenty
        </MuiTypography>
      )}
    </div>
  );
};
