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
  const [seelctedUser, setSelectedUser] = useState<UserProps | undefined>(
    undefined
  );

  return (
    <>
      <div
        className={classNames(
          classesLayout.flex,
          classesLayout.alignCenter,
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
          variant="outlined"
          onClick={() => setOpen(prev => !prev)}
        >
          Vytvořit nového uživatele
        </Button>
        <UserCreateModal open={open} setOpen={setOpen} />
      </div>
      <TableUser
        baseUrl="admin/users?"
        columns={columns}
        selectedRow={seelctedUser}
        setSelectedRow={setSelectedUser}
      />
    </>
  );
};

export { UserCreate as default };
