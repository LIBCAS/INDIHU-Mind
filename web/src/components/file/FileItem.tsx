import React, { useState } from "react";
import { useHistory } from "react-router-dom";
import PictureAsPdf from "@material-ui/icons/PictureAsPdf";
import CloudDownload from "@material-ui/icons/CloudDownload";
import Tooltip from "@material-ui/core/Tooltip";
import Delete from "@material-ui/icons/Delete";
import InsertDriveFile from "@material-ui/icons/InsertDriveFile";
import classNames from "classnames";

import { Drive } from "../../components/icons/Drive";
import { Dropbox } from "../../components/icons/Dropbox";
import { Popconfirm } from "../../components/portal/Popconfirm";
import { FileProps } from "../../types/file";
import { FileType } from "../../enums";
import { useStyles } from "./_fileStyles";
import { useStyles as useEffectStyles } from "../../theme/styles/effectStyles";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { downloadFile } from "./_utils";

interface FileItemProps {
  file: FileProps;
  onDelete?: (file: FileProps) => void;
  disableDownload?: boolean;
}

export const FileItem: React.FC<FileItemProps> = ({
  file,
  onDelete,
  disableDownload,
}) => {
  const classes = useStyles();
  const classesLayout = useLayoutStyles();
  const classesSpacing = useSpacingStyles();
  const classesText = useTextStyles();
  const classesEffect = useEffectStyles();
  const history = useHistory();

  const [deleteConfirmOpen, setDeleteConfirmOpen] = useState(false);

  const { name, type, providerType } = file;
  const onDownload = (e: any) => {
    e.stopPropagation();
    downloadFile(file);
  };
  return (
    <div
      className={classes.fileWrapper}
      onClick={() => {
        !deleteConfirmOpen &&
          history.push(`/attachments?search=${encodeURI(file.name)}`);
        setDeleteConfirmOpen(false);
      }}
    >
      <Tooltip title={type}>
        <div className={classesLayout.flex}>
          {type === "pdf" ? (
            <PictureAsPdf className={classesSpacing.mr1} color="secondary" />
          ) : (
            <InsertDriveFile className={classesSpacing.mr1} color="secondary" />
          )}
        </div>
      </Tooltip>
      <Tooltip title={name}>
        <div
          className={classNames(
            classesText.textBold,
            classesSpacing.mrAuto,
            classesText.noWrap
          )}
        >
          {name}
        </div>
      </Tooltip>
      <span className={classesSpacing.ml1} />
      {providerType === FileType.GOOGLE_DRIVE && (
        <Tooltip title="Stáhnout přílohu">
          <div className={classesLayout.flex}>
            <Drive
              onClick={onDownload}
              className={classNames(
                classesSpacing.mr1,
                classes.fileIcons,
                classesEffect.hoverPrimary
              )}
            />
          </div>
        </Tooltip>
      )}
      {providerType === FileType.DROPBOX && (
        <Tooltip title="Stáhnout přílohu">
          <div className={classesLayout.flex}>
            <Dropbox
              onClick={onDownload}
              className={classNames(
                classesSpacing.mr1,
                classes.fileIcons,
                classesEffect.hoverPrimary
              )}
            />
          </div>
        </Tooltip>
      )}

      {((providerType !== FileType.DROPBOX &&
        providerType !== FileType.GOOGLE_DRIVE) ||
        onDelete === undefined) &&
        !disableDownload && (
          <Tooltip title="Stáhnout přílohu">
            <div className={classesLayout.flex}>
              <CloudDownload
                onClick={onDownload}
                className={classNames(
                  classesSpacing.mr1,
                  classes.fileIcons,
                  classesEffect.hoverPrimary
                )}
              />
            </div>
          </Tooltip>
        )}

      {onDelete && (
        <Popconfirm
          confirmText="Odebrat přílohu z karty?"
          acceptText="Odebrat"
          onConfirmClick={(e: any) => {
            e.stopPropagation();
            onDelete(file);
          }}
          onOpenCallback={() => setDeleteConfirmOpen(true)}
          Button={
            <Tooltip title="Odebrat přílohu z karty">
              <div className={classesLayout.flex}>
                <Delete
                  className={classNames(
                    classesSpacing.mr1,
                    classes.fileIcons,
                    classesEffect.hoverSecondary
                  )}
                />
              </div>
            </Tooltip>
          }
        />
      )}
    </div>
  );
};
