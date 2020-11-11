export const specialTags = [
  {
    id: "AUTHOR",
    text: "Tvůrce (Autor, Editor...)",
    count: 0,
  },
];

export const punctuation = [
  {
    id: "PERIOD",
    text: "Tečka (.)",
    count: 0,
  },
  {
    id: "COLON",
    text: "Dvojtečka (:)",
    count: 0,
  },
  {
    id: "COMMA",
    text: "Čárka (,)",
    count: 0,
  },
  {
    id: "SEMICOLON",
    text: "Středník (;)",
    count: 0,
  },
  {
    id: "SPACE",
    text: "Mezera",
    count: 0,
  },
  {
    id: "BRACKET_LEFT",
    text: "Hranatá závorka, levá ([)",
    count: 0,
  },
  {
    id: "BRACKET_RIGHT",
    text: "Hranatá závorka, pravá (])",
    count: 0,
  },
  {
    id: "HYPHEN",
    text: "Spojovník (-)",
    count: 0,
  },
  {
    id: "SLASH",
    text: "Lomítko (/)",
    count: 0,
  },
];

export const otherTags = [
  {
    id: "ONLINE",
    text: "[online]",
    count: 0,
  },
  {
    id: "GENERATE_DATE",
    text: "[datum citování]",
    count: 0,
  },
  {
    id: "INSIDE",
    text: "[In:]",
    count: 0,
  },
];

export enum FirstNameFormat {
  FULL = "FULL",
  INITIAL = "INITIAL",
}

export enum MultipleAuthorsFormat {
  FULL = "FULL",
  ETAL = "ETAL",
}

export enum OrderFormat {
  FIRSTNAME_FIRST = "FIRSTNAME_FIRST",
  LASTNAME_FIRST = "LASTNAME_FIRST",
}
