import React, { useState } from "react";
import classNames from "classnames";
import { compact } from "lodash";
import { Divider, TextField, Button } from "@material-ui/core";
import Edit from "@material-ui/icons/Edit";
import Delete from "@material-ui/icons/Delete";
import Tooltip from "@material-ui/core/Tooltip";

import { Timeline } from "../../components/timeline";
import { CardContentProps } from "../../types/card";
import { formatDateTime, formatMultiline } from "../../utils";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useFormStyles } from "../../components/form/_styles";
import { MessageSnackbar } from "../../components/messages/MessageSnackbar";
import { Loader } from "../../components/loader/Loader";
import { useStyles } from "./_cardDetailStyles";
import { createComment, updateComment, deleteComment } from "./_utils";

interface CommentEditProps {
  value?: string;
  onClose?: Function;
  onSubmit: (value: string) => void;
  disabled?: boolean;
}

interface CardDetailCommentsProps {
  card: CardContentProps;
}

const clearDateTime = (dateTime: string) => dateTime.replace(/\..*$/, "");

const CommentEdit: React.FC<CommentEditProps> = ({
  value = "",
  onClose,
  onSubmit,
  disabled,
}) => {
  const classesForm = useFormStyles();
  const classesLayout = useLayoutStyles();

  const [text, setText] = useState<string>(value);

  return (
    <div>
      <TextField
        type="textarea"
        placeholder="Napište komentář..."
        value={text}
        onChange={(e) => setText(e.target.value)}
        InputProps={{
          autoComplete: "off",
          disableUnderline: true,
        }}
        multiline={true}
        fullWidth={true}
        rows={3}
        inputProps={{
          className: classNames(classesForm.default, classesForm.textArea),
        }}
      />
      <div className={classNames(classesLayout.flex, classesLayout.justifyEnd)}>
        {compact([
          onClose && { label: "Zrušit", onClick: () => onClose() },
          {
            label: "Potvrdit",
            onClick: () => onSubmit(text),
            color: "primary" as "primary",
            disabled: disabled || !text,
          },
        ]).map(({ label, ...button }) => (
          <Button key={label} {...button}>
            {label}
          </Button>
        ))}
      </div>
    </div>
  );
};

export const CardDetailComments: React.FC<CardDetailCommentsProps> = ({
  card,
}) => {
  const classes = useStyles();
  const classesSpacing = useSpacingStyles();

  const [active, setActive] = useState<number | null>(null);
  const [comments, setComments] = useState(card.card.comments || []);
  const [message, setMessage] = useState<boolean | string>(false);
  const [loading, setLoading] = useState(false);

  const onClose = () => setActive(null);

  const onSubmit = async (text: string, index: number) => {
    setLoading(true);
    const isEdit = index < comments.length;
    const result = await (isEdit
      ? updateComment({ ...comments[index], text })
      : createComment(card.card.id, text));

    if (result) {
      setComments(
        isEdit
          ? comments.map((c) => (c.id === result.id ? { ...c, ...result } : c))
          : [...comments, result]
      );
      onClose();
    } else {
      setMessage(
        `Chyba: Nepodařilo se ${isEdit ? "upravit" : "přidat"} komentář.`
      );
    }
    setLoading(false);
  };

  const onDeleteComment = async (id: string) => {
    setLoading(true);
    const ok = await deleteComment(id);
    if (ok) {
      setComments(comments.filter((c) => c.id !== id));
    } else {
      setMessage(`Chyba: Nepodařilo se smazat komentář.`);
    }
    setLoading(false);
  };

  return (
    <div className={classesSpacing.mt3}>
      <Loader loading={loading} />
      {message && <MessageSnackbar setVisible={setMessage} message={message} />}
      <Divider />
      <div className={""}>
        <Timeline
          {...{
            items: [...comments, null],
            labelLeftMapper: (item) =>
              item ? formatDateTime(item.created) : "",
            labelRightMapper: (item, index) => {
              const key = `${item ? `${item.created}` : ""}-${index}`;
              return item ? (
                active === index ? (
                  <CommentEdit
                    {...{
                      key,
                      value: item.text,
                      onClose,
                      onSubmit: (value) => onSubmit(value, index),
                    }}
                  />
                ) : (
                  <div {...{ key, className: classes.commentWrapper }}>
                    {loading ? (
                      <></>
                    ) : (
                      <div className={classes.commentActions}>
                        {[
                          {
                            title: "Editovat",
                            Component: Edit,
                            onClick: () => setActive(index),
                          },
                          {
                            title: "Smazat",
                            Component: Delete,
                            onClick: () => onDeleteComment(item.id),
                          },
                        ].map(({ title, Component, onClick }) => (
                          <Tooltip key={title} title={title}>
                            <Component {...{ onClick }} />
                          </Tooltip>
                        ))}
                      </div>
                    )}
                    <div className={classes.commentInnerWrapper}>
                      {formatMultiline(item.text)}
                    </div>
                    {item.textUpdated &&
                    clearDateTime(item.created) !==
                      clearDateTime(item.textUpdated) ? (
                      <div className={classes.updatedWrapper}>
                        Upraveno {formatDateTime(item.textUpdated)}
                      </div>
                    ) : (
                      <></>
                    )}
                  </div>
                )
              ) : (
                <CommentEdit
                  {...{
                    key,
                    onSubmit: (value) => onSubmit(value, index),
                    disabled: loading,
                  }}
                />
              );
            },
          }}
        />
      </div>
    </div>
  );
};
