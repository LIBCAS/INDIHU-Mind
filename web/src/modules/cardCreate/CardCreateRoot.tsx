import React, { useState, useEffect, useContext } from "react";
import TableChart from "@material-ui/icons/TableChart";
import classNames from "classnames";
import Tooltip from "@material-ui/core/Tooltip";
import IconButton from "@material-ui/core/IconButton";

import { templateGet } from "../../context/actions/template";
import { GlobalContext, StateProps } from "../../context/Context";
import { CardTemplateAttribute } from "../../types/cardTemplate";
import { Modal } from "../../components/portal/Modal";
import { api } from "../../utils/api";
import { CardProps } from "../../types/card";

import { CardCreateForm, InitValuesProps } from "./CardCreateForm";
import { CardCreateTemplate } from "./CardCreateTemplate";
import { defaultValue, parseLabel, flattenCategory } from "./_utils";

import { useStyles as useEffectStyles } from "../../theme/styles/effectStyles";
import { useStyles } from "./_cardCreateStyles";

interface CardCreateRootProps {
  showModal: boolean;
  setShowModal: Function;
  selectedRow?: CardProps;
  edit?: boolean;
  afterEdit?: Function;
}

export const CardCreateRoot: React.FC<CardCreateRootProps> = ({
  showModal,
  setShowModal,
  selectedRow,
  edit,
  afterEdit
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

  const loadTemplates = () => {
    templateGet(dispatch);
  };
  const loadCardContent = () => {
    if (!selectedRow) return;
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

  const setAttributes = (attributes: CardTemplateAttribute[]) => {
    const transformed = attributes.map(a => ({
      ...a,
      value: defaultValue(a.type)
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
  return (
    <>
      <Modal
        open={showModal}
        setOpen={setShowModal}
        content={
          <>
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
          </>
        }
        contentOutside={
          <>
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
          </>
        }
      />
    </>
  );
};
