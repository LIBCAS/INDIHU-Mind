export const parseCardNoteText = (note: string) =>
  JSON.parse(note).blocks.reduce(
    (text: string, block: any) => `${text} ${block.text}`,
    ""
  );
