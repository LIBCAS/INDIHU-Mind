import { get, pick, compact, find, filter, findIndex, includes } from "lodash";

import {
  STATUS_ERROR_TEXT_SET,
  STATUS_ERROR_COUNT_CHANGE,
  STATUS_LOADING_COUNT_CHANGE,
} from "../../context/reducers/status";
import { api } from "../../utils/api";
import { FormDataField, RecordProps } from "../../types/record";
import { RecordRequest } from "./RecordsForm";
import { RecordType, RecordTypes, RecordItem, DefaultCreators } from "./_enums";

const url = "record";

export const getRecords = async (text?: string, page = 0, pageSize = 10) => {
  try {
    const response = await api().post(`${url}/parametrized`, {
      json: {
        page,
        pageSize,
        ...(text
          ? { filter: [{ field: "name", operation: "EQ", value: text }] }
          : {}),
        order: "DESC",
      },
    });

    return await response.json();
  } catch (e) {
    console.log(e);
    return null;
  }
};

export const onSubmitRecord = (
  values: RecordRequest,
  setShowModal: Function,
  setError: Function,
  setLoading: Function,
  dispatch: any,
  history: any,
  record?: RecordProps,
  afterEdit?: () => void,
  redirect?: boolean,
  onSubmitCallback?: Function
) => {
  const { documents } = values;
  const func = record ? api().put : api().post;
  func(url, {
    json: {
      ...pick(values, compact(["id", "name", "content", "dataFields"])),
      ...(documents ? { documents: documents.map((d) => d.id) } : {}),
      linkedCards: (values.linkedCards || []).map(({ id }) => id),
    },
  })
    .json()
    .then((res: any) => {
      const newId = res ? res.id : null;
      dispatch({
        type: STATUS_ERROR_TEXT_SET,
        payload: record
          ? `Citace ${values.name} byla úspěšně změněna`
          : `Citace ${values.name} byla úspěšně vytvořena`,
      });
      if (afterEdit) {
        afterEdit();
      }
      dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
      setLoading(false);
      redirect && newId && history.push(`/record/${newId}`);
      setError(false);
      setShowModal(false);
      onSubmitCallback && onSubmitCallback([{ id: newId, name: values.name }]);
    })
    .catch((err) => {
      setLoading(false);
      setError(
        get(err, "response.errorType") === "ERR_NAME_ALREADY_EXISTS"
          ? "Citace se zvoleným názvem již existuje."
          : true
      );
    });
};

export const onDeleteRecord = (
  id: string,
  dispatch: Function,
  afterDelete: Function
) => {
  dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: 1 });
  api()
    .delete(`${url}/${id}`)
    .then(() => {
      dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
      dispatch({
        type: STATUS_ERROR_TEXT_SET,
        payload: "Citace byla úspěšně odstraněna",
      });
      dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: -1 });
      afterDelete();
    })
    .catch(() => {
      dispatch({ type: STATUS_LOADING_COUNT_CHANGE, payload: -1 });
      dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
    });
};

const CREATOR_DELIMINER = "#&&#";

export const createCreatorValue = (firstValue: string, secondValue: string) =>
  `${firstValue}${CREATOR_DELIMINER}${secondValue}`;

export const createCreatorRegex = (
  params: {
    prefix?: string;
    suffix?: string;
  } = {}
) => {
  const { prefix = "", suffix = "" } = params;
  return new RegExp(createCreatorValue(prefix, suffix));
};

export const contentNotEmpty = (content?: string) =>
  content &&
  JSON.parse(content).blocks.reduce(
    (text: string, block: any) => text + block.text,
    ""
  ).length > 0;

const isDelimiter = (value: RecordItem) => {
  switch (value) {
    case RecordItem.IN:
    case RecordItem.DOT:
    case RecordItem.COLON:
    case RecordItem.COMMA:
    case RecordItem.COMMA_OPTIONAL:
    case RecordItem.DOT_OPTIONAL:
    case RecordItem.BRACKET_LEFT:
    case RecordItem.BRACKET_LEFT_OPTIONAL:
    case RecordItem.BRACKET_RIGHT:
    case RecordItem.BRACKET_RIGHT_OPTIONAL:
    case RecordItem.COLON_OPTIONAL:
      return true;
  }
  return false;
};

const isCreator = (value: RecordItem) =>
  value === RecordItem.CREATOR || value === RecordItem.ANOTHER_CREATOR;

