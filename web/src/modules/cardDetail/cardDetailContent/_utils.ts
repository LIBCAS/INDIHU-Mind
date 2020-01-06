import uuid from "uuid/v4";

import { api } from "../../../utils/api";
// import { parseAttributeForApi } from "../../../utils/card";
import { CardContentProps } from "../../../types/card";
import { AttributeProps } from "../../../types/attribute";
import { FileProps } from "../../../types/file";
// import { CategoryProps } from "../../types/category";
// import { STATUS_ERROR_COUNT_CHANGE, STATUS_ERROR_TEXT_SET, STATUS_LOADING_COUNT_CHANGE } from '../../context/reducers/status';

export const fileUpload = (file: FileProps, cardId: string) => {
  const formData = new FormData();
  formData.append(`cardId`, cardId);
  formData.append(`id`, file.id);
  formData.append(`name`, file.name);
  formData.append(`type`, file.type);
  formData.append(`ordinalNumber`, file.ordinalNumber.toString());
  if (file.providerType === "LOCAL" && file.content) {
    formData.append(`providerType`, file.providerType);
    formData.append(`content`, file.content);
  }
  if (file.providerType && file.providerType !== "LOCAL" && file.providerId) {
    formData.append(`providerType`, file.providerType);
    formData.append(`providerId`, file.providerId);
    formData.append(`link`, file.link);
  }
  return api({ noContentType: true })
    .post(`attachment_file`, { body: formData })
    .json<any>();
};

export const fileDelete = (id: string) => {
  api().delete(`attachment_file/${id}`);
};

const transformCardContent = (
  field: string,
  value: any,
  prevCardContent: CardContentProps[] | undefined,
  card: CardContentProps
) => {
  // fields with version history
  const outsideCardFields = ["attributes"];
  if (prevCardContent) {
    const transformed = prevCardContent.map(c => {
      if (c.id === card.id) {
        return {
          ...c,
          ...(outsideCardFields.includes(field) && { [field]: value }),
          card: {
            ...c.card,
            ...(!outsideCardFields.includes(field) && { [field]: value })
          }
        };
      } else {
        // do not update attributes for older versions
        return {
          ...c,
          ...(!outsideCardFields.includes(field) && { [field]: value }),
          card: {
            ...c.card,
            ...(!outsideCardFields.includes(field) && { [field]: value })
          }
        };
      }
    });
    return transformed;
  } else {
    return undefined;
  }
};

let controller = new AbortController();

export const updateCardContent = (
  field: string,
  value: any,
  card: CardContentProps,
  setCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >
) => {
  setCardContent(prevCardContent => {
    return transformCardContent(field, value, prevCardContent, card);
  });
};

export const onEditCard = (
  field: string,
  value: any,
  card: CardContentProps,
  setCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >
) => {
  updateCardContent(field, value, card, setCardContent);
  const { id, name, note, categories, labels, linkedCards } = card.card;
  const createIds = (o: any) => o.id;
  const fieldsVersioned = ["attributes"];
  const arrayIdFields = ["categories", "labels", "linkedCards", "records"];
  const hasVersions = fieldsVersioned.includes(field);
  if (!controller.signal.aborted) controller.abort();
  controller = new AbortController();
  if (hasVersions) {
    const body = {
      newVersion: true,
      [field]: value
    };
    api().put(`card/${card.card.id}/content`, {
      json: body,
      signal: controller.signal
    });
  } else {
    const body = {
      categories: categories && categories.map(createIds),
      labels: labels && labels.map(createIds),
      linkedCards: linkedCards && linkedCards.map(createIds),
      name,
      note,
      [field]: arrayIdFields.includes(field) ? value.map(createIds) : value
    };
    api().put(`card/${id}`, {
      json: body,
      signal: controller.signal
    });
  }
};

export const deleteAttribute = (
  formikBagParent: any,
  previousAttribute?: AttributeProps
) => {
  if (previousAttribute) {
    const attributes = formikBagParent.values.attributes.filter(
      (att: AttributeProps) => att.id !== previousAttribute.id
    );
    formikBagParent.setFieldValue("attributes", attributes, false);
  }
};
export const onSubmitAttribute = (
  values: AttributeProps,
  card: CardContentProps,
  setCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >,
  setOpen: Function,
  previousAttribute?: AttributeProps
) => {
  const { attributes } = card;
  let orderedAttributes;
  if (previousAttribute) {
    const attributesPreviousId = attributes.map((att: AttributeProps) =>
      att.id === previousAttribute.id ? values : att
    );
    orderedAttributes = attributesPreviousId.map(
      (att: AttributeProps, i: number) => ({ ...att, ordinalNumber: i })
    );
  } else {
    values.id = uuid();
    const merged = [...attributes, values];
    orderedAttributes = merged.map((att: AttributeProps, i: number) => ({
      ...att,
      ordinalNumber: i
    }));
  }
  onEditCard("attributes", orderedAttributes, card, setCardContent);
  setOpen(false);
};

export const onDeleteAttribute = (
  card: CardContentProps,
  setCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >,
  setOpen: Function,
  previousAttribute: AttributeProps
) => {
  const { attributes } = card;
  let orderedAttributes;
  const attributesPreviousId = attributes.filter(
    (att: AttributeProps) => att.id !== previousAttribute.id
  );
  orderedAttributes = attributesPreviousId.map(
    (att: AttributeProps, i: number) => ({ ...att, ordinalNumber: i })
  );
  onEditCard("attributes", orderedAttributes, card, setCardContent);
  setOpen(false);
};

export const onChangeType = (formikBag: any, e: any) => {
  switch (e.target.value) {
    case "STRING":
    case "DOUBLE":
      return formikBag.setFieldValue("value", "");
    case "BOOLEAN":
      return formikBag.setFieldValue("value", false);
    case "DATETIME":
      return formikBag.setFieldValue("value", new Date());
  }
};
