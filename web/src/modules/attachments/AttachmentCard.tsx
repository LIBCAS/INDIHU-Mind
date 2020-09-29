import React from "react";
import { get } from "lodash";
import classNames from "classnames";
import { Attachment } from "./_types";
import MuiCard from "@material-ui/core/Card";
import MuiCardContent from "@material-ui/core/CardContent";
import MuiCardHeader from "@material-ui/core/CardHeader";
import Moment from "react-moment";
import MuiTooltip from "@material-ui/core/Tooltip";
import { AttachmentCardMenu } from "./AttachmentCardMenu";

import { FileTypeText } from "../../enums";
import { formatBytes, openInNewTab } from "../../utils";
import { useStyles } from "./_styles";

interface AttachmentCardProps {
  attachment: Attachment;
  update: () => void;
}

export const AttachmentCard: React.FC<AttachmentCardProps> = ({
  attachment,
  update
}) => {
  const classes = useStyles();

  return (
    <React.Fragment>
      <MuiCard style={{ width: "225px", margin: "7.5px" }}>
        <MuiCardHeader
          classes={classes}
          title={
            <MuiTooltip title="Název přílohy">
              <span>{attachment.name}</span>
            </MuiTooltip>
          }
          subheader={
            <MuiTooltip title="Datum vytvoření">
              <Moment format="DD. MM. YYYY">{attachment.created}</Moment>
            </MuiTooltip>
          }
          action={
            <AttachmentCardMenu attachment={attachment} update={update} />
          }
        />
        <MuiCardContent>
          <div className={classes.attachmentCardRow}>
            {[
              { label: "Počet karet", value: attachment.linkedCards.length },
              {
                label: "Počet citací",
                value: get(attachment, "records.length", 0)
              },
              { label: "Velikost", value: formatBytes(attachment.size) },
              {
                label: "Původ",
                value: get(FileTypeText, attachment.providerType, "Neznámý")
              },
              { label: "Typ souboru", value: attachment.type },
              {
                label: "Odkaz",
                value: attachment.link,
                visible: !!attachment.link,
                link: true
              }
            ].map(
              ({ label, value, visible, link }) =>
                visible !== false && (
                  <div key={label} className={classes.attachmentCardLabel}>
                    <div style={{ marginRight: 8 }}>{label}</div>
                    <div
                      className={classNames(
                        classes.attachmentCardValue,
                        link && classes.attachmentCardLink
                      )}
                      onClick={() => link && openInNewTab(value)}
                    >
                      {value}
                    </div>
                  </div>
                )
            )}
          </div>
        </MuiCardContent>
      </MuiCard>
    </React.Fragment>
  );
};
