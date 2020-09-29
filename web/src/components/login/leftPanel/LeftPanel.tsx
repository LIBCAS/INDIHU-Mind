import * as React from "react";
import {
  Drawer,
  Link,
  ListItem,
  List,
  Button,
  ListItemText
} from "@material-ui/core";
import { useStyles } from "./_leftPanelStyle";

export interface LeftPanelProps {
  leftPanelOpen: boolean;
  setLeftPanelOpen: any;
  setOpenModalInfo: any;
}

export const LeftPanel: React.SFC<LeftPanelProps> = ({
  leftPanelOpen,
  setLeftPanelOpen,
  setOpenModalInfo
}) => {
  const classes = useStyles();

  const clickHandle = () => {
    setOpenModalInfo(true);
  };

  return (
    <div>
      <Drawer
        open={leftPanelOpen}
        anchor="left"
        onClose={() => setLeftPanelOpen(false)}
      >
        <div className={classes.list} role="presentation">
          <List>
            <ListItem button onClick={clickHandle}>
              <ListItemText primary="O aplikaci" />
            </ListItem>
            <ListItem button component="a" href="https://indihu.cz/">
              <ListItemText primary="O projektu INDIHU" />
            </ListItem>
            <ListItem
              button
              component="a"
              href="https://github.com/LIBCAS/INDIHU-Mind"
            >
              <ListItemText primary="Info o INDIHU Mind" />
            </ListItem>
            <ListItem button component="a" href="https://exhibition.indihu.cz/">
              <ListItemText primary="INDIHU Exhibition" />
            </ListItem>
            <ListItem button component="a" href="https://ocr.indihu.cz/">
              <ListItemText primary="INDIHU OCR" />
            </ListItem>
          </List>
        </div>
      </Drawer>
    </div>
  );
};
