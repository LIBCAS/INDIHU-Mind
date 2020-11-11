import React from "react";
import MuiFiberManualRecordIcon from "@material-ui/icons/FiberManualRecord";

export interface LabelColorDotProps {
  color: string;
}

export const LabelColorDot: React.FC<LabelColorDotProps> = ({ color }) => {
  return (
    <MuiFiberManualRecordIcon
      style={{
        color: color,
        width: "12px",
        height: "12px",
      }}
    />
  );
};
