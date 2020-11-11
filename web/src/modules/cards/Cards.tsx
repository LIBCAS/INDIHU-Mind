import React, { useContext } from "react";
import { RouteComponentProps } from "react-router-dom";
import Typography from "@material-ui/core/Typography";
import classNames from "classnames";
import { isEmpty } from "lodash";

import { GlobalContext, StateProps } from "../../context/Context";
import { categoryActiveSet } from "../../context/actions/category";
import { Column, ColumnType, Table } from "../../components/table";
import { CardsTableDetail } from "../cardsTableDetail/CardsTableDetail";

import { CardCreateRoot } from "../cardCreate/CardCreateRoot";
import { CardCreateAddCategory } from "../../modules/cardCreate/CardCreateAddCategory";
import { CardCreateAddLabel } from "../../modules/cardCreate/CardCreateAddLabel";
import { useStyles as useEffectStyles } from "../../theme/styles/effectStyles";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles } from "./_cardsStyles";

import { onDeleteCard } from "../../utils/card";
import {
  getPathToCategory,
  flattenCardArrays,
  concatCardArrays,
} from "./_utils";
import { Label } from "../../components/card/Label";
import { api } from "../../utils/api";
import { CardTile } from "../../components/card/CardTile";
import { parseCardNoteText } from "../../components/card/_utils";

export const columns: Column[] = [
  {
    field: "name",
    name: "Název",
    bold: true,
  },
  {
    field: "updated",
    name: "Poslední úprava",
    type: ColumnType.DATE,
  },
  {
    field: "note",
    name: "Popis",
    unsortable: true,
    format: (row: any) => row.note && parseCardNoteText(row.note),
  },
  {
    field: "labels",
    name: "Štítky",
    unsortable: true,
    format: (row: any) =>
      row.labels.map((label: any) => <Label label={label} key={label.id} />),
  },
];

export const Cards: React.FC<RouteComponentProps> = () => {
  const classes = useStyles();
  const classesText = useTextStyles();
  const classesEffect = useEffectStyles();
  const classesSpacing = useSpacingStyles();

  const context: any = useContext(GlobalContext);

  const state: StateProps = context.state;

  const dispatch: Function = context.dispatch;

  const handleDelete = (id: string, refresh: Function) =>
    onDeleteCard(id, dispatch, refresh);

  return (
    <Table
      name="cards"
      baseUrl="card"
      title={
        <>
          {state.search.categoryName !== "" && state.category.categoryActive ? (
            <Typography variant="h5">
              {getPathToCategory(
                state.category.categoryActive,
                state.category.categories
              ).map((cat, i, arr) => {
                return (
                  <span
                    key={cat.id}
                    onClick={() => {
                      categoryActiveSet(dispatch, cat);
                    }}
                    className={classNames({
                      [classesText.text600]:
                        i + 1 === arr.length && arr.length > 1,
                      [(classesText.cursor, classesEffect.hoverPrimary)]:
                        i + 1 !== arr.length,
                    })}
                  >
                    {cat.name}
                    {i + 1 !== arr.length && " > "}
                  </span>
                );
              })}
            </Typography>
          ) : (
            <Typography variant="h5">{state.search.labelName || ""}</Typography>
          )}
        </>
      }
      createLabel="Nová karta"
      enableGroupDelete={true}
      enableGroupEdit={true}
      redirectOnEdit={true}
      enableRowActions={true}
      enablePrint={true}
      enableOpenInNewTab={true}
      filter={
        !isEmpty(state.search.category)
          ? state.search.category
          : !isEmpty(state.search.label)
          ? state.search.label
          : []
      }
      columns={columns}
      FormModal={CardCreateRoot}
      onDelete={handleDelete}
      onGroupDelete={(checkboxRows) =>
        api().post(`card/set-softdelete`, {
          json: {
            ids: checkboxRows.map((c) => c.id),
            value: true,
          },
        })
      }
      GroupActionsComponent={({ formikBag }) => (
        <div className={classes.groupActionsComponent}>
          <div
            className={classNames(
              classes.groupActionsComponentField,
              classesSpacing.mr1
            )}
          >
            <CardCreateAddCategory formikBag={formikBag} compact={true} />
          </div>
          <div className={classes.groupActionsComponentField}>
            <CardCreateAddLabel formikBag={formikBag} compact={true} />
          </div>
        </div>
      )}
      groupEditMapper={(card, newValues) => ({
        ...concatCardArrays(flattenCardArrays(card), newValues),
      })}
      ComponentDetail={CardsTableDetail}
      TileComponent={({ item }) => <CardTile card={item} showLabels={true} />}
    />
  );
};
