import { pick, compact } from "lodash";

import { api } from "../../utils/api";
import { FileProps } from "../../types/file";
import { FileType } from "../../enums";
import { AttachmentUpdateProps, AttachmentEditProps } from "./_types";

export const baseUrl = "attachment-file";
const url = `${baseUrl}/parametrized`;
const searchUrl = `${baseUrl}/search`;

export const getFiles = async (text?: string, page = 0, pageSize = 10) => {
  try {
    const response =
      text && text.length
        ? await api().get(
            `${searchUrl}?page=${page}&pageSize=${pageSize}&q=${encodeURI(
              text
            )}`
          )
        : await api().post(url, {
            json: { page, pageSize, order: "DESC" },
          });

    return await response.json();
  } catch (e) {
    console.log(e);
    return null;
  }
};

export const uploadFile = (file: FileProps) => {
  const body = new FormData();

  const notLocal = file.providerType && file.providerType !== FileType.LOCAL;

  const dto: any = pick(
    file,
    compact([
      "name",
      "type",
      "ordinalNumber",
      "location",
      file.linkedCards && file.linkedCards.length ? "linkedCards" : null,
      file.records && file.records.length ? "records" : null,
      notLocal ? "providerType" : null,
      notLocal ? "link" : null,
      notLocal && file.providerId ? "providerId" : null,
    ])
  );

  if (file.providerType === FileType.LOCAL && file.content) {
    dto.providerType = file.providerType;

    body.append(`file`, file.content);
  }

  body.append(
    "dto",
    new Blob([JSON.stringify(dto)], {
      type: "application/json",
    })
  );

  return api({ noContentType: true }).post(baseUrl, { body }).json<any>();
};

export const fileUpload = async (file: FileProps) => await uploadFile(file);

const mapUpdateValue = (value?: any[]) =>
  (value || []).map((c: any) => (typeof c === "object" ? c.id : c));

export const updateFile = async (
  file: AttachmentUpdateProps | AttachmentEditProps
) =>
  api().put(`${baseUrl}/${file.id}`, {
    json: {
      ...file,
      linkedCards: mapUpdateValue(file.linkedCards),
      records: mapUpdateValue(file.records),
    },
  });

export const fileUpdate = async (
  file: AttachmentUpdateProps | AttachmentEditProps
) => {
  const response = await updateFile(file);
  return response && response.ok;
};

export const fileDelete = async (id: string) => {
  const response = await api().delete(`${baseUrl}/${id}`);
  return response && response.ok;
};

export enum FileErrors {
  USER_QUOTA_REACHED = "USER_QUOTA_REACHED",
  FILE_TOO_BIG = "FILE_TOO_BIG",
  FILE_FORBIDDEN = "FILE_FORBIDDEN",
}

const fileErrors = {
  USER_QUOTA_REACHED: "Překročen celkový limit na soubory",
  FILE_TOO_BIG: "Soubor je příliš velký",
  FILE_FORBIDDEN: "Neplatný typ souboru",
};

export const translateFileError = (err: FileErrors) => {
  return fileErrors[err];
};

export const createErrorMessage = (
  err: any,
  name?: string,
  shorterMessage = false
) =>
  `${shorterMessage ? "Chyba" : "Nastala chyba při přidávání"} souboru${
    name ? ` ${name}` : ""
  }${
    err && err.response && err.response.message
      ? `: ${err.response.message}${
          err.response.code === FileErrors.FILE_TOO_BIG
            ? `. Maximální povolena velikost: ${err.response.details.maxUploadSize}`
            : ""
        }`
      : ""
  }.`;
