import React from "react";
import { NavLink } from "react-router-dom";
import Link from "@material-ui/core/Link";

import { useUserToken } from "../../hooks/authHooks";

import { routesTabs, adminRoutes, RoutesProps } from "../../router/_routes";
import { useStyles } from "./_leftPanelStyles";
import { isAdmin } from "../../utils/token";

interface LinksProps {
  setLeftPanelOpen?: any;
  setOpenModalInfo: any;
}

const CustomNavLink = React.forwardRef((
  props: any,
  ref // eslint-disable-line @typescript-eslint/no-unused-vars
) => <NavLink {...props} />);

export const Links: React.FC<LinksProps> = ({
  setOpenModalInfo,
  setLeftPanelOpen,
}) => {
  const classes = useStyles();
  const token = useUserToken();

  const mapRoutes = (r: RoutesProps) => (
    <Link
      key={r.label}
      component={CustomNavLink}
      to={r.path}
      exact
      activeClassName={classes.linkActive}
      className={classes.link}
      onClick={() => setLeftPanelOpen(false)}
    >
      {r.label}
    </Link>
  );
  return (
    <div className={classes.wrapper}>
      {routesTabs.map(mapRoutes)}
      {isAdmin(token) && adminRoutes.map(mapRoutes)}
      {setOpenModalInfo && (
        <Link onClick={() => setOpenModalInfo(true)} className={classes.link}>
          O aplikaci
        </Link>
      )}
    </div>
  );
};
