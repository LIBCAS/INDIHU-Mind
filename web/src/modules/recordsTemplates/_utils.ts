import { v4 as uuid } from "uuid";
import { get, find } from "lodash";

import {
  STATUS_ERROR_TEXT_SET,
  STATUS_ERROR_COUNT_CHANGE,
  STATUS_LOADING_COUNT_CHANGE,
} from "./../../context/reducers/status";
import {
  RecordTemplateProps,
  FieldsEntity,
} from "./../../types/recordTemplate";
import { api } from "./../../utils/api";
import { MarcEntity } from "../../types/record";
import { specialTags, punctuation, otherTags } from "./_enums";
import { createCreatorRegex } from "../records/_utils";

export const onSubmitRecordTemplate = (
  values: RecordTemplateProps,
  setShowModal: Function,
  setError: Function,
  setLoading: Function,
  recordTemplateGet: Function,
  dispatch: any,
  record?: RecordTemplateProps
) => {
  let id = uuid();
  if (record) {
    id = record.id;
  }
  const body = values;
  api()
    .put(`template/${id}`, { json: body })
    .json<any[]>()
    .then(() => {
      dispatch({
        type: STATUS_ERROR_TEXT_SET,
        payload: record
          ? `Šablona ${values.name} byla úspěšně změněna`
          : `Šablona ${values.name} byla úspěšně vytvořena`,
      });
      dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
      setLoading(false);
      setShowModal(false);
      setError(false);
      recordTemplateGet(dispatch);
    })
    .catch(() => {
      setLoading(false);
      setError(true);
    });
};

export const onDeleteRecordTemplate = (
  recordTemplateId: string,
  recordTemplateGet: Function,
  dispatch: Function
) => {
  dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: 1 });
  api()
    .delete(`template/${recordTemplateId}`)
    .then(() => {
      dispatch({
        type: STATUS_ERROR_TEXT_SET,
        payload: "Citační šablona byla úspěšně odstraněna",
      });
      dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
      dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: -1 });
      recordTemplateGet(dispatch);
    })
    .catch(() => {
      dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: -1 });
      dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
    });
};

export const isCreator = (tag?: string) =>
  tag === "100" || tag === "110" || tag === "700" || tag === "710";

export const isIndicatorEmpty = (indicator?: string) =>
  !indicator || indicator === "#";

export const compareIndicators = (indicator1?: string, indicator2?: string) =>
  (isIndicatorEmpty(indicator1) && isIndicatorEmpty(indicator2)) ||
  indicator1 === indicator2;

export const findMarc = (
  fieldEntity: Omit<FieldsEntity, "type">,
  marcFields: MarcEntity[]
) =>
  find(
    marcFields,
    ({ tag, code, indicator1, indicator2 }) =>
      tag === fieldEntity.tag &&
      code === fieldEntity.code &&
      compareIndicators(indicator1, fieldEntity.indicator1) &&
      compareIndicators(indicator2, fieldEntity.indicator2)
  );

export const getCreatorLabel = (isCorporate: boolean, code: string) => {
  let text;

  switch (code) {
    case "e":
      text = "Editor";
      break;
    default:
      text = "Autor";
      break;
  }

  return `${text}${isCorporate ? " (Název společnosti)" : ""}`;
};

export const getCreatorLabelByTag = (tag: string, code: string) =>
  getCreatorLabel(tag === "110" || tag === "710", code);

export const formatIndicator = (indicator1?: string, indicator2?: string) =>
  !isIndicatorEmpty(indicator1)
    ? ` ${indicator1}`
    : !isIndicatorEmpty(indicator2)
    ? " #"
    : "";

export const createMarcLabel = ({
  tag,
  code,
  indicator1,
  indicator2,
}: {
  tag: string;
  code?: string;
  indicator1?: string;
  indicator2?: string;
}) =>
  `${tag} ${code}${formatIndicator(indicator1, indicator2)}${formatIndicator(
    indicator2,
    indicator1
  )}`;

export const isCorporate = (value: string) => /^_/.test(value);

export const clearCreatorValue = (value: string) => value.replace(/_/, "");

export const clearAuthorData = (data?: string) =>
  data ? data.replace(createCreatorRegex(), " ") : data;

export const getMarcLabel = (
  fieldEntity: Omit<FieldsEntity, "type">,
  marcFields: MarcEntity[]
) => {
  const { tag, code } = fieldEntity;
  if (tag && code && isCreator(tag)) {
    return getCreatorLabelByTag(tag, code);
  }
  return get(findMarc(fieldEntity, marcFields), "czech", "Neznámé");
};

export const createMarcId = (m: Partial<MarcEntity>) =>
  `MARC-${m.tag}-${m.code}${
    !isIndicatorEmpty(m.indicator1) ? `-${m.indicator1}` : ""
  }${!isIndicatorEmpty(m.indicator2) ? `-${m.indicator2}` : ""}`;

export const parseTemplate = (
  recordTemplate: RecordTemplateProps,
  marcFields: MarcEntity[]
) => {
  let counts: any = {};
  let cardsInit: any[] = [];
  if (recordTemplate.fields) {
    recordTemplate.fields.forEach(({ type, ...f }) => {
      const isMarc = type === "MARC";
      const isCustom = type === "CUSTOM";
      const id = isMarc ? createMarcId(f) : type;
      counts[id] = get(counts, id, -1) + 1;
      cardsInit.push({
        ...f,
        id,
        count: counts[id],
        text: isMarc
          ? get(findMarc(f, marcFields), "czech", "Neznámé")
          : isCustom
          ? get({ ...f }, "text", "Neznámé")
          : get(
              find(
                [...specialTags, ...punctuation, ...otherTags],
                ({ id }) => id === type
              ),
              "text",
              "Neznámé"
            ),
      });
    });
  }

  let initValuesParsed: any = {};
  cardsInit.forEach((c) =>
    [
      "customizations",
      ...(c.id === "AUTHOR"
        ? ["firstNameFormat", "multipleAuthorsFormat", "orderFormat"]
        : []),
    ].forEach((path) => {
      initValuesParsed[c.id + c.count + path] = get(c, path);
    })
  );
  initValuesParsed.name = recordTemplate.name;

  return {
    cardsInit,
    initValuesParsed,
  };
};

export const createStyle = (customizations: string[], style = {}) =>
  ({
    ...style,
    ...(customizations.includes("BOLD") && {
      fontWeight: 600,
    }),
    ...(customizations.includes("ITALIC") && {
      fontStyle: "italic",
    }),
    ...(customizations.includes("UPPERCASE") && {
      textTransform: "uppercase",
    }),
  } as any);
