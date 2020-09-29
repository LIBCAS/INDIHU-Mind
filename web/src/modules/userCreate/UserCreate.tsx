import React, { useState } from "react";
import Typography from "@material-ui/core/Typography";
import Button from "@material-ui/core/Button";
import classNames from "classnames";

import { UserProps } from "../../types/user";
import { TableUser } from "../../components/tableUser/Table";

import { UserCreateModal } from "./UserCreateModal";
import { columns } from "./_utils";

import { useStyles } from "./_userCreateStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";

export const UserCreate: React.FC = () => {
  const classes = useStyles();
  const classesLayout = useLayoutStyles();
  const classesSpacing = useSpacingStyles();

  const [open, setOpen] = useState(false);
  const [selctedUser, setSelectedUser] = useState<UserProps | undefined>(
    undefined
  );

  return (
    <>
      <UserCreateModal open={open} setOpen={setOpen} />
      <div
        className={classNames(
          classesLayout.flex,
          classesLayout.alignCenter,
          classesLayout.spaceBetween,
          classesLayout.flexWrap,
          classesSpacing.mb2,
          classesSpacing.mt1
        )}
      >
        <Typography
          className={classNames(classesSpacing.mr2, classesSpacing.mb1)}
          variant="h5"
        >
          Správa uživatelů
        </Typography>
        <Button
          className={classes.createUserButton}
          color="primary"
          variant="contained"
          onClick={() => setOpen(prev => !prev)}
        >
          Vytvořit nového uživatele
        </Button>
      </div>
      <TableUser
        baseUrl="admin/users"
        columns={columns}
        selectedRow={selctedUser}
        setSelectedRow={setSelectedUser}
      />
    </>
  );
};

export { UserCreate as default };
