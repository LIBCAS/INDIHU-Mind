import IconButton from "@material-ui/core/IconButton";
import Tooltip from "@material-ui/core/Tooltip";
import Delete from "@material-ui/icons/Delete";
import KeyboardArrowLeft from "@material-ui/icons/KeyboardArrowLeft";
import classNames from "classnames";
import React, { useContext, useState } from "react";
import { Popconfirm } from "../../components/portal/Popconfirm";
import { GlobalContext } from "../../context/Context";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { CardContentProps, CardProps } from "../../types/card";
import { onDeleteCard } from "../../utils/card";
import { CardCreateRoot } from "../cardCreate/CardCreateRoot";
import { CardDetailContentId } from "./cardDetailContent/CardDetailContentId";
import { CardPrint } from "./CardPrint";
import { useStyles } from "./_cardDetailStyles";

interface CardDetailActionsProps {
  card: CardProps;
  cardContent: CardContentProps;
  history: any;
  loadCard: Function;
  isTrashed?: boolean;
}

export const CardDetailActions: React.FC<CardDetailActionsProps> = ({
  card,
  cardContent,
  history,
  loadCard,
  isTrashed,
}) => {
  const classes = useStyles();
  const classesLayout = useLayoutStyles();
  const classesSpacing = useSpacingStyles();
  const classesText = useTextStyles();
  const [showModal, setShowModal] = useState(false);
  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;

  const handleDelete = () => {
    onDeleteCard(card.id, dispatch, () => history.push("/cards"));
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
        <CardDetailContentId card={card} />
        <CardPrint
          card={{ ...card, attributes: cardContent.attributes }}
          className={classesSpacing.ml1}
        />
        <Popconfirm
          disabled={isTrashed || !cardContent.lastVersion}
          Button={
            <Tooltip title="Smazat">
              <IconButton
                disabled={isTrashed || !cardContent.lastVersion}
                className={classes.iconSecondary}
              >
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
        item={card}
        edit
        afterEdit={() => loadCard()}
      />
    </div>
  );
};
