import React from "react";
import MaterialSwitch from "@material-ui/core/Switch";

import { useStyles as useFormStyles } from "../../components/form/_styles";

interface UserCreateSwitchProps {
  row: any;
}

export const UserCreateSwitch: React.FC<UserCreateSwitchProps> = ({ row }) => {
  const classesForm = useFormStyles();
  return (
    <>
      <MaterialSwitch
        disabled
        size="small"
        color="primary"
        checked={row.allowed}
        classes={{
          switchBase: classesForm.switchBase,
          disabled: classesForm.disabled,
          track: classesForm.track,
          checked: classesForm.checked,
          colorPrimary: classesForm.disabled,
        }}
      />
    </>
  );
};
