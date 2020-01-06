import React, { useState, useContext } from "react";
import Typography from "@material-ui/core/Typography";
import Button from "@material-ui/core/Button";
import classNames from "classnames";
import { withRouter, RouteComponentProps } from "react-router-dom";
import Delete from "@material-ui/icons/Delete";

import { FileItem } from "../../components/file/FileItem";
import { GlobalContext } from "../../context/Context";
import { CardProps } from "../../types/card";

import { Record } from "../../components/card/Record";
import { Label } from "../../components/card/Label";
import { Category } from "../../components/card/Category";
import { Popconfirm } from "../../components/portal/Popconfirm";
import { ButtonCancel } from "../../components/control/ButtonCancel";
import { CardTile } from "../../components/card/CardTile";
import { CardCreateRoot } from "../cardCreate/CardCreateRoot";

import { useStyles } from "./_cardsTableDetailStyles";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";

import { parseAttribute, onDeleteCard } from "../../utils/card";

interface CardsTableDetailContentProps {
  card: CardProps;
  onCancel(e: React.MouseEvent): void;
}

const CardsTableDetailContentView: React.FC<
  CardsTableDetailContentProps & RouteComponentProps
> = ({ card, onCancel, history }) => {
  const classes = useStyles();
  const classesText = useTextStyles();
  const classesSpacing = useSpacingStyles();
  const classesLayout = useLayoutStyles();
  const [open, setOpen] = useState(false);
  const context: any = useContext(GlobalContext);

  const dispatch: Function = context.dispatch;
  const refreshPage = () => {
    history.push({ pathname: "/empty" });
    setTimeout(() => {
      history.replace({ pathname: "/cards" });
    });
  };

  const handleDelete = () => {
    onDeleteCard(card.id, dispatch, refreshPage);
  };

  return (
    <div style={{ position: "relative" }}>
      <CardCreateRoot
        showModal={open}
        setShowModal={setOpen}
        selectedRow={card}
        edit
      />
      <ButtonCancel onClick={onCancel} />
      <Typography variant="h5" className={classes.contentName}>
        {card.name}
      </Typography>
      <div className={classNames(classesLayout.flex, classesText.textGrey)}>
        <Button
          variant="outlined"
          className={classesSpacing.mb1}
          size="small"
          color="primary"
          onClick={() => history.push(`/card/${card.id}`)}
        >
          Přejít na detail
        </Button>

        {/* <Button
          variant="outlined"
          className={classNames(classesSpacing.ml1, classesSpacing.mb1)}
          size="small"
          color="inherit"
          onClick={() => setOpen(true)}
        >
          Editovat <Edit className={classesSpacing.ml1} fontSize="small" />
        </Button> */}
        <Popconfirm
          confirmText="Smazat kartu?"
          onConfirmClick={handleDelete}
          Button={() => (
            <Button
              variant="outlined"
              className={classNames(classesSpacing.ml1, classesSpacing.mb1)}
              size="small"
              color="inherit"
            >
              Smazat <Delete className={classesSpacing.ml1} fontSize="small" />
            </Button>
          )}
        />
      </div>
      <div />
      {card.categories.map(cat => (
        <Category key={cat.id} category={cat} />
      ))}
      {card.note && (
        <Typography variant="body1" className={classesSpacing.mt2}>
          {card.note}
        </Typography>
      )}
      {card.labels.length > 0 && (
        <Typography
          className={classNames(classesText.subtitle, classesSpacing.mt2)}
        >
          štítky
        </Typography>
      )}
      <div className={classNames(classesLayout.flex, classesLayout.flexWrap)}>
        {card.labels.map(label => (
          <Label key={label.id} label={label} />
        ))}
      </div>
      {card.records.length > 0 && (
        <Typography
          className={classNames(classesText.subtitle, classesSpacing.mt2)}
        >
          Citace
        </Typography>
      )}
      <div className={classNames(classesLayout.flex, classesLayout.flexWrap)}>
        {card.records.map(record => (
          <Record key={record.id} record={record} />
        ))}
      </div>
      {card.attributes && (
        <div
          className={classNames(
            classesLayout.flex,
            classesLayout.directionColumn,
            classesSpacing.mt1
          )}
        >
          {card.attributes.map(attribute => {
            return (
              <div
                key={attribute.id}
                className={classNames(
                  classesLayout.flex,
                  classesSpacing.mb1,
                  classesLayout.directionColumn
                )}
              >
                <Typography className={classes.contentAttributeTitle}>
                  {attribute.name}
                </Typography>
                <Typography>{parseAttribute(attribute)}</Typography>
              </div>
            );
          })}
        </div>
      )}
      {card.files && card.files.length > 0 && (
        <>
          <Typography
            className={classNames(classesText.subtitle, classesSpacing.mt1)}
          >
            Soubory
          </Typography>
          <div className={classes.columnsWrapper}>
            {card.files.map(f => (
              <FileItem key={f.id} file={f} />
            ))}
          </div>
        </>
      )}
      {(card.linkedCards.length > 0 || card.linkingCards.length > 0) && (
        <>
          <Typography
            className={classNames(classesText.subtitle, classesSpacing.mt1)}
          >
            Propojené karty
          </Typography>
          <div className={classes.columnsWrapper}>
            {card.linkedCards.map(card => (
              <CardTile key={card.id} card={card} />
            ))}
            {card.linkingCards.map(card => (
              <CardTile key={card.id} card={card} />
            ))}
          </div>
        </>
      )}
    </div>
  );
};

export const CardsTableDetailContent = withRouter(CardsTableDetailContentView);
