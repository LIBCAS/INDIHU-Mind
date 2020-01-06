import React, { useRef, useState } from "react";
import Tooltip from "@material-ui/core/Tooltip";
import IconButton from "@material-ui/core/IconButton";
import Person from "@material-ui/icons/Person";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import classNames from "classnames";

import { remove } from "../../utils/store";
import { useUserToken } from "../../hooks/authHooks";

import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";

interface NavbarUserProps {
  matchesMd: boolean;
}

export const NavbarUser: React.FC<NavbarUserProps> = ({ matchesMd }) => {
  const classesLayout = useLayoutStyles();
  const classesSpacing = useSpacingStyles();
  const iconRef = useRef(null);
  const [open, setOpen] = useState(false);
  const token = useUserToken();

  const onClick = () => setOpen(prev => !prev);
  const onClose = () => setOpen(false);
  const onLogout = () => remove("token");
  return (
    <div
      ref={iconRef}
      className={classNames(
        classesLayout.flex,
        classesLayout.alignCenter,
        { [classesSpacing.mlAuto]: !matchesMd },
        { [classesSpacing.ml1]: matchesMd }
      )}
    >
      <Tooltip title="Profil">
        <IconButton onClick={onClick} color="inherit">
          <Person fontSize="large" />
        </IconButton>
      </Tooltip>
      <Menu anchorEl={iconRef.current} open={open} onClose={onClose}>
        <MenuItem disabled onClick={onLogout}>
          {token && token.email}
        </MenuItem>
        <MenuItem onClick={onLogout}>Odhlásit se</MenuItem>
      </Menu>
    </div>
  );
};
