import uuid from "uuid/v4";
import { flattenDeep, isArray, pick, isEmpty } from "lodash";

import {
  STATUS_ERROR_COUNT_CHANGE,
  STATUS_ERROR_TEXT_SET
} from "../../context/reducers/status";

import { api } from "../../utils/api";
import { AttributeProps } from "../../types/attribute";
import { CardTemplateProps } from "../../types/cardTemplate";
import { CategoryProps } from "../../types/category";
import { LabelProps } from "../../types/label";
import { FileProps } from "../../types/file";
import { OptionType } from "../../components/select/_types";
import { parseAttributeForApi } from "../../utils/card";
import {
  createErrorMessage,
  updateFile,
  uploadFile
} from "../attachments/_utils";
import { getAttributeTypeDefaultValue } from "../../utils/attribute";
import Categories from "../categories/Categories";
import { Labels } from "../../components/tabContent/Labels";

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
  formikBagParent: any,
  setOpen: Function,
  previousAttribute?: AttributeProps
) => {
  const { attributes } = formikBagParent.values;
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
  formikBagParent.setFieldValue("attributes", orderedAttributes, false);
  setOpen(false);
};

export const onChangeType = (formikBag: any, value: any) => {
  formikBag.setFieldValue("value", getAttributeTypeDefaultValue(value));
};

const newCard = (values: any, cardId: string) => {
  const mapArrayValue = (field: any, mapField: string = "value") => {
    const value = values[`${field}`];
    const arr = isArray(value) ? value : value ? [value] : [];
    return !isEmpty(arr)
      ? {
          [`${field}`]: arr.map((opt: any) => opt[`${mapField}`])
        }
      : {};
  };
  return {
    ...pick(values, ["name", "note"]),
    id: cardId,
    attributes: values.attributes.map(parseAttributeForApi),
    categories: values.categories,
    labels: values.labels,
    ...mapArrayValue("records", "id"),
    ...mapArrayValue("linkedCards", "id")
  };
};

export const filesUpload = (files: FileProps[], cardId: string) => {
  if (files) {
    return files.map((file: FileProps) => {
      return file.id
        ? updateFile({
            ...file,
            linkedCards: [...(file.linkedCards || []), cardId]
          })
        : uploadFile({ ...file, linkedCards: [cardId] });
    });
  }
  return [];
};

export const onSubmitCard = (
  values: any,
  setShowModal: Function,
  setError: Function,
  setLoading: Function,
  history: any,
  dispatch: any,
  setErrorMessage: React.Dispatch<React.SetStateAction<string | undefined>>,
  afterEdit?: Function
) => {
  setErrorMessage(undefined);
  const cardId = uuid();
  const cardBody = newCard(values, cardId);
  const { documents } = values;
  api()
    .post(`card`, { json: cardBody })
    .json<any[]>()
    .then(() => {
      const documentsPromises = filesUpload(documents, cardId);
      return Promise.all(documentsPromises.map(p => p.catch((e: any) => e)));
    })
    .then((results: any) => {
      let documentsErrors = "";
      results.forEach((r: any, i: number) => {
        if (r.response && r.response.errorType) {
          documentsErrors += `| ${createErrorMessage(r, documents[i].name)} `;
          // setErrorMessage(translated);
          // history.push(`/card/${cardId}`);
          // if (afterEdit) {
          //   afterEdit();
          // }
        }
      });
      setLoading(false);
      setShowModal(false);
      setError(false);
      dispatch({
        type: STATUS_ERROR_TEXT_SET,
        payload:
          documentsErrors === ""
            ? "Nová karta byla úspěšně vytvořena"
            : `Nová karta byla vytvořena. ${documentsErrors}`
      });
      dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
      history.push(`/card/${cardId}`);
      if (afterEdit) {
        afterEdit();
      }
    })
    .catch(() => {
      setLoading(false);
      setError(true);
    });
};

