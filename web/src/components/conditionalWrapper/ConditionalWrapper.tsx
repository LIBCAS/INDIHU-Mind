import React from "react";

interface ConditionalWrapperProps {
  condition: boolean;
  wrap: (children: any) => any;
  children: any;
}

export const ConditionalWrapper: React.FC<ConditionalWrapperProps> = ({
  condition,
  wrap,
  children,
}) => (condition ? wrap(children) : children);
