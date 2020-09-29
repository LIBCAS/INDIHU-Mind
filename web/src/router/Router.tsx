import React from "react";
import {
  Switch,
  Route,
  Redirect,
  withRouter,
  RouteComponentProps
} from "react-router-dom";

import { AppWrapper } from "../components/appWrapper/AppWrapper";
import { ConditionalWrapper } from "../components/conditionalWrapper/ConditionalWrapper";

import { Bin } from "../modules/bin/Bin";

import { useTokenRefresh } from "../hooks/authHooks";

import { routes, adminRoutes, RoutesProps } from "./_routes";
import { isAdmin } from "../utils/token";

const RouterView: React.FC<RouteComponentProps> = ({ location }) => {
  const token = useTokenRefresh();

  const isPathLogin = location.pathname === "/";
  const isResetPassword = location.pathname === "/reset-password";
  const notAuth = !isPathLogin && !isResetPassword;

  if (!token && notAuth) {
    return <Redirect to="/" />;
  }

  if (token && isPathLogin) {
    return <Redirect to="/cards" />;
  }

  const mapRoutes = (r: RoutesProps) => (
    <Route
      key={r.label}
      exact={r.exact}
      path={r.path}
      component={r.component}
    />
  );
  return (
    <ConditionalWrapper
      condition={notAuth}
      wrap={(children: any) => <AppWrapper>{children}</AppWrapper>}
    >
      <Switch>
        {routes.map(mapRoutes)}
        {isAdmin(token) && adminRoutes.map(mapRoutes)}
        <Route path="/bin" component={Bin} />
        {/* empty route for page refresh https://github.com/ReactTraining/react-router/issues/1982#issuecomment-314167564 */}
        {/*<Route path="empty" component={undefined} key="empty" />*/}
      </Switch>
    </ConditionalWrapper>
  );
};

export const Router = withRouter(RouterView);