export const onSubmitTemplate = (
  values: any,
  attributes: AttributeProps[],
  setTemplateOpen: Function,
  loadTemplates: Function,
  setLoading: Function,
  setError: Function,
  dispatch: any
) => {
  const id = uuid();
  api()
    .put(`card/template/${id}`, {
      json: {
        id,
        name: values.name,
        attributeTemplates: attributes
      }
    })
    .json<any[]>()
    .then(() => {
      dispatch({
        type: STATUS_ERROR_TEXT_SET,
        payload: "Vytvoření nové šablony proběhlo úspěšně"
      });
      dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
      setLoading(false);
      loadTemplates();
      setTemplateOpen(false);
    })
    .catch(() => {
      setLoading(false);
      setError(true);
      setTemplateOpen(false);
    });
};

export const getTemplateName = (
  templates: CardTemplateProps[],
  formikBag: any
): string | undefined => {
  let templateExisting = undefined;
  const formAttributesLength = formikBag.values.attributes.length;
  if (formAttributesLength === 0) return undefined;
  templates.forEach(template => {
    let attributeMatch = 0;
    const templateAttributesLength = template.attributeTemplates.length;
    template.attributeTemplates.forEach(attributeTemplate => {
      formikBag.values.attributes.forEach((attribute: AttributeProps) => {
        if (
          attribute.name === attributeTemplate.name &&
          attribute.type === attributeTemplate.type
        ) {
          attributeMatch++;
        }
      });
    });
    if (
      attributeMatch === templateAttributesLength &&
      formAttributesLength === templateAttributesLength
    ) {
      templateExisting = template.name;
    }
  });
  return templateExisting;
};

export const getPathToCategory = (
  cat: CategoryProps,
  categories: CategoryProps[]
): string => {
  let result = "";
  categories.forEach(c => {
    if (c.id === cat.id) {
      result = `<b>${c.name}</b>`;
    }
    if (c.subCategories && c.subCategories.length > 0) {
      const nestedCategory = getPathToCategory(cat, c.subCategories);
      if (nestedCategory !== "") {
        result = `${c.name} > ${nestedCategory}`;
      }
    }
  });
  return result;
};

export const parseCategory = (
  cat: CategoryProps,
  categories: CategoryProps[]
): OptionType => {
  let option: OptionType = {
    label: getPathToCategory(cat, categories),
    value: cat.id
  };
  // if (cat.subCategories && cat.subCategories.length > 0) {
  //   option.subOptions = cat.subCategories.map(parseCategory);
  // }
  return option;
};

const parseSubCategory = (
  cat: CategoryProps,
  categories: CategoryProps[]
): OptionType[] => {
  if (cat.subCategories) {
    const parsedSubcategories = flattenDeep(
      cat.subCategories.map(cat => parseSubCategory(cat, categories))
    ) as OptionType[];
    return [parseCategory(cat, categories), ...parsedSubcategories];
  } else {
    return [parseCategory(cat, categories)];
  }
};

export const flattenCategory = (categories: CategoryProps[]): OptionType[] => {
  let result: OptionType[] = [];
  categories.forEach(cat => {
    result = [...result, ...parseSubCategory(cat, categories)];
  });
  return result;
};

export const parseLabel = (cat: any): OptionType => {
  let option: OptionType = {
    label: cat.name,
    value: cat.id
  };
  return option;
};

export const parseCategoryIds = (cat: CategoryProps): string[] => {
  let ids = [cat.id];
  if (cat.subCategories && cat.subCategories.length > 0) {
    // @ts-ignore
    ids.concat(cat.subCategories.map(parseCategoryIds));
  }
  return ids;
};

export const parseLabelIds = (cat: LabelProps): string => {
  return cat.id;
};

export const getFlatCategories = (
  categories: CategoryProps[]
): CategoryProps[] => {
  let result: CategoryProps[] = [];
  categories.forEach(c => {
    if (c.subCategories) {
      result = [...result, ...getFlatCategories(c.subCategories)];
    }
    result = [...result, c];
  });
  return result;
};
