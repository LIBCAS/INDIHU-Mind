import React, { useRef, useState } from "react";
import { filter, find, uniqBy } from "lodash";
import MuiIconButton from "@material-ui/core/IconButton";
import MuiMoreVertIcon from "@material-ui/icons/MoreVert";
import MuiMenuItem from "@material-ui/core/MenuItem";
import MuiMenu from "@material-ui/core/Menu";

import { fileDelete } from "./_utils";
import { Attachment, AttachmentEditProps } from "./_types";
import { Popoverconfirm } from "../../components/portal/Popoverconfirm";
import { Modal } from "../../components/portal/Modal";
import { AttachmentCardRename } from "./AttachmentCardRename";
import { AttachmentCardEdit } from "./AttachmentCardEdit";
import { downloadFile } from "../../components/file/_utils";
import { getCards } from "../cards/_utils";
import { getRecords } from "../records/_utils";

interface AttachmentCardMenuProps {
  attachment: Attachment;
  update: () => void;
}

const mapText = (value: { name: string }[]) =>
  value.map(({ name }, i) => (
    <span key={name}>
      {i ? ", " : ""}
      <span style={{ fontWeight: "bold" }}>{name}</span>
    </span>
  ));

export const AttachmentCardMenu: React.FC<AttachmentCardMenuProps> = ({
  attachment,
  update
}) => {
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [renameOpen, setRenameOpen] = useState(false);
  const [editCardsOpen, setEditCardsOpen] = useState(false);
  const [editRecordsOpen, setEditRecordsOpen] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);

  const ref = useRef(null);

  const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const close = () => {
    setAnchorEl(null);
  };

  const handleDownload = () => {
    downloadFile(attachment);
    close();
  };

  const handleDelete = async () => {
    if (await fileDelete(attachment.id)) {
      update();
      close();
    }
  };

  return (
    <React.Fragment>
      <div ref={ref}>
        <MuiIconButton onClick={handleClick} aria-label="actions">
          <MuiMoreVertIcon />
        </MuiIconButton>
      </div>
      <MuiMenu
        id="simple-menu"
        anchorEl={anchorEl}
        keepMounted
        open={Boolean(anchorEl)}
        onClose={close}
      >
        <MuiMenuItem onClick={handleDownload}>Stáhnout</MuiMenuItem>
        <MuiMenuItem onClick={() => setRenameOpen(true)}>
          Přejmenovat
        </MuiMenuItem>
        <MuiMenuItem onClick={() => setEditCardsOpen(true)}>
          Upravit karty
        </MuiMenuItem>
        <MuiMenuItem onClick={() => setEditRecordsOpen(true)}>
          Upravit citace
        </MuiMenuItem>
        <MuiMenuItem onClick={() => setDeleteOpen(true)}>Odstranit</MuiMenuItem>
      </MuiMenu>
      <Modal
        open={renameOpen}
        setOpen={setRenameOpen}
        withPadding={true}
        content={
          <AttachmentCardRename
            attachment={attachment}
            close={() => {
              setRenameOpen(false);
              update();
              close();
            }}
          />
        }
      />
      <Modal
        open={editCardsOpen}
        setOpen={setEditCardsOpen}
        overflowVisible={true}
        withPadding={true}
        content={
          <AttachmentCardEdit
            field="linkedCards"
            text="karty"
            loadOptions={getCards}
            attachment={attachment}
            close={() => {
              setEditCardsOpen(false);
              update();
              close();
            }}
          />
        }
      />
      <Modal
        open={editRecordsOpen}
        setOpen={setEditRecordsOpen}
        overflowVisible={true}
        withPadding={true}
        content={
          <AttachmentCardEdit
            field="records"
            text="citace"
            loadOptions={getRecords}
            attachment={attachment}
            close={() => {
              setEditRecordsOpen(false);
              update();
              close();
            }}
            submitCheck={(
              values: AttachmentEditProps,
              options: any[],
              callback: Function
            ) => {
              const records = filter(
                (values.records || []).map(({ id }) =>
                  find(options, o => o.id === id)
                ),
                r => r.document && r.document.id !== attachment.id
              );

              if (records.length) {
                const documents = uniqBy(
                  records.map(({ document }) => document),
                  ({ id }) => id
                );
                callback({
                  confirmText: (
                    <span>
                      Citace {mapText(records)}{" "}
                      {records.length > 1 ? "jsou navázány" : "je navázána"} na
                      dokument{documents.length > 1 ? "y" : ""}{" "}
                      {mapText(documents)}.
                      <br />
                      <br />
                      <span style={{ fontWeight: "bold" }}>
                        Opravdu chcete navázat{" "}
                        {records.length > 1 ? "citace" : "citaci"} na tento
                        dokument?
                      </span>
                      <br />
                    </span>
                  ),
                  values
                });
                return false;
              }
              return true;
            }}
          />
        }
      />
      <Popoverconfirm
        {...{
          ref: ref,
          open: deleteOpen,
          setOpen: setDeleteOpen,
          confirmText: "Opravdu chcete odstranit zvolený dokument?",
          acceptText: "Odstranit",
          onConfirmClick: handleDelete
        }}
      />
    </React.Fragment>
  );
};
