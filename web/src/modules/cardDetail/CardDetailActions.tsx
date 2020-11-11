import React, { useState, useContext } from "react";
import classNames from "classnames";
import KeyboardArrowLeft from "@material-ui/icons/KeyboardArrowLeft";
import Delete from "@material-ui/icons/Delete";
import IconButton from "@material-ui/core/IconButton";
import Tooltip from "@material-ui/core/Tooltip";

import { GlobalContext } from "../../context/Context";

import { onDeleteCard } from "../../utils/card";
import { CardContentProps } from "../../types/card";
import { Popconfirm } from "../../components/portal/Popconfirm";
import { CardCreateRoot } from "../cardCreate/CardCreateRoot";
import { CardPrint } from "./CardPrint";

import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles } from "./_cardDetailStyles";
import { CardDetailContentId } from "./cardDetailContent/CardDetailContentId";

interface CardDetailActionsProps {
  card: CardContentProps;
  history: any;
  loadCard: Function;
}

export const CardDetailActions: React.FC<CardDetailActionsProps> = ({
  card,
  history,
  loadCard,
}) => {
  const classes = useStyles();
  const classesLayout = useLayoutStyles();
  const classesSpacing = useSpacingStyles();
  const classesText = useTextStyles();
  const [showModal, setShowModal] = useState(false);
  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;

  const cardInner = card.card;

  const handleDelete = () => {
    onDeleteCard(cardInner.id, dispatch, () => history.push("/cards"));
  };

  return (
    <div className={classes.actionsWrapper}>
      <div className={classNames(classes.actionsBack, classes.actionsIcon)}>
        <IconButton
          onClick={() => history.goBack()}
          className={classNames(classesLayout.flex, classesLayout.alignCenter)}
        >
          <KeyboardArrowLeft fontSize={"large"} />
          <div className={classesText.normal}>Zpět</div>
        </IconButton>
      </div>
      <div className={classes.actions}>
        <CardDetailContentId card={cardInner} />
        <CardPrint
          card={{ ...cardInner, attributes: card.attributes }}
          className={classesSpacing.ml1}
        />
        <Popconfirm
          Button={
            <Tooltip title="Smazat">
              <IconButton className={classes.iconSecondary}>
                <Delete color="inherit" />
              </IconButton>
            </Tooltip>
          }
          confirmText="Smazat kartu?"
          onConfirmClick={() => {
            handleDelete();
          }}
        />
      </div>
      <CardCreateRoot
        open={showModal}
        setOpen={setShowModal}
        item={card.card}
        edit
        afterEdit={() => loadCard()}
      />
    </div>
  );
};
