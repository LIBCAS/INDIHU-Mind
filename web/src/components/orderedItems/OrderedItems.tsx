import { sortBy } from "lodash";
import React, { ReactElement, useContext, useEffect, useState } from "react";
import { GlobalContext } from "../../context/Context";
import {
  STATUS_ERROR_COUNT_CHANGE,
  STATUS_ERROR_TEXT_SET,
} from "../../context/reducers/status";
import { api } from "../../utils/api";
import { Loader } from "../loader/Loader";
import { MessageSnackbar } from "../messages/MessageSnackbar";

interface OrderedItemArguments<T> {
  item: T;
  moveForward?: () => void;
  moveBackward?: () => void;
}
interface Props<T> {
  initialItems: T[] | undefined;
  itemComponent: (args: OrderedItemArguments<T>) => ReactElement;
  label: string;
  endpoint: "category" | "label";
}

enum Direction {
  FORWARD = "FORWARD",
  BACKWARD = "BACKWARD",
}

export const OrderedItems = <T extends { id: string; ordinalNumber: number }>({
  initialItems,
  itemComponent,
  label,
  endpoint,
}: Props<T>) => {
  const sortByOrdinal = (items: T[]) => sortBy(items, "ordinalNumber");

  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;

  const [items, setItems] = useState<T[]>(sortByOrdinal(initialItems || []));

  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<boolean | string>(false);

  useEffect(() => {
    setItems(sortByOrdinal(initialItems || []));
  }, [initialItems]);

  const swap = (index: number) => (direction: Direction) => {
    const newItems = [...items];
    const offset = direction === Direction.FORWARD ? 1 : -1;
    if (index + offset < 0 || index + offset > items.length - 1) {
      return;
    }
    setLoading(true);
    newItems[index] = {
      ...items[index + offset],
      ordinalNumber: items[index].ordinalNumber,
    };
    newItems[index + offset] = {
      ...items[index],
      ordinalNumber: items[index + offset].ordinalNumber,
    };

    Promise.all(
      [index, index + offset].map((index) => {
        const item = items[index];
        return api().put(`${endpoint}/${item.id}`, {
          json: { ...item, parent: (item as any).parentId },
        });
      })
    )
      .then(() => {
        dispatch({
          type: STATUS_ERROR_TEXT_SET,
          payload: `${label} byly úspěšně přesunuty`,
        });
        dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
        setItems(newItems);
        setLoading(false);
      })
      .catch((err) => {
        setLoading(false);
        setError(`${label} se nepovedlo přesunout.`);
      });
  };

  return (
    <>
      <Loader loading={loading} />
      {error && <MessageSnackbar setVisible={setError} message={error} />}
      {items.map((item, index) =>
        itemComponent({
          item,
          moveForward:
            index < items.length - 1
              ? () => swap(index)(Direction.FORWARD)
              : undefined,
          moveBackward:
            index > 0 ? () => swap(index)(Direction.BACKWARD) : undefined,
        })
      )}
    </>
  );
};

export default OrderedItems;
