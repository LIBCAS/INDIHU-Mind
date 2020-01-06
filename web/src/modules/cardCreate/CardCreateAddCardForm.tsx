import React, { useState } from "react";
import Typography from "@material-ui/core/Typography";
import TextField from "@material-ui/core/TextField";
import Button from "@material-ui/core/Button";
import classNames from "classnames";

import { SearchCardProps } from "../../types/search";
import { api } from "../../utils/api";

import { Loader } from "../../components/loader/Loader";

import { useStyles as useFormStyles } from "../../components/form/_formStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";

import { CardTile } from "../../components/card/CardTile";

interface CardCreateAddCardFormProps {
  formikBagParent: any;
  setOpen: Function;
}

let controller = new AbortController();

export const CardCreateAddCardForm: React.FC<CardCreateAddCardFormProps> = ({
  formikBagParent
}) => {
  const classesForm = useFormStyles();
  const classesSpacing = useSpacingStyles();
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(false);
  const [cards, setCards] = useState<SearchCardProps[]>([]);
  const { linkedCards, id } = formikBagParent.values;
  const isSelected = (id: string) => {
    const index = linkedCards.findIndex((c: any) => c.id === id);
    return index !== -1;
  };
  const onSelect = (card: any) => {
    const shouldRemove = isSelected(card.id);
    let newLinkedCards;
    if (shouldRemove) {
      newLinkedCards = linkedCards.filter((c: any) => c.id !== card.id);
    } else {
      newLinkedCards = [card, ...linkedCards];
    }
    formikBagParent.setFieldValue("linkedCards", newLinkedCards);
  };
  const onSearch = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    e.stopPropagation();
    setLoading(true);
    if (!controller.signal.aborted && loading) controller.abort();
    controller = new AbortController();

    api()
      .get(`card/search?page=0&pageSize=8&q=${search}`, {
        signal: controller.signal
      })
      .json()
      .then((res: any) => {
        setLoading(false);
        setCards(res.items);
      });
    // TODO
    // .catch()
  };
  return (
    <div
      className={classNames(
        classesSpacing.p1,
        classesSpacing.mt2,
        classesSpacing.mb2
      )}
      style={{ maxWidth: "400px" }}
    >
      <Typography gutterBottom variant="h6" display="block">
        Propojení karet
      </Typography>
      <form onSubmit={onSearch}>
        <TextField
          value={search}
          onChange={(e: any) => setSearch(e.target.value)}
          placeholder="Vyhledat"
          InputProps={{
            disableUnderline: true
          }}
          fullWidth
          autoFocus
          inputProps={{
            className: classNames(classesForm.default)
          }}
        />
        <Button
          className={classesSpacing.mt1}
          fullWidth
          variant="outlined"
          type="submit"
          color="primary"
        >
          Vyhledat kartu
        </Button>
        <Loader loading={loading} local />
      </form>
      {cards && cards.length === 0 && !loading && (
        <Typography
          className={classesSpacing.mt2}
          gutterBottom
          variant="h6"
          display="block"
        >
          Žádné výsledky
        </Typography>
      )}
      {cards &&
        cards.map(card =>
          card.card.id === id ? null : (
            <CardTile
              key={card.card.id}
              onSelect={onSelect}
              card={{
                id: card.card.id,
                name: card.card.name,
                note: card.card.note || ""
              }}
              text={
                isSelected(card.card.id) ? (
                  <span style={{ color: "#f00" }}>Odebrat kartu</span>
                ) : (
                  "Přidat kartu"
                )
              }
            />
          )
        )}
    </div>
  );
};
