import React from "react";
import { ColumnProps } from "../../components/tableCard/_types";
import { UserProps } from "../../types/user";

import { UserCreateSwitch } from "./UserCreateSwitch";

export const columns: ColumnProps[] = [
  {
    id: "1",
    path: "email",
    name: "E-mail",
    format: (row: any) => {
      return <span style={{ fontWeight: 800 }}>{row.email}</span>;
    }
  },
  {
    id: "2",
    path: "allowed",
    name: "Stav registrace",
    format: (row: any) => {
      return <UserCreateSwitch row={row} />;
    }
  }
];

export const sample: { count: number; items: UserProps[] } = {
  count: 2,
  items: [
    {
      id: "1",
      email: "email",
      name: "Random name",
      state: true,

      created: "",
      deleted: "",
      updated: "",
      password: ""
    },
    {
      id: "2",
      email: "email",
      name: "Random name 2",
      state: false,

      created: "",
      deleted: "",
      updated: "",
      password: ""
    }
  ]
};
