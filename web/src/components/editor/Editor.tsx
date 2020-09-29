import React, { useState } from "react";
import { Editor as DraftEditor } from "react-draft-wysiwyg";
import "../../../node_modules/react-draft-wysiwyg/dist/react-draft-wysiwyg.css";

import { MessageSnackbar } from "../messages/MessageSnackbar";
import { useStyles } from "./_styles";

export interface EditorProps {
  value?: string;
  onChange?: (value?: string) => void;
  readOnly?: boolean;
}

export const Editor: React.FC<EditorProps> = ({
  value,
  onChange = () => {},
  ...props
}) => {
  const classes = useStyles();

  const [message, setMessage] = useState<boolean | string>(false);

  const createDefaultContentState = (value?: string) => {
    if (value) {
      try {
        return JSON.parse(value);
      } catch {
        setMessage("Nepodařilo se načíst text v editoru!");
      }
    }

    return {
      entityMap: {},
      blocks: [
        {
          key: "637gr",
          text: "",
          type: "unstyled",
          depth: 0,
          inlineStyleRanges: [],
          entityRanges: [],
          data: {}
        }
      ]
    };
  };

  const onContentStateChange = (contentState: any) => {
    try {
      onChange(JSON.stringify(contentState));
    } catch {
      setMessage("V editoru nastala chyba!");
    }
  };

  const { readOnly } = props;

  return (
    <div className={classes.editorWrapper}>
      {message && <MessageSnackbar setVisible={setMessage} message={message} />}
      <DraftEditor
        {...{
          ...props,
          defaultContentState: createDefaultContentState(value),
          onContentStateChange,
          editorClassName: classes.editor,
          toolbarClassName: readOnly ? classes.hidden : undefined,
          handlePastedText: () => false,
          toolbar: readOnly
            ? { options: [] }
            : {
                options: ["inline", "fontSize", "fontFamily", "history"],
                inline: {
                  options: ["bold", "italic", "underline"]
                }
              }
        }}
      />
    </div>
  );
};
