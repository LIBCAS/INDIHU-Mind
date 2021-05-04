import IconButton from "@material-ui/core/IconButton";
import Tooltip from "@material-ui/core/Tooltip";
import TableChart from "@material-ui/icons/TableChart";
import classNames from "classnames";
import React, { useCallback, useContext, useEffect, useState } from "react";
import { Modal } from "../../components/portal/Modal";
import { templateGet } from "../../context/actions/template";
import { GlobalContext, StateProps } from "../../context/Context";
import { useStyles as useEffectStyles } from "../../theme/styles/effectStyles";
import { CardProps } from "../../types/card";
import { CardTemplateAttribute } from "../../types/cardTemplate";
import { api } from "../../utils/api";
import { getAttributeTypeDefaultValue } from "../../utils/attribute";
import { CardCreateCloseConfirm } from "./CardCreateCloseConfirm";
import { CardCreateForm, InitValuesProps } from "./CardCreateForm";
import { CardCreateTemplate } from "./CardCreateTemplate";
import { useStyles } from "./_cardCreateStyles";
import { flattenCategory, parseLabel } from "./_utils";

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
      let card: any;
      api()
        .get(`card/${item.id}/contents`)
        .json()
        .then((res: any) => {
          card = res;
          return;
        });
      if (card) {
        api()
          .get(`card/${item.id}/content`)
          .json()
          .then((content: any) => {
            const init = {
              ...card,
              attributes: content[0].attributes,
              labels: card.labels.map(parseLabel),
              categories: flattenCategory(card.categories),
              cardContentId: content[0].id,
            };
            setInitValues(init);
          });
      }
    } else if (attributeTemplates && attributeTemplates.length) {
      setAttributes(attributeTemplates);
    }
  }, [attributeTemplates, setAttributes, item]);

  // common template has no owner
  useEffect(() => {
    loadCardContent();
  }, [loadCardContent]);

  useEffect(() => {}, [state.template]);

  useEffect(() => {
    if (open) {
      loadTemplates();
    }
    if (open && item) {
      setType("create");
      loadCardContent();
    }
    if (!open) {
      setInitValues(defaultValues);
    }
  }, [item, open, loadCardContent, loadTemplates]);

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
