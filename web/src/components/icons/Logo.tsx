import React from "react";
import LogoSVG from "./logo.svg";

export const Logo: React.FC<React.HTMLAttributes<HTMLDivElement>> = (props) => {
  return (
    <img
      style={{
        height: 64,
        marginTop: -10,
        marginBottom: -10,
        marginLeft: -20,
        marginRight: -20,
      }}
      alt="Logo"
      src={LogoSVG}
      {...props}
    />
  );
};
