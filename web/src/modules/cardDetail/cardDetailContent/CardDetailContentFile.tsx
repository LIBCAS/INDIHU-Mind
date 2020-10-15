import React from "react";

import { CardContentProps } from "../../../types/card";
import { FileProps } from "../../../types/file";
import { FileItem } from "../../../components/file/FileItem";

import { useStyles } from "../_cardDetailStyles";
import { updateCardContent, fileDelete } from "./_utils";

interface CardDetailContentFileProps {
  card: CardContentProps;
  cardContent: CardContentProps[] | undefined;
  setCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
}

export const CardDetailContentFile: React.FC<CardDetailContentFileProps> = ({
  card,
  setCardContent
}) => {
  const classes = useStyles();
  const onDelete = (file: FileProps) => {
    const filesFiltered = card.card.files.filter(
      (f: FileProps) => f.id !== file.id
    );
    updateCardContent("files", filesFiltered, card, setCardContent);
    fileDelete(file.id);
  };
  return (
    <div className={classes.columnsWrapper}>
      {card.card.files &&
        card.card.files.map((file: FileProps) => (
          <FileItem
            key={file.id}
            file={file}
            onDelete={card.lastVersion ? onDelete : undefined}
          />
        ))}
    </div>
  );
};
