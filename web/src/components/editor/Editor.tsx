import React, { useState, useEffect, useCallback } from "react";
import { Editor as DraftEditor } from "react-draft-wysiwyg";
import "../../../node_modules/react-draft-wysiwyg/dist/react-draft-wysiwyg.css";

import { MessageSnackbar } from "../messages/MessageSnackbar";
import { useStyles } from "./_styles";
import classNames from "classnames";
import { EditorState, convertFromRaw, convertToRaw } from "draft-js";
import { debounce } from "lodash";
//import { imageUploadCallBack } from "./_utils";
import { translations } from "./_enums";

export interface EditorProps {
  value?: string;
  onChange?: (value?: string) => void;
  readOnly?: boolean;
  briefOptions?: boolean;
}

export const Editor: React.FC<EditorProps> = ({
  value,
  onChange = () => {},
  briefOptions = false,
  readOnly,
  ...props
}) => {
  const classes = useStyles();

  const [editorState, setEditorState] = useState(EditorState.createEmpty());

  useEffect(() => {
    setEditorState(
      value
        ? EditorState.createWithContent(convertFromRaw(JSON.parse(value)))
        : EditorState.createEmpty()
    );
  }, [value]);

  const [message, setMessage] = useState<boolean | string>(false);

  const onChangeDebounced = useCallback(debounce(onChange, 500), [onChange]);

  function onEditorStateChange(newEditorState: any) {
    setEditorState(newEditorState);
    try {
      onChangeDebounced(
        JSON.stringify(convertToRaw(newEditorState.getCurrentContent()))
      );
    } catch {
      setMessage("V editoru nastala chyba!");
    }
  }

  return (
    <div className={classes.editorWrapper}>
      {message && <MessageSnackbar setVisible={setMessage} message={message} />}
      <DraftEditor
        {...{
          ...props,
          editorState: editorState,
          onEditorStateChange: onEditorStateChange,
          editorClassName: classNames(classes.editor, {
            [classes.editorActive]: !readOnly,
          }),
          toolbarClassName: classNames(
            classes.toolbar,
            readOnly ? classes.hidden : undefined
          ),
          handlePastedText: () => false,
          localization: {
            translations,
          },
          toolbar: readOnly
            ? { options: [] }
            : {
                options: briefOptions
                  ? ["inline", "fontSize", "fontFamily", "history"]
                  : undefined,
                inline: briefOptions
                  ? {
                      options: ["bold", "italic", "underline"],
                    }
                  : undefined,
                // image: {
                //   uploadEnabled: true,
                //   uploadCallback: imageUploadCallBack,
                //   previewImage: true,
                // },
              },
        }}
      />
    </div>
  );
};
