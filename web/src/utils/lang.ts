// plural(5, ['komentář', 'komentáře', 'komentářů'])
// returns komentářů
export const plural = (count: number, variants: [string, string, string]) => {
  count = Math.abs(count);
  if (count === 1) return variants[0];
  if (count < 5 && count > 0) return variants[1];
  return variants[2];
};
