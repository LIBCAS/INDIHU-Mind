import React from "react";
import { get } from "lodash";
import classNames from "classnames";
import { Attachment } from "./_types";
import MuiCard from "@material-ui/core/Card";
import MuiCardContent from "@material-ui/core/CardContent";
import MuiCardHeader from "@material-ui/core/CardHeader";
import MuiTooltip from "@material-ui/core/Tooltip";
import { AttachmentCardMenu } from "./AttachmentCardMenu";

import { FileTypeText } from "../../enums";
import { formatBytes, openInNewTab } from "../../utils";
import { useStyles } from "./_styles";
import { useStyles as useFormStyles } from "../../components/form/_styles";
import { formatDate } from "../../utils/dateTime";

interface AttachmentCardProps {
  item: Attachment;
  refresh: Function;
}

export const AttachmentCard: React.FC<AttachmentCardProps> = ({
  item,
  refresh,
}) => {
  const classes = useStyles();
  const classesForm = useFormStyles();

  return (
    <React.Fragment>
      <MuiCard style={{ width: "225px", margin: "7.5px" }}>
        <MuiCardHeader
          classes={classes}
          title={
            <MuiTooltip title="Název přílohy">
              <span>{item.name}</span>
            </MuiTooltip>
          }
          subheader={
            <MuiTooltip title="Datum vytvoření">
              <span>{item.created && formatDate(item.created.toString())}</span>
            </MuiTooltip>
          }
          action={<AttachmentCardMenu item={item} refresh={refresh} />}
        />
        <MuiCardContent>
          <div className={classes.attachmentCardRow}>
            {[
              { label: "Počet karet", value: item.linkedCards.length },
              {
                label: "Počet citací",
                value: get(item, "records.length", 0),
              },
              { label: "Velikost", value: formatBytes(item.size) },
              {
                label: "Původ",
                value: get(FileTypeText, item.providerType, "Neznámý"),
              },
              { label: "Typ souboru", value: item.type },
              {
                label: "Odkaz",
                value: item.link,
                visible: !!item.link,
                link: true,
              },
            ].map(
              ({ label, value, visible, link }) =>
                visible !== false && (
                  <div key={label} className={classes.attachmentCardLabel}>
                    <div style={{ marginRight: 8 }}>{label}</div>
                    <div
                      className={classNames(
                        classes.attachmentCardValue,
                        link && classesForm.link
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
