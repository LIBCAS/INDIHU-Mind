import React from "react";

import DropboxSvg from "./dropbox.svg";

export const Dropbox: React.FC<
  React.HTMLAttributes<HTMLDivElement>
> = props => {
  return <img src={DropboxSvg} {...props} />;
};
