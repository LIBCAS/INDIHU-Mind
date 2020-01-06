import React from "react";

import DriveSvg from "./drive.svg";

export const Drive: React.FC<React.HTMLAttributes<HTMLDivElement>> = props => {
  return <img src={DriveSvg} {...props} />;
};
