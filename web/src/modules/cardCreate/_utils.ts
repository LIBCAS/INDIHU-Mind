import uuid from "uuid/v4";
import { api } from "../../utils/api";
import { flattenDeep } from "lodash";

import {
  STATUS_ERROR_COUNT_CHANGE,
  STATUS_ERROR_TEXT_SET
} from "../../context/reducers/status";

import { AttributeProps } from "../../types/attribute";
import { CardTemplateProps } from "../../types/cardTemplate";
import { CategoryProps } from "../../types/category";
import { LabelProps } from "../../types/label";
import { FileProps } from "../../types/file";
import { OptionType } from "../../components/form/reactSelect/_reactSelectTypes";
import {
  translateFileError,
  FileErrors,
  parseAttributeForApi
} from "../../utils/card";

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

const newCard = (values: any, cardId: string) => {
  const formData = new FormData();
  const {
    attributes,
    categories,
    labels,
    name,
    note,
    linkedCards,
    records
  } = values;

  formData.append("id", cardId);
  formData.append("name", name);
  formData.append("note", note);
  categories.forEach((opt: any, i: number) =>
    formData.append(`categories[${i}]`, opt.value)
  );
  labels.forEach((opt: any, i: number) =>
    formData.append(`labels[${i}]`, opt.value)
  );
  if (records) {
    records.forEach((opt: any, i: number) =>
      formData.append(`records[${i}]`, opt.value)
    );
  }
  linkedCards.forEach((card: any, i: number) =>
    formData.append(`linkedCards[${i}]`, card.id)
  );
  const attributesParsed = attributes.map(parseAttributeForApi);
  attributesParsed.forEach((att: any, i: number) => {
    formData.append(`attributes[${i}].name`, att.name);
    formData.append(`attributes[${i}].ordinalNumber`, att.ordinalNumber);
    formData.append(`attributes[${i}].type`, att.type);
    formData.append(`attributes[${i}].value`, att.value);
  });
  return formData;
};

export const filesUpload = (files: FileProps[], cardId: string) => {
  if (files) {
    return files.map((file: FileProps, i: number) => {
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
      if (
        file.providerType &&
        file.providerType !== "LOCAL" &&
        file.providerId
      ) {
        formData.append(`providerType`, file.providerType);
        formData.append(`providerId`, file.providerId);
        formData.append(`link`, file.link);
      }
      return api({ noContentType: true })
        .post(`attachment-file`, { body: formData })
        .json<any>();
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
  const { files } = values;
  api({ noContentType: true })
    .post(`card`, { body: cardBody })
    .json<any[]>()
    .then(() => {
      const filesPromises = filesUpload(files, cardId);
      return Promise.all(filesPromises.map(p => p.catch(e => e)));
    })
    .then((results: any) => {
      let filesErrors = "";
      results.forEach((r: any, i: number) => {
        if (r.response && r.response.errorType) {
          filesErrors += `| ${translateFileError(r.response
            .errorType as FileErrors)}: ${
            r.response.errorType === "FILE_TOO_BIG" ? `${files[i].name} ` : ""
          }${r.response.errorMessage} `;
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
          filesErrors === ""
            ? "Nová karta byla úspěšně vytvořena"
            : `Nová karta byla vytvořena. ${filesErrors}`
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

export const defaultValue = (type: string) => {
  switch (type) {
    case "STRING":
      return "";
    case "DOUBLE":
      return 0;
    case "BOOLEAN":
      return false;
    case "DATETIME":
      return new Date();
  }
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
