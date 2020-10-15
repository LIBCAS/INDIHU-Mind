import React from "react";
import { NavLink } from "react-router-dom";
import Link from "@material-ui/core/Link";

import { routesTabs, adminRoutes, RoutesProps } from "../../router/_routes";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { useUserToken } from "../../hooks/authHooks";

import { useStyles } from "./_navbarStyles";

interface NavbarItemsProps {
  matchesMd: boolean;
}

const CustomNavLink = React.forwardRef((
  props: any,
  ref // eslint-disable-line @typescript-eslint/no-unused-vars
) => <NavLink {...props} />);

export const NavbarItems: React.FC<NavbarItemsProps> = ({ matchesMd }) => {
  const classesText = useTextStyles();
  const classes = useStyles();
  const token = useUserToken();
  const isAdmin = token && token.authorities.indexOf("ROLE_ADMIN") !== -1;

  const renderItems = ({ path, label }: RoutesProps) => (
    <Link
      key={path}
      component={CustomNavLink}
      to={path}
      activeClassName={classesText.textBold}
      className={classesText.textLink}
      exact
    >
      {label}
    </Link>
  );

  return (
    <>
      {matchesMd && (
        <div className={classes.navItems}>
          {routesTabs.map(renderItems)}
          {isAdmin && adminRoutes.map(renderItems)}
        </div>
      )}
    </>
  );
};
