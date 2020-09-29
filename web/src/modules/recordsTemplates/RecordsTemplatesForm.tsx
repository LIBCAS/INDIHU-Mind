import React, { useState, useEffect, useContext, useMemo } from "react";
import HTML5Backend from "react-dnd-html5-backend";
import classNames from "classnames";
import { withRouter, RouteComponentProps } from "react-router-dom";
import { get, find, filter } from "lodash";
import { Typography } from "@material-ui/core";
import { Button } from "@material-ui/core";
import { Formik, Form, FormikProps, Field, FieldProps } from "formik";
import uuid from "uuid/v4";
import { DndProvider } from "react-dnd";

import { GlobalContext, StateProps } from "../../context/Context";
import { Loader } from "../../components/loader/Loader";
import { MessageSnackbar } from "../../components/messages/MessageSnackbar";

import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";

import { RecordTemplateProps } from "../../types/recordTemplate";
import DnD from "./dnd/DnD";
import { DnDSelection } from "./dnd/DnDSelection";
import { Item } from "./dnd/_types";
import { notEmpty } from "../../utils/form/validate";
import { InputText } from "../../components/form/InputText";
import { Divider } from "../../components/divider/Divider";
import { api } from "../../utils/api";
import {
  STATUS_ERROR_TEXT_SET,
  STATUS_ERROR_COUNT_CHANGE
} from "../../context/reducers/status";
import { parseTemplate, createStyle, createMarcId, isCreator } from "./_utils";
import { useStyles } from "./_recordsTemplatesStyles";
import { specialTags, punctuation, otherTags } from "./_enums";

type RecordsTemplatesFormValues = RecordTemplateProps;

interface RecordsTemplatesFormProps {
  setShowModal: Function;
  recordTemplate?: RecordTemplateProps;
  afterEdit?: () => void;
}

