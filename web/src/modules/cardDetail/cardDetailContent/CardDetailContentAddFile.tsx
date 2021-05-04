import MuiIconButton from "@material-ui/core/IconButton";
import MuiTooltip from "@material-ui/core/Tooltip";
import MuiAddCircleIcon from "@material-ui/icons/AddCircle";
import { FormikProps } from "formik";
import React, { useContext, useState } from "react";
import { FileUploadPicker } from "../../../components/file/FileUploadPicker";
import { GlobalContext } from "../../../context/Context";
import {
  STATUS_ERROR_COUNT_CHANGE,
  STATUS_ERROR_TEXT_SET,
} from "../../../context/reducers/status";
import { CardContentProps, CardProps } from "../../../types/card";
import { FileProps } from "../../../types/file";
import { createErrorMessage, fileUpload } from "../../attachments/_utils";
import { onEditCard, updateCardContent } from "./_utils";

interface CardDetailContentAddFileProps {
  card: CardProps;
  setCard: React.Dispatch<React.SetStateAction<CardProps | undefined>>;
  currentCardContent: CardContentProps;
  setCardContents: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
  refreshCard: () => void;
  disabled?: boolean;
}

export const CardDetailContentAddFile: React.FC<CardDetailContentAddFileProps> = ({
  card,
  setCard,
  currentCardContent,
  setCardContents,
  refreshCard,
  disabled,
}) => {
  const context: any = useContext(GlobalContext);

  const dispatch: Function = context.dispatch;

  const [loading, setLoading] = useState(false);

  const onSubmit = async (
    values: FileProps,
    close: () => void,
    formikBag: FormikProps<FileProps>
  ) => {
    try {
      setLoading(true);

      const orderedDocuments = card.documents.map((f, i) => ({
        ...f,
        ordinalNumber: i + 1,
      }));

      values = { ...values, ordinalNumber: orderedDocuments.length };

      const value = [...orderedDocuments, values];

      const cardId = currentCardContent.card.id;

      if (values.id) {
        onEditCard(
          "files",
          value,
          card,
          setCard,
          currentCardContent,
          setCardContents,
          refreshCard
        );
      } else {
        const ok = await fileUpload({
          ...values,
          linkedCards: [...(values.linkedCards || []), cardId],
        });

        setLoading(false);

        if (ok) {
          dispatch({
            type: STATUS_ERROR_TEXT_SET,
            payload: `Soubor přidán`,
          });

          dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });

          updateCardContent(
            "documents",
            value,
            setCard,
            currentCardContent,
            setCardContents
          );

          close();
        } else {
          formikBag.setSubmitting(false);

          dispatch({
            type: STATUS_ERROR_TEXT_SET,
            payload: "Nepovedlo se přidat soubor ke kartě.",
          });

          dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
        }
      }
    } catch (e) {
      console.log(e);

      setLoading(false);

      formikBag.setSubmitting(false);

      if (e.response && e.response.error) {
        dispatch({
          type: STATUS_ERROR_TEXT_SET,
          payload: createErrorMessage(e),
        });
      }

      dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
    }
  };

  return (
    <FileUploadPicker
      onSubmit={onSubmit}
      loading={loading}
      enableExisting={true}
      position="top"
      ButtonComponent={({ onClick }) => (
        <MuiTooltip title="Přidat přílohu" arrow={true}>
          <MuiIconButton
            style={{ marginTop: "12px" }}
            color="primary"
            aria-label="add-attachment"
            component="span"
            onClick={onClick}
            disabled={disabled}
          >
            <MuiAddCircleIcon fontSize="default" />
          </MuiIconButton>
        </MuiTooltip>
      )}
    />
  );
};
