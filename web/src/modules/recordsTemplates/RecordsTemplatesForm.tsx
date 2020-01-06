import React, { useState, useEffect, useContext, useMemo } from "react";
import HTML5Backend from "react-dnd-html5-backend";
import classNames from "classnames";
import { groupBy, set, get, omit, sortBy, pick } from "lodash";
import { withRouter, RouteComponentProps } from "react-router-dom";

import { GlobalContext, StateProps } from "../../context/Context";
import { Loader } from "../../components/loader/Loader";
import { MessageSnackbar } from "../../components/messages/MessageSnackbar";

import { useStyles } from "./_recordsTemplatesStyles";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";

import { RecordTemplateProps } from "../../types/recordTemplate";
import DnD from "./dnd/DnD";
import { DndProvider } from "react-dnd";
import { DnDSelection } from "./dnd/DnDSelection";
import { Item } from "./dnd/_types";
import { Formik, Form, FormikProps, Field, FieldProps } from "formik";
import { notEmpty } from "../../utils/form/validate";
import { InputText } from "../../components/form/InputText";
import { Divider } from "../../components/divider/Divider";
import { Button } from "@material-ui/core";
import uuid from "uuid/v4";
import { api } from "../../utils/api";
import {
  STATUS_ERROR_TEXT_SET,
  STATUS_ERROR_COUNT_CHANGE
} from "../../context/reducers/status";
import { parseTemplate } from "./_utils";

type RecordsTemplatesFormValues = RecordTemplateProps;

interface RecordsTemplatesFormProps {
  setShowModal: Function;
  recordTemplate?: RecordTemplateProps;
  afterEdit?: () => void;
}

const RecordsTemplatesFormView: React.FC<
  RecordsTemplatesFormProps & RouteComponentProps
> = ({ setShowModal, recordTemplate, afterEdit, history }) => {
  const classes = useStyles();
  const classesText = useTextStyles();
  const classesSpacing = useSpacingStyles();
  const classesLayout = useLayoutStyles();
  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;
  const state: StateProps = context.state;
  const marc = state.record.marc;
  let fields: any;
  if (marc) {
    fields = state.record.marc.fields;
  }
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<boolean>(false);
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
      const { cardsInit, initValuesParsed } = parseTemplate(recordTemplate);
      setInitValues(initValuesParsed as any);
      setCardsInit(cardsInit);
    }
  }, [recordTemplate]);

  const initCards = useMemo(
    () =>
      fields
        ? [
            ...Object.keys(fields)
              .filter(key => key !== "LDR")
              .map((key, i) => {
                const card = fields[key];
                return { id: card.tag, text: card.tag, count: 0 };
              }),
            {
              id: "customizations",
              text: "Vlastní text",
              count: 0
            }
          ]
        : [],
    [fields]
  );

  return (
    <>
      <Formik
        validateOnChange
        initialValues={initValues}
        enableReinitialize
        onSubmit={(values: any) => {
          if (loading) return false;
          setLoading(true);
          const fields = cards.map(c => {
            if (c.id === "customizations") {
              return {
                tag: c.id,
                text: values[c.id + c.count]
              };
            } else {
              return {
                tag: c.id,
                code: values[c.id + c.count + "code"],
                customizations: values[c.id + c.count + "customizations"]
              };
            }
          });
          let pattern = ``;
          fields.forEach(f => {
            const value = f.tag === "customizations" ? f.text : `\${?}`;
            pattern += value;
          });
          const fieldsFiltered = fields.filter(f => f.tag !== "customizations");

          const id = recordTemplate ? recordTemplate.id : uuid();
          let payload = {
            id,
            name: values.name,
            pattern,
            fields: fieldsFiltered
          };

          api()
            .put(`template/${id}`, {
              json: payload
            })

            .json<any[]>()
            .then(res => {
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
              history.push(`/template/${id}`);
              setShowModal(false);
            })
            .catch(() => {
              setError(true);
              setLoading(false);
            });
        }}
        render={(formikBag: FormikProps<any>) => (
          <Form className={classNames(classesSpacing.p1)}>
            <Loader loading={loading} />
            {error && <MessageSnackbar setVisible={setError} />}
            <Field
              name="name"
              validate={notEmpty}
              render={({ field, form }: FieldProps<any>) => (
                <InputText
                  label="Název"
                  type="text"
                  field={field}
                  form={form}
                  autoFocus={recordTemplate ? false : true}
                />
              )}
            />
            <div className={classNames(classesSpacing.mb1)} />
            <DndProvider backend={HTML5Backend}>
              <DnDSelection
                initCards={initCards}
                cards={cards}
                setCards={setCards}
              />
              <DnD
                recordTemplate={recordTemplate}
                initCards={initCards}
                cards={cards}
                setCards={setCards}
                formikBag={formikBag}
              />
            </DndProvider>
            <Divider className={classesSpacing.mt3} />
            <div
              className={classNames(
                classesLayout.flex,
                classesLayout.justifyCenter,
                classesSpacing.mb1
              )}
            >
              <Button
                className={classesSpacing.mt3}
                variant="contained"
                color="primary"
                type="submit"
              >
                {recordTemplate
                  ? "Změnit citační šablonu"
                  : "Vytvořit citační šablonu"}
              </Button>
            </div>
          </Form>
        )}
      />
    </>
  );
};

export const RecordsTemplatesForm = withRouter(RecordsTemplatesFormView);
