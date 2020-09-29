import React, { useState, useContext } from "react";
import { FormikProps } from "formik";
import MuiTooltip from "@material-ui/core/Tooltip";
import MuiIconButton from "@material-ui/core/IconButton";
import MuiAddCircleIcon from "@material-ui/icons/AddCircle";

import { GlobalContext } from "../../../context/Context";
import {
  STATUS_ERROR_COUNT_CHANGE,
  STATUS_ERROR_TEXT_SET
} from "../../../context/reducers/status";
import { FileProps } from "../../../types/file";
import { CardContentProps } from "../../../types/card";
import { updateCardContent } from "./_utils";
import {
  fileUpload,
  fileUpdate,
  createErrorMessage
} from "../../attachments/_utils";
import { FileUploadPicker } from "../../../components/file/FileUploadPicker";

interface CardDetailContentAddFileProps {
  card: CardContentProps;
  cardContent: CardContentProps[] | undefined;
  setCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >;
}

export const CardDetailContentAddFile: React.FC<CardDetailContentAddFileProps> = ({
  card,
  setCardContent
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

      const orderedDocuments = card.card.documents.map((f, i) => ({
        ...f,
        ordinalNumber: i + 1
      }));

      values = { ...values, ordinalNumber: orderedDocuments.length };

      const value = [...orderedDocuments, values];

      const cardId = card.card.id;

      const func = values.id ? fileUpdate : fileUpload;

      const ok = await func({
        ...values,
        linkedCards: [...(values.linkedCards || []), cardId]
      });

      setLoading(false);

      if (ok) {
        dispatch({
          type: STATUS_ERROR_TEXT_SET,
          payload: `Soubor přidán`
        });

        dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });

        updateCardContent("documents", value, card, setCardContent);

        close();
      } else {
        formikBag.setSubmitting(false);

        dispatch({
          type: STATUS_ERROR_TEXT_SET,
          payload: "Nepovedlo se přidat soubor ke kartě."
        });

        dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
      }
    } catch (e) {
      console.log(e);

      setLoading(false);

      formikBag.setSubmitting(false);

      if (e.response && e.response.errorType) {
        dispatch({
          type: STATUS_ERROR_TEXT_SET,
          payload: createErrorMessage(e)
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
          >
            <MuiAddCircleIcon fontSize="default" />
          </MuiIconButton>
        </MuiTooltip>
      )}
    />
  );
};
