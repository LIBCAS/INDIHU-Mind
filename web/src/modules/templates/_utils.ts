import uuid from "uuid/v4";

import {
  STATUS_LOADING_COUNT_CHANGE,
  STATUS_ERROR_COUNT_CHANGE,
  STATUS_ERROR_TEXT_SET
} from "../../context/reducers/status";

import { api } from "../../utils/api";
import {
  CardTemplateAttribute,
  CardTemplateProps
} from "../../types/cardTemplate";

export const onDeleteTemplate = (
  templateId: string,
  templateGet: Function,
  dispatch: Function
) => {
  dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: 1 });
  api()
    .delete(`card/template/${templateId}`)
    .then(() => {
      dispatch({
        type: STATUS_ERROR_TEXT_SET,
        payload: "Šablona byla úspěšně odstraněna"
      });
      dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
      dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: -1 });
      templateGet(dispatch);
    })
    .catch(() => {
      dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: -1 });
      dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
    });
};

export const deleteAttribute = (
  formikBagParent: any,
  previousAttribute?: CardTemplateAttribute
) => {
  if (previousAttribute) {
    const attributeTemplates = formikBagParent.values.attributeTemplates.filter(
      (att: CardTemplateAttribute) => att.id !== previousAttribute.id
    );
    formikBagParent.setFieldValue(
      "attributeTemplates",
      attributeTemplates,
      false
    );
  }
};
export const onSubmitAttribute = (
  values: CardTemplateAttribute,
  formikBagParent: any,
  setOpen: Function,
  previousAttribute?: CardTemplateAttribute
) => {
  const { attributeTemplates } = formikBagParent.values;
  let orderedAttributes;
  if (previousAttribute) {
    const attributeTemplatesPreviousId = attributeTemplates.map(
      (att: CardTemplateAttribute) =>
        att.id === previousAttribute.id ? values : att
    );
    orderedAttributes = attributeTemplatesPreviousId.map(
      (att: CardTemplateAttribute, i: number) => ({ ...att, ordinalNumber: i })
    );
  } else {
    values.id = uuid();
    const merged = [...attributeTemplates, values];
    orderedAttributes = merged.map((att: CardTemplateAttribute, i: number) => ({
      ...att,
      ordinalNumber: i
    }));
  }
  formikBagParent.setFieldValue("attributeTemplates", orderedAttributes, false);
  setOpen(false);
};

export const onSubmitTemplate = (
  values: any,
  setShowModal: Function,
  setError: Function,
  setLoading: Function,
  templateGet: Function,
  dispatch: any,
  template?: CardTemplateProps
) => {
  let id = uuid();
  if (template) {
    id = template.id;
  }
  const body = {
    id,
    name: values.name,
    attributeTemplates: values.attributeTemplates
  };
  api()
    .put(`card/template/${id}`, { json: body })
    .json<any[]>()
    .then(() => {
      dispatch({
        type: STATUS_ERROR_TEXT_SET,
        payload: template
          ? `Šablona ${values.name} byla úspěšně změněna`
          : `Šablona ${values.name} byla úspěšně vytvořena`
      });
      dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
      setLoading(false);
      setShowModal(false);
      setError(false);
      templateGet(dispatch);
    })
    .catch(() => {
      setLoading(false);
      setError(true);
    });
};
