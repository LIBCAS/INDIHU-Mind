import { FileType } from "../../enums";

export interface Item {
  id: string;
  name: string;
  providerType?: FileType;
}

export interface Image {
  src: string;
  thumbnail: string;
  thumbnailWidth: number;
  thumbnailHeight: number;
  caption: string;
}

export interface GalleryProps {
  items: Item[];
  className?: string;
}

export interface ImageMeta {
  width: number;
  height: number;
}
