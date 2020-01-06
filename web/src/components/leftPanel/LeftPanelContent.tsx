import React, { useState } from "react";
import classNames from "classnames";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import { useTheme } from "@material-ui/styles";
import { Theme } from "@material-ui/core/styles/createMuiTheme";

import { CardCreateButton } from "../../modules/cardCreate/CardCreateButton";

import { DeleteCards } from "../deleteCards/DeleteCards";

import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles } from "./_leftPanelStyles";
import { Links } from "./Links";
import { TabContent } from "../tabContent/TabContent";

interface LeftPanelContentProps {
  setLeftPanelOpen?: any;
}

export type TabProps = "category" | "label" | undefined;

export const LeftPanelContent: React.FC<LeftPanelContentProps> = ({
  setLeftPanelOpen
}) => {
  const classes = useStyles();
  const classesSpacing = useSpacingStyles();
  const [activeTab, setActiveTab] = useState<TabProps>("category");
  const theme: Theme = useTheme();
  const matchesMd = useMediaQuery(theme.breakpoints.up("md"));
  return (
    <>
      <div className={classes.buttonWrapper}>
        {matchesMd ? (
          <CardCreateButton />
        ) : (
          <>
            <Links setLeftPanelOpen={setLeftPanelOpen} />
            <div className={classesSpacing.mt1} />
            <DeleteCards />
            <div className={classesSpacing.mt1} />
            <CardCreateButton />
          </>
        )}
      </div>
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
              "category" === activeTab || undefined === activeTab
          })}
        >
          Kategorie
        </div>
        <div
          id="label"
          onClick={() => setActiveTab("label")}
          className={classNames({
            [classes.tab]: true,
            [classes.activeTab]: "label" === activeTab
          })}
        >
          Štítky
        </div>
      </div>
      <TabContent activeTab={activeTab} setActiveTab={setActiveTab} />
    </>
  );
};
