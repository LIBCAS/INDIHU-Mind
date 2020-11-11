import React, { useEffect, useState, useCallback } from "react";
import classNames from "classnames";
// @ts-ignore
import ReactGallery from "react-grid-gallery";

import { useStyles } from "./_styles";
import { createFileUrl, getImageMeta } from "./_utils";
import { GalleryProps, Image, ImageMeta } from "./_types";
import { FileType } from "../../enums";
import { Loader } from "../loader/Loader";

export const Gallery: React.FC<GalleryProps> = ({ items, className }) => {
  const classes = useStyles();

  const [images, setImages] = useState<Image[] | null>(null);

  const loadImages = useCallback(async () => {
    let images: Image[];

    const promisses = items
      .filter(({ providerType }) => providerType === FileType.LOCAL)
      .map(async ({ id, name }) => {
        const url = createFileUrl(id);
        const { width, height }: ImageMeta = await getImageMeta(id);

        return {
          src: url,
          thumbnail: url,
          thumbnailWidth: width,
          thumbnailHeight: height,
          caption: name,
        };
      });

    try {
      images = await Promise.all(promisses);
    } catch (e) {
      images = [];
    }

    setImages(images);
  }, [items]);

  useEffect(() => {
    loadImages();
  }, [loadImages]);

  const isLoading = items && !images;

  return isLoading || (images && images.length) ? (
    <div className={classNames(classes.gallery, className)}>
      {isLoading ? (
        <div className={classes.loader}>
          <Loader loading={true} local={true} />
          <div className={classes.loaderText}>Načítají se obrázky...</div>
        </div>
      ) : (
        <ReactGallery
          {...{ imageCountSeparator: " z ", lightboxWidth: 1920, images }}
        />
      )}
    </div>
  ) : (
    <></>
  );
};
