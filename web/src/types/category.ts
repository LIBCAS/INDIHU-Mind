export interface CategoryProps {
  id: string;
  name: string;
  owner: any;
  ordinalNumber: number;
  cardsCount: number;
  // when dto model
  parentId?: string;
  // when normal model
  parent?: { id: string };
  subCategories?: CategoryProps[];
}
