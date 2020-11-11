import React, { useState } from "react";
import { Field } from "formik";

import { api } from "../../utils/api";
import { Table, Column, ColumnType } from "../../components/table";
import { Switch } from "../../components/form/Switch";

import { UserCreateModal } from "./UserCreateModal";

export const columns: Column[] = [
  {
    field: "email",
    name: "Uživatelské jméno",
    bold: true,
  },
  {
    field: "allowed",
    name: "Stav registrace",
    type: ColumnType.BOOLEAN,
  },
];

export const UserCreate: React.FC = () => {
  const [open, setOpen] = useState(false);

  return (
    <>
      <UserCreateModal open={open} setOpen={setOpen} />
      <Table
        title="Správa uživatelů"
        createLabel="Vytvořit nového uživatele"
        baseUrl="admin/users"
        columns={columns}
        enableRowClick={false}
        enableGroupEdit={true}
        parametrized={false}
        onCreate={() => setOpen(true)}
        onGroupEdit={(checkboxRows, values) =>
          api().post(`admin/set-allowance`, {
            json: {
              ids: checkboxRows.map((checkbox) => checkbox.id),
              value: values.state,
            },
          })
        }
        GroupActionsComponent={({ formikBag }) => (
          <Field
            name="state"
            component={Switch}
            label={`Stav registrace ${
              formikBag.values.state ? "(povoleno)" : "(zakázáno)"
            }`}
            secondary
          />
        )}
      />
    </>
  );
};

export { UserCreate as default };
