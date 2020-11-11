export interface CategoryProps {
  id: string;
  name: string;
  owner: any;
  ordinalNumber: number;
  cardsCount: number;
  // when dto model
  parentId?: string;
  // when normal model
  parent?: CategoryParent;
  subCategories?: CategoryProps[];
}

interface CategoryParent {
  id: string;
  name?: string;
  parent?: CategoryParent;
  subCategories?: CategoryProps[];
}
