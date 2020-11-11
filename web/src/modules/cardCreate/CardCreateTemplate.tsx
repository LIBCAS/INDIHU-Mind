import React from "react";
import Typography from "@material-ui/core/Typography";

import BrandingWatermark from "@material-ui/icons/BrandingWatermark";
import CallToAction from "@material-ui/icons/CallToAction";
import FeaturedPlayList from "@material-ui/icons/FeaturedPlayList";
import FeaturedVideo from "@material-ui/icons/FeaturedVideo";

import { CardTemplateProps } from "../../types/cardTemplate";
import { Divider } from "../../components/divider/Divider";

import { CardCreateTemplateItem } from "./CardCreateTemplateItem";
import { useStyles } from "./_cardCreateStyles";

const randomIcons = [
  <BrandingWatermark key="watermark" style={{ fontSize: 60 }} />,
  <CallToAction key="callToAction" style={{ fontSize: 60 }} />,
  <FeaturedPlayList key="playlist" style={{ fontSize: 60 }} />,
  <FeaturedVideo key="featured" style={{ fontSize: 60 }} />,
];

interface CardCreateTemplateProps {
  templates: CardTemplateProps[];
  setAttributes: Function;
}

export const CardCreateTemplate: React.FC<CardCreateTemplateProps> = ({
  templates,
  setAttributes,
}) => {
  const classes = useStyles();

  const selectTemplate = (template: CardTemplateProps) => {
    if (template.attributeTemplates) {
      setAttributes(template.attributeTemplates);
    }
  };
  let iconCount = -1;

  return (
    <>
      <div className={classes.wrapper}>
        <Typography className={classes.title} variant="h5">
          Vyberte ze šablony
        </Typography>
        <Divider />
        <div className={classes.templateWrapper}>
          {templates.map((template) => {
            iconCount++;
            if (iconCount >= randomIcons.length) iconCount = 0;
            const Icon = randomIcons[iconCount];
            return (
              <CardCreateTemplateItem
                key={template.id}
                template={template}
                Icon={Icon}
                selectTemplate={selectTemplate}
              />
            );
          })}
        </div>
      </div>
    </>
  );
};
