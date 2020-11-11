import React, { useState } from "react";
import classNames from "classnames";
import IconButton from "@material-ui/core/IconButton";
import FileCopyOutlined from "@material-ui/icons/FileCopyOutlined";
import CopyToClipboard from "react-copy-to-clipboard";

import { CardProps } from "../../../types/card";

import { useStyles as useLayoutStyles } from "../../../theme/styles/layoutStyles";
import { useStyles } from "./_cardStyles";
import { MessageSnackbar } from "../../../components/messages/MessageSnackbar";

interface CardDetailContentIdProps {
  card: CardProps;
}

export const CardDetailContentId: React.FC<CardDetailContentIdProps> = ({
  card,
}) => {
  const classes = useStyles();
  const classesLayout = useLayoutStyles();

  const [message, setMessage] = useState<boolean>(false);

  const { id } = card;

  return (
    <>
      {message && (
        <MessageSnackbar setVisible={setMessage} message="Zkopírováno!" />
      )}
      <div
        className={classNames(
          classes.id,
          classesLayout.flex,
          classesLayout.alignCenter
        )}
      >
        <div style={{ marginRight: 2 }}>ID: {id}</div>
        <CopyToClipboard text={id}>
          <IconButton color="inherit" onClick={() => setMessage(true)}>
            <FileCopyOutlined color="inherit" fontSize="small" />
          </IconButton>
        </CopyToClipboard>
      </div>
    </>
  );
};
