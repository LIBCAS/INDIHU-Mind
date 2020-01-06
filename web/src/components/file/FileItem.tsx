import React from "react";
import PictureAsPdf from "@material-ui/icons/PictureAsPdf";
import CloudDownload from "@material-ui/icons/CloudDownload";
import Tooltip from "@material-ui/core/Tooltip";
import Delete from "@material-ui/icons/Delete";
import InsertDriveFile from "@material-ui/icons/InsertDriveFile";
import classNames from "classnames";

import { api } from "../../utils/api";
import { Drive } from "../../components/icons/Drive";
import { Dropbox } from "../../components/icons/Dropbox";
import { Popconfirm } from "../../components/portal/Popconfirm";
import { FileProps } from "../../types/file";

import { useStyles } from "./_fileStyles";
import { useStyles as useEffectStyles } from "../../theme/styles/effectStyles";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";

interface FileItemProps {
  file: FileProps;
  onDelete?: (file: FileProps) => void;
  disableDownload?: boolean;
}

export const FileItem: React.FC<FileItemProps> = ({
  file,
  onDelete,
  disableDownload
}) => {
  const classes = useStyles();
  const classesLayout = useLayoutStyles();
  const classesSpacing = useSpacingStyles();
  const classesText = useTextStyles();
  const classesEffect = useEffectStyles();
  const { name, type, link, providerType } = file;
  const onDownload = () => {
    if (file.providerType === "LOCAL") {
      api({ noContentType: true })
        .get(`attachment_file/${file.id}`)
        .then(response => response.blob())
        .then(blob => {
          const url = window.URL.createObjectURL(blob);
          let a = document.createElement("a");
          a.href = url;
          a.download = file.name;
          document.body.appendChild(a);
          a.click();
          a.remove();
        });
    } else {
      window.open(link, "_blank");
    }
  };
  return (
    <div className={classes.fileWrapper}>
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
      {providerType === "GOOGLE_DRIVE" && (
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
      {providerType === "DROPBOX" && (
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

      {((providerType !== "DROPBOX" && providerType !== "GOOGLE_DRIVE") ||
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
                color="action"
              />
            </div>
          </Tooltip>
        )}

      {onDelete && (
        <Popconfirm
          confirmText="Smazat přílohu?"
          onConfirmClick={() => onDelete(file)}
          Button={() => (
            <Tooltip title="Smazat přílohu">
              <div className={classesLayout.flex}>
                <Delete
                  className={classNames(
                    classesSpacing.mr1,
                    classes.fileIcons,
                    classesEffect.hoverSecondary
                  )}
                  color="action"
                />
              </div>
            </Tooltip>
          )}
        />
      )}
    </div>
  );
};
