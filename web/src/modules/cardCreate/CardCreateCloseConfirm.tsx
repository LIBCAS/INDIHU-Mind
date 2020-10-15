import React from "react";
import MuiTypography from "@material-ui/core/Typography";
import MuiButton from "@material-ui/core/Button";

export interface CardCreateCloseConfirmProps {
  onCancel: VoidFunction;
  onSubmit: VoidFunction;
}

export const CardCreateCloseConfirm: React.FC<CardCreateCloseConfirmProps> = ({
  onCancel,
  onSubmit
}) => {
  return (
    <div style={{ display: "flex", flexDirection: "column", padding: "10px" }}>
      <MuiTypography variant="h5" color="inherit" align="center" gutterBottom>
        Potvrzení zahození změn
      </MuiTypography>
      <MuiTypography variant="body1" align="center" style={{ margin: "25px" }}>
        Opravdu si přejete ukončit vytváření karty?
        <br /> Můžete tak přijít o neuložené změny!
      </MuiTypography>
      <div
        style={{
          display: "flex",
          flexDirection: "row",
          justifyContent: "flex-end"
        }}
      >
        <MuiButton
          style={{ margin: "0 3px" }}
          variant="outlined"
          color="secondary"
          size="medium"
          onClick={onCancel}
        >
          Pokračovat
        </MuiButton>
        <MuiButton
          style={{ margin: "0 3px" }}
          variant="outlined"
          color="primary"
          size="medium"
          onClick={onSubmit}
        >
          Ukončit
        </MuiButton>
      </div>
    </div>
  );
};
