import React, { useState } from "react";
import { RouteComponentProps } from "react-router-dom";

import Navbar from "../../components/login/navbar/Navbar";
import { LoginForm } from "../../components/login/login/LoginForm";
import { Theme, Card, CardContent, Typography, Grid } from "@material-ui/core";
import { useStyles } from "./_loginStyles";
import { useTheme } from "@material-ui/styles";
import { LeftPanel } from "../../components/login/leftPanel/LeftPanel";
import { Modal } from "../../components/login/modal/Modal";

export const Login: React.FC<RouteComponentProps> = ({ history }) => {
  const classes = useStyles();
  const theme: Theme = useTheme();

  const [leftPanelOpen, setLeftPanelOpen] = useState(false);
  const [openModalInfo, setOpenModalInfo] = React.useState(false);

  return (
    <div>
      <div className={classes.header}>
        <Navbar
          setLeftPanelOpen={setLeftPanelOpen}
          setOpenModalInfo={setOpenModalInfo}
        />
        <LeftPanel
          leftPanelOpen={leftPanelOpen}
          setLeftPanelOpen={setLeftPanelOpen}
          setOpenModalInfo={setOpenModalInfo}
        />
        <Modal
          openModalInfo={openModalInfo}
          setOpenModalInfo={setOpenModalInfo}
        />
      </div>
      <div className={classes.main}>
        <div className={classes.left}>
          <div className={classes.content}>
            <div className={classes.title}>INDIHU Mind</div>
            <p className={classes.description}>
              Shromažďování informací, podkladů, nejrůznějších materiálů a
              spojování informací v nové myšlenky či články je podstatou vědecké
              práce. INDIHU Mind je virtuální znalostní báze umožňující vědeckým
              a odborným pracovníkům zejména z humanitních oborů shromažďovat
              data, informace a znalosti.
            </p>
          </div>
        </div>
        <div className={classes.right}>
          <Card className={classes.card}>
            <CardContent>
              <Typography
                variant="h5"
                component="h2"
                className={classes.cardTitle}
              >
                Přihlášení
              </Typography>
              <LoginForm history={history} />
              <Typography variant="body1">
                Pro založení účtu nás kontaktujte na{" "}
                <a href="mailto:info@indihu.cz">info@indihu.cz</a>
              </Typography>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
};

export { Login as default };
