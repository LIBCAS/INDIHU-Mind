import uuid from "uuid/v4";

import { api } from "../../../utils/api";
// import { parseAttributeForApi } from "../../../utils/card";
import { CardContentProps } from "../../../types/card";
import { AttributeProps } from "../../../types/attribute";
import { getAttributeTypeDefaultValue } from "../../../utils/attribute";
// import { CategoryProps } from "../../types/category";
// import { STATUS_ERROR_COUNT_CHANGE, STATUS_ERROR_TEXT_SET, STATUS_LOADING_COUNT_CHANGE } from '../../context/reducers/status';

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
  >,
  onSuccess = () => {}
) => {
  updateCardContent(field, value, card, setCardContent);

  const createIds = (o: any) => o.id;

  const mapFieldValue = (field: string) => {
    const fieldValue = card.card[`${field}`];
    const value =
      fieldValue &&
      typeof fieldValue !== "string" &&
      typeof fieldValue !== "number"
        ? (fieldValue as any).map(createIds)
        : null;
    return value ? { [`${field}`]: value } : {};
  };

  const { id, name, note } = card.card;

  const fieldsVersioned = ["attributes"];

  const arrayIdFields = [
    "categories",
    "labels",
    "linkedCards",
    "linkingCards",
    "records",
    "documents"
  ];

  const hasVersions = fieldsVersioned.includes(field);

  if (!controller.signal.aborted) {
    controller.abort();
  }

  controller = new AbortController();

  if (hasVersions) {
    const body = {
      newVersion: true,
      [field]: value
    };
    api()
      .put(`card/${card.card.id}/content`, {
        json: body,
        signal: controller.signal
      })
      .then(onSuccess);
  } else {
    const body = {
      ...mapFieldValue("categories"),
      ...mapFieldValue("labels"),
      ...mapFieldValue("linkedCards"),
      ...mapFieldValue("documents"),
      ...mapFieldValue("records"),
      name,
      note,
      [field]: arrayIdFields.includes(field) ? value.map(createIds) : value
    };

    api()
      .put(`card/${id}`, {
        json: body,
        signal: controller.signal
      })
      .then(onSuccess);
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

export const onChangeType = (formikBag: any, value: any) => {
  formikBag.setFieldValue("value", getAttributeTypeDefaultValue(value));
};
