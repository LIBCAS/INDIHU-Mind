import React, { useState, useEffect, useContext } from "react";
import TableChart from "@material-ui/icons/TableChart";
import classNames from "classnames";
import Tooltip from "@material-ui/core/Tooltip";
import IconButton from "@material-ui/core/IconButton";

import { templateGet } from "../../context/actions/template";
import { GlobalContext, StateProps } from "../../context/Context";
import { CardTemplateAttribute } from "../../types/cardTemplate";
import { CardProps } from "../../types/card";
import { Modal } from "../../components/portal/Modal";
import { api } from "../../utils/api";
import { getAttributeTypeDefaultValue } from "../../utils/attribute";

import { CardCreateForm, InitValuesProps } from "./CardCreateForm";
import { CardCreateTemplate } from "./CardCreateTemplate";
import { parseLabel, flattenCategory } from "./_utils";

import { useStyles as useEffectStyles } from "../../theme/styles/effectStyles";
import { useStyles } from "./_cardCreateStyles";
import { CardCreateCloseConfirm } from "./CardCreateCloseConfirm";

interface CardCreateRootProps {
  showModal: boolean;
  setShowModal: Function;
  selectedRow?: CardProps;
  edit?: boolean;
  afterEdit?: Function;
  attributeTemplates?: CardTemplateAttribute[];
}

export const CardCreateRoot: React.FC<CardCreateRootProps> = ({
  showModal,
  setShowModal,
  selectedRow,
  edit,
  afterEdit,
  attributeTemplates
}) => {
  const classes = useStyles();

  const classesEffect = useEffectStyles();

  const context: any = useContext(GlobalContext);

  const state: StateProps = context.state;

  const dispatch: Function = context.dispatch;

  const [type, setType] = useState<"create" | "template">("create");

  const templates = state.template.templates;

  const [initValues, setInitValues] = useState<InitValuesProps | undefined>(
    undefined
  );

  const [showCloseConfirmModal, setShowCloseConfirmModal] = useState<boolean>(
    false
  );

  const loadTemplates = () => {
    templateGet(dispatch);
  };

  const setAttributes = (attributes: CardTemplateAttribute[]) => {
    const transformed = attributes.map(a => ({
      ...a,
      value: getAttributeTypeDefaultValue(a.type)
    }));
    setInitValues(() => ({
      id: "",
      name: "",
      note: "",
      categories: [],
      labels: [],
      attributes: transformed,
      linkedCards: [],
      records: []
    }));
    setType("create");
  };

  const loadCardContent = () => {
    if (selectedRow) {
      api()
        .get(`card/${selectedRow.id}/content`)
        .json()
        .then((res: any) => {
          if (res.length > 0 && res[0].card) {
            const card = res[0].card;
            const init = {
              ...card,
              attributes: res[0].attributes,
              labels: card.labels.map(parseLabel),
              categories: flattenCategory(card.categories),
              cardContentId: res[0].id
            };
            setInitValues(init);
          }
          return;
        });
    } else if (attributeTemplates && attributeTemplates.length) {
      setAttributes(attributeTemplates);
    }
  };

  // common template has no owner
  useEffect(() => {
    loadTemplates();
    loadCardContent();
  }, []);

  useEffect(() => {}, [state.template]);

  useEffect(() => {
    if (showModal && selectedRow) {
      setType("create");
      loadCardContent();
    }
    if (!showModal) {
      setInitValues(undefined);
    }
  }, [selectedRow, showModal]);

  /**
   * It handles modal closing. If the truth is returned, the modal closes and vice versa
   * @returns boolean
   */
  const handleModalClose = () => {
    // TODO: Show message only if form is touched by user
    setShowCloseConfirmModal(true);
    return false;
  };

  /**
   * It handles confirm close modal closing. If the truth is returned, the modal closes and vice versa
   * @returns boolean
   */
  const handleConfirmModalClose = () => {
    setShowCloseConfirmModal(false);
    return true;
  };

  /**
   * It handles confirm close modal submit.
   */
  const handleConfirmModalSubmit = () => {
    setShowCloseConfirmModal(false);
    setShowModal(false);
  };

  return (
    <React.Fragment>
      <Modal
        open={showCloseConfirmModal}
        setOpen={setShowCloseConfirmModal}
        onClose={handleConfirmModalClose}
        content={
          <CardCreateCloseConfirm
            onCancel={handleConfirmModalClose}
            onSubmit={handleConfirmModalSubmit}
          />
        }
      />
      <Modal
        open={showModal}
        setOpen={setShowModal}
        onClose={handleModalClose}
        disableEnforceFocus={true}
        fullSize={true}
        content={
          <React.Fragment>
            {type === "create" && (
              <CardCreateForm
                setShowModal={setShowModal}
                loadTemplates={loadTemplates}
                initValues={initValues}
                templates={templates}
                edit={edit}
                afterEdit={afterEdit}
              />
            )}
            {type === "template" && (
              <CardCreateTemplate
                setAttributes={setAttributes}
                templates={templates}
              />
            )}
          </React.Fragment>
        }
        contentOutside={
          <React.Fragment>
            <div className={classes.outsidePanel}>
              <Tooltip title="Šablony">
                <IconButton
                  size="small"
                  onClick={() =>
                    setType(prev =>
                      prev === "template" ? "create" : "template"
                    )
                  }
                  className={classNames(classesEffect.hoverPrimary, {
                    [classes.outsidePanelItemActive]: type === "template"
                  })}
                >
                  <TableChart />
                </IconButton>
              </Tooltip>
            </div>
          </React.Fragment>
        }
      />
    </React.Fragment>
  );
};
