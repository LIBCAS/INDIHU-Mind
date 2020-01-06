import { CardProps } from "./card";

interface HighlightMapProps {
  [key: string]: string[];
}

interface HighlightedAttributesProps {
  id: string;
  name: string;
  highlight: string;
}

export interface SearchCardProps {
  card: CardProps;
  highlightMap: HighlightMapProps;
  highlightedAttributes: HighlightedAttributesProps[];
  [key: string]: any;
}