export const parseRecord = (
  content: string,
  type: RecordType,
  dataFields: FormDataField[] | null | undefined
) => {
  const { structure, optional = [] } = find(
    RecordTypes,
    ({ value }) => value === type
  ) || { structure: null, optional: [] };
  console.log(optional);
  if (structure) {
    const regex = new RegExp(
      [
        "^\\s*",
        ...structure.map((item) => {
          let part;
          let enableSpace = true;
          switch (item) {
            case RecordItem.IN:
              part = "In:";
              break;
            case RecordItem.DOT_OPTIONAL:
            case RecordItem.DOT:
              part = "\\.";
              break;
            case RecordItem.COLON_OPTIONAL:
            case RecordItem.COLON:
              part = ":";
              break;
            case RecordItem.COMMA_OPTIONAL:
            case RecordItem.COMMA:
              part = ",";
              break;
            case RecordItem.BRACKET_LEFT_OPTIONAL:
            case RecordItem.BRACKET_LEFT:
              part = "\\[";
              enableSpace = false;
              break;
            case RecordItem.BRACKET_RIGHT_OPTIONAL:
            case RecordItem.BRACKET_RIGHT:
              part = "\\]";
              enableSpace = false;
              break;
            case RecordItem.PARENT_NAME:
            case RecordItem.SECONDARY_NAME:
            case RecordItem.CREATOR:
            case RecordItem.PERIODICAL_NAME:
              part = "([^.]+)";
              break;
            case RecordItem.SUBNAME:
              part = ":\\s+([^.]+)";
              break;
            case RecordItem.PARENT_SUBNAME:
              part = ":\\s+(.+)";
              break;
            case RecordItem.DATE_CITATION:
              part = "cit\\.\\s*(.+)";
              break;
            case RecordItem.AVAILABILITY_AND_ACCESS:
              part = "Dostupné(?:[^:]*):\\s*(\\S+)";
              break;
            case RecordItem.ISBN:
              part = "ISBN\\s*([^.]+)";
              break;
            case RecordItem.ISSN:
              part = "ISSN\\s*([^.]+)";
              break;
            case RecordItem.NAME:
              part = "([^:.]+)";
              break;
            case RecordItem.NUMBERING:
              part = "(?!(?:ISBN|ISSN|DOI))(\\D*\\d+[^.]*)";
              break;
            case RecordItem.EDITION_NAME_AND_NUMBERING:
              part = "(?!(?:ISBN|ISSN|DOI))([^.]*)";
              break;
            case RecordItem.PUBLISHER:
              part = "([^\\d.]+)";
              break;
            case RecordItem.RANGE_OF_PAGES:
              part = "(?:\\D*(\\d+(?:-|‑)\\d*))";
              break;
            //optional fields default
            case RecordItem.ANOTHER_CREATOR:
            case RecordItem.PARENT_ANOTHER_CREATOR:
            case RecordItem.LOCATION:
            case RecordItem.DIMENSION:
            case RecordItem.SUPERVISOR:
            case RecordItem.EDITION:
            case RecordItem.NOTES:
            case RecordItem.NUMBERING_OF_VOLUME:
            case RecordItem.PARENT_CREATOR:
              part = "([^.,]*)";
              break;

            default:
              part = "([^.,]+)";
              break;
          }
          const isOptional = includes(
            [
              ...optional,
              RecordItem.SUBNAME,
              RecordItem.PARENT_SUBNAME,
              RecordItem.SECONDARY_NAME,
              RecordItem.STANDARD_IDENTIFIER,
              RecordItem.ISBN,
              RecordItem.ISSN,
              RecordItem.DIMENSION,
              RecordItem.SUPERVISOR,
              RecordItem.NOTES,
              RecordItem.EDITION,
              RecordItem.EDITION_NAME_AND_NUMBERING,
              RecordItem.ANOTHER_CREATOR,
              RecordItem.DATE_CITATION,
              RecordItem.BRACKET_RIGHT_OPTIONAL,
              RecordItem.BRACKET_LEFT_OPTIONAL,
              RecordItem.DOT_OPTIONAL,
              RecordItem.COMMA_OPTIONAL,
              RecordItem.COLON_OPTIONAL,
              RecordItem.PARENT_CREATOR,
              RecordItem.PARENT_ANOTHER_CREATOR,
              RecordItem.NUMBERING_OF_VOLUME,
              RecordItem.CARRIER_TYPE,
              RecordItem.NUMBERING,
            ],
            item
          );
          return `${isOptional ? "(?:" : ""}${part}${
            enableSpace ? "\\s*" : ""
          }${isOptional ? ")?" : ""}`;
        }),
        "$",
      ].join("")
    );

    const matches = content.match(regex);
    console.log(structure);
    console.log(regex);
    console.log(content);
    if (!matches || matches.length < 2) {
      return null;
    }

    const items = matches.slice(1);

    const fields = filter(structure, (item) => !isDelimiter(item));

    const creators = compact(
      fields.map((field, i) => {
        if (isCreator(field)) {
          const item = items[i] || "";
          const matched = item.match(/^\s*([^\s,]+),?\s+(.+)$/);

          return {
            value: "a",
            data:
              matched && matched.length > 2
                ? createCreatorValue(matched[1], matched[2])
                : item,
          };
        }

        return null;
      })
    );

    return {
      dataFields: (dataFields || []).map((field, i) => {
        if (!field) {
          return undefined;
        }
        const index = findIndex(
          fields,
          (f) =>
            f ===
            `${field.tag}${field.code}${field.indicator1 || ""}${
              field.indicator2 || ""
            }`
        );

        return index >= 0
          ? {
              ...field,
              data: items[index],
            }
          : field;
      }),
      creators: creators.length ? creators : DefaultCreators,
    };
  }

  return null;
};
