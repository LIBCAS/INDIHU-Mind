import Typography from "@material-ui/core/Typography";
import classNames from "classnames";
import { isEmpty, sortBy } from "lodash";
import React, { useCallback, useContext, useEffect } from "react";
import { RouteComponentProps } from "react-router-dom";
import { CardTile } from "../../components/card/CardTile";
import { Label } from "../../components/card/Label";
import { FileFromTemplateGenerator } from "../../components/file/FileFromTemplateGenerator";
import { Column, ColumnType, Table } from "../../components/table";
import { categoryActiveSet } from "../../context/actions/category";
import { recordTemplateGet } from "../../context/actions/recordTemplate";
import { GlobalContext, StateProps } from "../../context/Context";
import { CardCreateAddCategory } from "../../modules/cardCreate/CardCreateAddCategory";
import { CardCreateAddLabel } from "../../modules/cardCreate/CardCreateAddLabel";
import { useStyles as useEffectStyles } from "../../theme/styles/effectStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { api } from "../../utils/api";
import { onDeleteCard } from "../../utils/card";
import { CardCreateRoot } from "../cardCreate/CardCreateRoot";
import { CardsTableDetail } from "../cardsTableDetail/CardsTableDetail";
import { useStyles } from "./_cardsStyles";
import {
  concatCardArrays,
  flattenCardArrays,
  getPathToCategory,
} from "./_utils";

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
    field: "rawNote",
    name: "Popis",
    unsortable: true,
  },
  {
    field: "labels",
    name: "Štítky",
    unsortable: true,
    format: (row: any) =>
      sortBy(row.labels, "ordinalNumber").map((label: any) => (
        <Label label={label} key={label.id} />
      )),
  },
];

export const Cards: React.FC<RouteComponentProps> = React.memo(() => {
  const classes = useStyles();
  const classesText = useTextStyles();
  const classesEffect = useEffectStyles();
  const classesSpacing = useSpacingStyles();

  const context: any = useContext(GlobalContext);

  const state: StateProps = context.state;

  const dispatch: Function = context.dispatch;

  const handleDelete = (id: string, refresh: Function) =>
    onDeleteCard(id, dispatch, refresh);

  useEffect(() => {
    recordTemplateGet(dispatch);
  }, [dispatch]);

  const Toolbar = useCallback(
    (checkboxRows: any) => (
      <FileFromTemplateGenerator variant="cards" checkboxRows={checkboxRows} />
    ),
    []
  );

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
        api().post(`card/status`, {
          json: {
            ids: checkboxRows.map((c) => c.id),
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
      enableCardsExports={true}
      Toolbar={({ checkboxRows }) => Toolbar(checkboxRows)}
    />
  );
});
