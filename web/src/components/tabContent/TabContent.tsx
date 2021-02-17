import Slide from "@material-ui/core/Slide";
import KeyboardArrowDown from "@material-ui/icons/KeyboardArrowDown";
import KeyboardArrowUp from "@material-ui/icons/KeyboardArrowUp";
import classNames from "classnames";
import React, { useContext, useEffect, useState } from "react";
import { RouteComponentProps, withRouter } from "react-router-dom";
import { GlobalContext, StateProps } from "../../context/Context";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { get, unwatch, watch } from "../../utils/store";
import { TabProps } from "../leftPanel/LeftPanelContent";
import { Categories } from "./Categories";
import { Labels } from "./Labels";
import { useStyles } from "./_tabContentStyles";

interface TabContentProps {
  activeTab: TabProps;
  setActiveTab: Function;
}

export const TabContentView: React.FC<
  TabContentProps & RouteComponentProps
> = ({ activeTab, setActiveTab, history, location }) => {
  const classesText = useTextStyles();
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
      activeTab === "category" &&
      labelActive !== undefined
    ) {
      setActiveTab("label");
    }
    if (
      labelActive === undefined &&
      activeTab === "label" &&
      categoryActive !== undefined
    ) {
      setActiveTab("category");
    }
  }, [categoryActive, labelActive, setActiveTab]); // eslint-disable-line 
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

  const setActiveCallback = () => {
    if (location.pathname !== "/cards") {
      history.push("/cards");
    }
  };
  return (
    <div className={classes.tabContentWrapper}>
      <div
        style={{
          flex: "1",
          maxHeight: transition ? "57vh" : "70vh",
          transition: ".3s ease max-height",
          overflow: "hidden",
          display:
            activeTab === "category" || activeTab === undefined
              ? "block"
              : "none",
        }}
      >
        <Categories
          activeTab={activeTab}
          setActiveTab={setActiveTab}
          transition={transition}
          setActiveCallback={setActiveCallback}
        />
      </div>
      <div
        style={{
          flex: "1",
          maxHeight: transition ? "57vh" : "70vh",
          transition: ".3s ease max-height",
          overflow: "auto",
          display: activeTab === "label" ? "block" : "none",
        }}
      >
        <Labels activeTab={activeTab} setActiveCallback={setActiveCallback} />
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
              classesText.small
            )}
            onClick={() => setShowCards((prev) => !prev)}
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
              {cardsOpened.map((c) => (
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
