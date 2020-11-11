import React, { useState, useEffect, useContext, useCallback } from "react";
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
  open: boolean;
  setOpen: Function;
  item?: CardProps;
  edit?: boolean;
  afterEdit?: Function;
  attributeTemplates?: CardTemplateAttribute[];
}

const defaultValues = {
  id: "",
  name: "",
  note: "",
  categories: [],
  labels: [],
  attributes: [],
  linkedCards: [],
  records: [],
};

export const CardCreateRoot: React.FC<CardCreateRootProps> = ({
  open,
  setOpen,
  item,
  edit,
  afterEdit,
  attributeTemplates,
}) => {
  const classes = useStyles();

  const classesEffect = useEffectStyles();

  const context: any = useContext(GlobalContext);

  const state: StateProps = context.state;

  const dispatch: Function = context.dispatch;

  const [type, setType] = useState<"create" | "template">("create");

  const templates = state.template.templates;

  const [initValues, setInitValues] = useState<InitValuesProps>(defaultValues);

  const [showCloseConfirmModal, setShowCloseConfirmModal] = useState<boolean>(
    false
  );

  const loadTemplates = useCallback(() => {
    templateGet(dispatch);
  }, [dispatch]);

  const setAttributes = useCallback((attributes: CardTemplateAttribute[]) => {
    const transformed = attributes.map((a) => ({
      ...a,
      value: getAttributeTypeDefaultValue(a.type),
    }));
    setInitValues({
      ...defaultValues,
      attributes: transformed,
    });
    setType("create");
  }, []);

  const loadCardContent = useCallback(() => {
    if (item) {
      api()
        .get(`card/${item.id}/content`)
        .json()
        .then((res: any) => {
          if (res.length > 0 && res[0].card) {
            const card = res[0].card;
            const init = {
              ...card,
              attributes: res[0].attributes,
              labels: card.labels.map(parseLabel),
              categories: flattenCategory(card.categories),
              cardContentId: res[0].id,
            };
            setInitValues(init);
          }
          return;
        });
    } else if (attributeTemplates && attributeTemplates.length) {
      setAttributes(attributeTemplates);
    }
  }, [attributeTemplates, setAttributes, item]);

  // common template has no owner
  useEffect(() => {
    loadTemplates();
    loadCardContent();
  }, [loadTemplates, loadCardContent]);

  useEffect(() => {}, [state.template]);

  useEffect(() => {
    if (open && item) {
      setType("create");
      loadCardContent();
    }
    if (!open) {
      setInitValues(defaultValues);
    }
  }, [item, open, loadCardContent]);

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
    setOpen(false);
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
        open={open}
        setOpen={setOpen}
        onClose={handleModalClose}
        disableEnforceFocus={true}
        fullSize={true}
        content={
          <React.Fragment>
            {type === "create" && (
              <CardCreateForm
                setOpen={setOpen}
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
                    setType((prev) =>
                      prev === "template" ? "create" : "template"
                    )
                  }
                  className={classNames(classesEffect.hoverPrimary, {
                    [classes.outsidePanelItemActive]: type === "template",
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
