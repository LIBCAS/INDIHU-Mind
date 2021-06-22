import React, { useState } from "react";
import classNames from "classnames";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import { useTheme } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

import { DeleteCards } from "../deleteCards/DeleteCards";

import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles } from "./_leftPanelStyles";
import { Links } from "./Links";
import { TabContent } from "../tabContent/TabContent";
import { useUserToken } from "../../hooks/authHooks";
import { isAdmin } from "../../utils/token";

interface LeftPanelContentProps {
  setLeftPanelOpen?: any;
  enableCards?: boolean;
  setOpenModalInfo: any;
}

export type TabProps = "category" | "label" | undefined;

export const LeftPanelContent: React.FC<LeftPanelContentProps> = ({
  setLeftPanelOpen,
  setOpenModalInfo,
  enableCards = true,
}) => {
  const classes = useStyles();
  const classesSpacing = useSpacingStyles();
  const [activeTab, setActiveTab] = useState<TabProps>("category");
  const theme: Theme = useTheme();

  const token = useUserToken();

  const admin = isAdmin(token);

  const matchesBigScreen = useMediaQuery(
    theme.breakpoints.up(admin ? ("xxl" as any) : "xl")
  );

  return (
    <div className={classes.leftPanelContent}>
      {matchesBigScreen ? (
        <div className={classesSpacing.mt3} />
      ) : (
        <div className={classes.buttonWrapper}>
          <Links
            setOpenModalInfo={setOpenModalInfo}
            setLeftPanelOpen={setLeftPanelOpen}
          />
          <DeleteCards setLeftPanelOpen={setLeftPanelOpen} />
        </div>
      )}
      {enableCards && (
        <div className={classes.tabsWrapper}>
          <div
            id="category"
            onClick={() => {
              if (activeTab === "category") {
                setActiveTab(undefined);
              } else {
                setActiveTab("category");
              }
            }}
            className={classNames({
              [classes.tab]: true,
              [classes.activeTab]:
                "category" === activeTab || undefined === activeTab,
            })}
          >
            Kategorie
          </div>
          <div
            id="label"
            onClick={() => setActiveTab("label")}
            className={classNames({
              [classes.tab]: true,
              [classes.activeTab]: "label" === activeTab,
            })}
          >
            Štítky
          </div>
        </div>
      )}
      {enableCards && (
        <TabContent activeTab={activeTab} setActiveTab={setActiveTab} />
      )}
    </div>
  );
};
