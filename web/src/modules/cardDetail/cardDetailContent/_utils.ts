import { get } from "lodash";
import { v4 as uuid } from "uuid";
import { AttributeType } from "../../../enums";
import { AttributeProps } from "../../../types/attribute";
// import { parseAttributeForApi } from "../../../utils/card";
import { CardContentProps, CardProps } from "../../../types/card";
import { api } from "../../../utils/api";
import { getAttributeTypeDefaultValue } from "../../../utils/attribute";
import { parseAttributeForApi } from "../../../utils/card";

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
    const transformed = prevCardContent.map((c) => {
      if (c.id === card.id) {
        return {
          ...c,
          ...(outsideCardFields.includes(field) && { [field]: value }),
          card: {
            ...c.card,
            ...(!outsideCardFields.includes(field) && { [field]: value }),
          },
        };
      } else {
        // do not update attributes for older versions
        return {
          ...c,
          ...(!outsideCardFields.includes(field) && { [field]: value }),
          card: {
            ...c.card,
            ...(!outsideCardFields.includes(field) && { [field]: value }),
          },
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
  setCard: React.Dispatch<React.SetStateAction<CardProps | undefined>>,
  currentCardContent: CardContentProps,
  setCardContents: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >
) => {
  if (field !== "attributes")
    setCard((prev) => (prev ? { ...prev, [field]: value } : undefined));
  else
    setCardContents((prevCardContent) => {
      return transformCardContent(
        field,
        value,
        prevCardContent,
        currentCardContent
      );
    });
};

export const onEditCard = (
  field: string,
  value: any,
  card: CardProps,
  setCard: React.Dispatch<React.SetStateAction<CardProps | undefined>>,
  currentCardContent: CardContentProps,
  setCurrentCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >,
  onSuccess = () => { },
  onError = (e: Error) => { }
) => {
  updateCardContent(
    field,
    value,
    setCard,
    currentCardContent,
    setCurrentCardContent
  );

  const createIds = (o: any) => o.id;

  const mapFieldValue = (field: string, newField?: string) => {
    const fieldValue = card[`${field}`];
    const value =
      fieldValue &&
        typeof fieldValue !== "string" &&
        typeof fieldValue !== "number"
        ? (fieldValue as any).map(createIds)
        : null;
    return value ? { [`${newField || field}`]: value } : {};
  };

  let { id, name, note, comments } = card;

  const fieldsVersioned = ["attributes"];

  const arrayIdFields = [
    "categories",
    "labels",
    "linkedCards",
    "linkingCards",
    "records",
    "documents",
    "files",
  ];

  const hasVersions = fieldsVersioned.includes(field);

  if (!controller.signal.aborted) {
    controller.abort();
  }

  controller = new AbortController();

  if (hasVersions) {
    const body = {
      newVersion: true,
      [field]: value,
    };
    api()
      .put(`card/${currentCardContent.card.id}/content`, {
        json: body,
        signal: controller.signal,
      })
      .then(onSuccess);
  } else {
    let rawNote;
    if (field === "note") {
      note = value;
    }
    if (note) {
      try {
        rawNote = get(JSON.parse(note), "blocks", [])
          .map(({ text }: { text: string }) => text)
          .join(" ");
      } catch {
        rawNote = undefined;
      }
    }

    const body = {
      ...mapFieldValue("categories"),
      ...mapFieldValue("labels"),
      ...mapFieldValue("linkedCards"),
      ...mapFieldValue("linkingCards"),
      ...mapFieldValue("files"),
      ...mapFieldValue("documents", "files"),
      ...mapFieldValue("records"),
      name,
      note,
      comments,
      rawNote,
      [field]: arrayIdFields.includes(field) ? value.map(createIds) : value,
    };

    api()
      .put(`card/${id}`, {
        json: body,
        signal: controller.signal,
      })
      .then(onSuccess)
      .catch(onError);
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
  card: CardProps,
  setCard: React.Dispatch<React.SetStateAction<CardProps | undefined>>,
  currentCardContent: CardContentProps,
  setCurrentCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >,
  setOpen: Function,
  previousAttribute?: AttributeProps
) => {
  const { attributes } = currentCardContent;
  if (values.type === AttributeType.DATE || AttributeType.DATETIME) {
    values = parseAttributeForApi(values);
  }
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
      ordinalNumber: i,
    }));
  }
  onEditCard(
    "attributes",
    orderedAttributes,
    card,
    setCard,
    currentCardContent,
    setCurrentCardContent
  );
  setOpen(false);
};

export const onDeleteAttribute = (
  card: CardProps,
  setCard: React.Dispatch<React.SetStateAction<CardProps | undefined>>,
  currentCardContent: CardContentProps,
  setCurrentCardContent: React.Dispatch<
    React.SetStateAction<CardContentProps[] | undefined>
  >,
  setOpen: Function,
  previousAttribute: AttributeProps
) => {
  const { attributes } = currentCardContent;
  let orderedAttributes;
  const attributesPreviousId = attributes.filter(
    (att: AttributeProps) => att.id !== previousAttribute.id
  );
  orderedAttributes = attributesPreviousId.map(
    (att: AttributeProps, i: number) => ({ ...att, ordinalNumber: i })
  );
  onEditCard(
    "attributes",
    orderedAttributes,
    card,
    setCard,
    currentCardContent,
    setCurrentCardContent
  );
  setOpen(false);
};

export const onChangeType = (formikBag: any, value: any) => {
  formikBag.setFieldValue("value", getAttributeTypeDefaultValue(value));
};

export const isNoteTextEmpty = (note: any): boolean =>
  note.blocks.reduce(
    (previousEmpty: boolean, block: any) =>
      previousEmpty && block.text.length === 0,
    true
  );

export const onNoteUploadError = (
  setError: (o: any) => void,
  afterErrorCB: () => void
) => (e: any) => {
  const errMessage =
    e.response && e.response.details && e.response.details.field === "note"
      ? e.response.status === 400
        ? "Popis přesahuje povolenou velikost 10MB."
        : e.response.status === 409
          ? "Popis přesahuje celkovou kvotu uživatele."
          : "Nepodařilo se změnit popis"
      : "Nepodařilo se upravit kartu";
  setError && setError({ isError: true, message: errMessage });
  afterErrorCB();
};
