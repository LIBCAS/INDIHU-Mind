import { ImageMeta } from "./_types";

export const createFileUrl = (id: string) =>
  `/api/attachment-file/${id}/download`;

export const getImageMeta = async (id: string): Promise<ImageMeta> =>
  new Promise((resolve, reject) => {
    let img = new Image();
    img.onload = () => resolve(img);
    img.onerror = reject;
    img.src = createFileUrl(id);
  });
