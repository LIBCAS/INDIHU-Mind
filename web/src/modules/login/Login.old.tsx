import React from "react";
import { RouteComponentProps } from "react-router-dom";

import { Modal } from "../../components/portal/Modal";

import { LoginForm } from "./LoginForm";

export const Login: React.FC<RouteComponentProps> = ({ history }) => {
  return (
    <Modal open setOpen={false} content={<LoginForm history={history} />} />
  );
};

export { Login as default };
