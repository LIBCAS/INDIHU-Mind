export enum AttributeType {
  INTEGER = "INTEGER",
  DOUBLE = "DOUBLE",
  DATE = "DATE",
  DATETIME = "DATETIME",
  BOOLEAN = "BOOLEAN",
  STRING = "STRING",
  URL = "URL",
  GEOLOCATION = "GEOLOCATION"
}

export const AttributeTypeEnum: { value: AttributeType; label: string }[] = [
  { value: AttributeType.STRING, label: "Text" },
  { value: AttributeType.DOUBLE, label: "Číslo" },
  { value: AttributeType.BOOLEAN, label: "Boolean" },
  { value: AttributeType.DATE, label: "Datum" },
  { value: AttributeType.DATETIME, label: "Datum a čas" },
  { value: AttributeType.URL, label: "Odkaz" },
  { value: AttributeType.GEOLOCATION, label: "Lokalita" }
];
