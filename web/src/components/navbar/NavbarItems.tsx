import Link from "@material-ui/core/Link";
import React from "react";
import { NavLink } from "react-router-dom";
import { useUserToken } from "../../hooks/authHooks";
import { adminRoutes, RoutesProps, routesTabs } from "../../router/_routes";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { isAdmin } from "../../utils/token";
import { useStyles } from "./_navbarStyles";

interface NavbarItemsProps {
  matchesLg: boolean;
}

const CustomNavLink = React.forwardRef((
  props: any,
  ref // eslint-disable-line @typescript-eslint/no-unused-vars
) => <NavLink {...props} />);

export const NavbarItems: React.FC<NavbarItemsProps> = ({ matchesLg }) => {
  const classesText = useTextStyles();
  const classes = useStyles();
  const token = useUserToken();

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
      {matchesLg && (
        <div className={classes.navItems}>
          {routesTabs.map(renderItems)}
          {isAdmin(token) && adminRoutes.map(renderItems)}
        </div>
      )}
    </>
  );
};
