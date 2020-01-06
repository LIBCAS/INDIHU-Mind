import React, { useContext, useEffect, useState } from "react";
import classNames from "classnames";
import Slide from "@material-ui/core/Slide";
import KeyboardArrowDown from "@material-ui/icons/KeyboardArrowDown";
import KeyboardArrowUp from "@material-ui/icons/KeyboardArrowUp";
import { withRouter, RouteComponentProps } from "react-router-dom";

import { watch, unwatch, get } from "../../utils/store";
import { GlobalContext, StateProps } from "../../context/Context";
import { Labels } from "./Labels";
import { Categories } from "./Categories";

import { useStyles } from "./_tabContentStyles";

import { TabProps } from "../leftPanel/LeftPanelContent";

import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";

interface TabContentProps {
  activeTab: TabProps;
  setActiveTab: Function;
}

export const TabContentView: React.FC<
  TabContentProps & RouteComponentProps
> = ({ activeTab, setActiveTab, history }) => {
  const classesText = useTextStyles();
  const classesSpacing = useSpacingStyles();
  const classesLayout = useLayoutStyles();
  const [cardsOpened, setCardsOpened] = useState<
    { id: string; name: string }[]
  >([]);
  const [showCards, setShowCards] = useState(false);
  const [transition, setTransition] = useState(false);
  const context: any = useContext(GlobalContext);
  const state: StateProps = context.state;
  const classes = useStyles();
  const { categoryActive } = state.category;
  const { labelActive } = state.label;
  // show the right tab if active label or category is changed somewhere else
  useEffect(() => {
    if (
      categoryActive === undefined &&
      activeTab == "category" &&
      labelActive !== undefined
    ) {
      setActiveTab("label");
    }
    if (
      labelActive === undefined &&
      activeTab == "label" &&
      categoryActive !== undefined
    ) {
      setActiveTab("category");
    }
  }, [categoryActive, labelActive]);
  const onChangeCards = () => {
    const storeCards = get("cardsOpened", []);
    setCardsOpened(storeCards);
  };
  useEffect(() => {
    onChangeCards();
    const watchId = watch("cardsOpened", onChangeCards);
    return () => {
      unwatch(watchId);
    };
  }, []);
  return (
    <div className={classes.tabContentWrapper}>
      <div
        style={{
          flex: "1",
          maxHeight: transition ? "57vh" : "70vh",
          transition: ".3s ease max-height",
          overflow: "auto",
          display:
            activeTab === "category" || activeTab === undefined
              ? "block"
              : "none"
        }}
      >
        <Categories activeTab={activeTab} setActiveTab={setActiveTab} />
      </div>
      <div
        style={{
          flex: "1",
          maxHeight: transition ? "57vh" : "70vh",
          transition: ".3s ease max-height",
          overflow: "auto",
          display: activeTab === "label" ? "block" : "none"
        }}
      >
        <Labels activeTab={activeTab} />
      </div>
      {cardsOpened.length > 0 && (
        <div>
          <div
            className={classNames(
              classesLayout.flex,
              classesLayout.alignCenter,
              classesText.textGreyLight,
              classesText.textUppercase,
              classesText.cursor,
              classesText.small,
              classesSpacing.mt2
            )}
            onClick={() => setShowCards(prev => !prev)}
          >
            Naposledy otevřené karty{" "}
            {showCards ? <KeyboardArrowDown /> : <KeyboardArrowUp />}
          </div>
          <Slide
            direction="right"
            in={showCards}
            unmountOnExit
            onEntering={() => setTransition(true)}
            onExited={() => setTransition(false)}
          >
            <div>
              {cardsOpened.map(c => (
                <div
                  key={c.id}
                  className={classNames(classes.cardOpened)}
                  onClick={() => {
                    history.push(`/card/${c.id}`);
                  }}
                >
                  {c.name}
                </div>
              ))}
            </div>
          </Slide>
        </div>
      )}
    </div>
  );
};

export const TabContent = withRouter(TabContentView);
