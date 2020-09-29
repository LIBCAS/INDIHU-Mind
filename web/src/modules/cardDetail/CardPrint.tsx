import React from "react";

import { Print } from "../../components/print";
import { CardProps } from "../../types/card";
import { useStyles } from "./_cardDetailStyles";
import { formatMultiline } from "../../utils";
import { formatCategoryName } from "../../utils/category";
import { CategoryProps } from "../../types/category";

interface PrintComponentProps {
  card: CardProps;
}

interface CardPrintProps {
  card: CardProps;
  className?: string;
}

const PrintComponent: React.FC<PrintComponentProps> = ({ card }) => {
  const classes = useStyles();

  return (
    <div className={classes.cardPrint}>
      {[
        { name: "name", label: "Název" },
        { name: "note", label: "Poznámka", multiLine: true },
        {
          name: "categories",
          label: "Kategorie",
          mapper: (value: CategoryProps[]) =>
            value.map((category, i) => {
              return (
                <div key={`${category.name}${i}`}>
                  {formatCategoryName(category)}
                </div>
              );
            })
        },
        { name: "labels", label: "Štítky" },
        { name: "linkedCards", label: "Karty" },
        { name: "records", label: "Citace" },
        { name: "attributes", label: "Atributy" }
      ].map(({ name, label, mapper, multiLine }) => {
        const value = card[`${name}`];
        return value === null ||
          value === "" ||
          (typeof value !== "string" &&
            typeof value !== "number" &&
            !value.length) ? (
          <></>
        ) : (
          <div key={name} className={classes.cardPrintRow}>
            <div className={classes.cardPrintLabel}>{label}:</div>
            <div className={classes.cardPrintValue}>
              {mapper
                ? mapper(value as any)
                : typeof value !== "string" && typeof value !== "number"
                ? (value as any)
                    .map(({ name }: { name: string }) => name)
                    .join(", ")
                : typeof value === "number"
                ? value
                : typeof value !== "number" && multiLine
                ? formatMultiline(value)
                : value}
            </div>
          </div>
        );
      })}
    </div>
  );
};

export const CardPrint: React.FC<CardPrintProps> = ({ card, ...props }) => (
  <Print
    {...props}
    variant="outlined"
    size="small"
    ComponentToPrint={PrintComponent}
    componentToPrintProps={{ card }}
  />
);