const RecordsTemplatesFormView: React.FC<RecordsTemplatesFormProps &
  RouteComponentProps> = ({
  setShowModal,
  recordTemplate,
  afterEdit,
  history
}) => {
  const classesSpacing = useSpacingStyles();
  const classes = useStyles();
  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;
  const state: StateProps = context.state;
  const { marc } = state.record;

  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<boolean | string>(false);
  const [
    initValues,
    setInitValues
  ] = useState<RecordsTemplatesFormValues | null>(null);

  const [cardsInit, setCardsInit] = useState<any[]>([]);
  const [cards, setCards] = useState<Item[]>([]);

  useEffect(() => {
    setCards(cardsInit);
  }, [initValues]);

  useEffect(() => {
    if (recordTemplate) {
      const { cardsInit, initValuesParsed } = parseTemplate(
        recordTemplate,
        marc
      );
      setInitValues(initValuesParsed as any);
      setCardsInit(cardsInit);
    }
  }, [recordTemplate]);

  const tags = useMemo(
    () =>
      marc
        ? [
            ...specialTags,
            ...filter(marc, ({ tag }) => !isCreator(tag)).map(
              ({ czech, ...m }: any) => {
                return {
                  ...m,
                  id: createMarcId(m),
                  text: czech
                };
              }
            )
          ]
        : [],
    [marc]
  );

  const initCards = [...tags, ...punctuation, ...otherTags];

  return (
    <>
      <Formik
        validateOnChange
        initialValues={initValues}
        enableReinitialize
        onSubmit={(values: any) => {
          if (loading) return false;
          setLoading(true);

          const fields = cards.map(({ id, text, count, ...c }: any) => {
            const getValue = (path: string) => values[id + count + path];
            const customizations = getValue("customizations");
            return {
              ...(/^MARC/.test(id) ? { type: "MARC", ...c } : { type: id }),
              ...(customizations.length ? { customizations } : {}),
              ...(id === "AUTHOR"
                ? {
                    firstNameFormat: getValue("firstNameFormat"),
                    multipleAuthorsFormat: getValue("multipleAuthorsFormat"),
                    orderFormat: getValue("orderFormat")
                  }
                : {})
            };
          });

          const payload = {
            id: recordTemplate ? recordTemplate.id : uuid(),
            name: values.name,
            fields
          };

          api()
            .put(`template/${payload.id}`, {
              json: payload
            })
            .json<any[]>()
            .then((res: any) => {
              dispatch({
                type: STATUS_ERROR_TEXT_SET,
                payload: recordTemplate
                  ? `Citační šablona ${values.name} byla úspěšně změněna`
                  : `Nová citační šablona ${values.name} byla úspěšně vytvořena`
              });
              dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
              setLoading(false);
              if (afterEdit) {
                afterEdit();
              }
              history.push(`/template/${payload.id}`);
              setShowModal(false);
            })
            .catch(err => {
              setLoading(false);
              setError(
                get(err, "response.errorType") === "ERR_NAME_ALREADY_EXISTS"
                  ? "Citační šablona se zvoleným názvem již existuje."
                  : true
              );
            });
        }}
        render={(formikBag: FormikProps<any>) => (
          <Form className={classes.recordTemplateForm}>
            <DndProvider backend={HTML5Backend}>
              <Loader loading={loading} />
              {error && (
                <MessageSnackbar setVisible={setError} message={error} />
              )}
              <div className={classes.recordTemplateFormLeftPanel}>
                {[
                  { initCards: tags },
                  { initCards: punctuation, label: "Interpunkce" },
                  { initCards: otherTags, label: "Ostatní" }
                ].map((c, i) => (
                  <DnDSelection
                    key={`${c.label}${i}`}
                    {...c}
                    cards={cards}
                    setCards={setCards}
                    formikBag={formikBag}
                  />
                ))}
              </div>
              <div className={classes.recordTemplateFormRightPanel}>
                <div className={classes.recordTemplateFormMainPanel}>
                  <Field
                    name="name"
                    validate={notEmpty}
                    render={({ field, form }: FieldProps<any>) => (
                      <InputText
                        label="Název"
                        type="text"
                        field={field}
                        form={form}
                        autoFocus={false}
                      />
                    )}
                  />
                  <div className={classesSpacing.mt3} />
                  <DnD
                    initCards={initCards}
                    cards={cards}
                    setCards={setCards}
                    formikBag={formikBag}
                  />
                  <div className={classNames(classesSpacing.mb2)} />
                  {cards.length ? (
                    <div>
                      <Typography variant="subtitle1" gutterBottom>
                        Citace
                      </Typography>
                      <div className={classes.preview}>
                        {cards.map(({ id, text, count }, i) => {
                          const customizations =
                            formikBag.values[id + count + "customizations"];

                          return (
                            <span
                              key={`${id}${i}`}
                              style={createStyle(customizations)}
                            >
                              {(() => {
                                const type = id.replace(/-.*$/, "");

                                if (type === "MARC") {
                                  return text;
                                }

                                const otherType = find(
                                  otherTags,
                                  o => o.id === type
                                );

                                if (otherType) {
                                  return get(otherType, "text");
                                }

                                switch (type) {
                                  case "AUTHOR":
                                    return "Tvůrce";
                                  case "PERIOD":
                                    return ".";
                                  case "COLON":
                                    return ":";
                                  case "COMMA":
                                    return ",";
                                  case "SEMICOLON":
                                    return ";";
                                  case "SPACE":
                                    return " ";
                                  default:
                                    return "";
                                }
                              })()}
                            </span>
                          );
                        })}
                      </div>
                    </div>
                  ) : (
                    <></>
                  )}
                </div>
                <Divider />
                <div className={classes.recordTemplateFormButtonsPanel}>
                  <Button variant="contained" color="primary" type="submit">
                    {recordTemplate
                      ? "Změnit citační šablonu"
                      : "Vytvořit citační šablonu"}
                  </Button>
                </div>
              </div>
            </DndProvider>
          </Form>
        )}
      />
    </>
  );
};

export const RecordsTemplatesForm = withRouter(RecordsTemplatesFormView);
