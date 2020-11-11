import React, { useContext, useState } from "react";
import { withRouter, RouteComponentProps } from "react-router-dom";
import MuiChip from "@material-ui/core/Chip";
import MuiCard from "@material-ui/core/Card";
import MuiCardContent from "@material-ui/core/CardContent";
import MuiCardActions from "@material-ui/core/CardActions";
import MuiTooltip from "@material-ui/core/Tooltip";
import MuiTypography from "@material-ui/core/Typography";

import { GlobalContext } from "../../context/Context";
import { labelActiveSet } from "../../context/actions/label";
import { LabelProps as LabelTypeProps } from "../../types/label";
import { LabelColorDot } from "./LabelColorDot";
import MuiClearIcon from "@material-ui/icons/Clear";
import { useLabelStyles } from "./Label.styles";
import MuiPopover from "@material-ui/core/Popover";
import MuiButton from "@material-ui/core/Button";

interface LabelProps {
  label: LabelTypeProps;
  onClick?: () => void;
  /**
   * If set, label will contain delete icon on hover
   */
  onDelete?: (label: LabelTypeProps) => void;
}

const LabelView: React.FC<LabelProps & RouteComponentProps> = ({
  label,
  onDelete,
  history,
}) => {
  const classes = useLabelStyles();

  const context: any = useContext(GlobalContext);

  const dispatch: Function = context.dispatch;

  const [open, setOpen] = useState<boolean>(false);

  const [anchorEl, setAnchorEl] = React.useState<Element | null>(null);

  const handleChipClick = (e: any) => {
    labelActiveSet(dispatch, label);
    history.push("/cards");
    e.stopPropagation();
  };

  const handleDelete = () => {
    if (onDelete) {
      onDelete(label);
    }
  };

  const handleDeleteClick = (event: React.MouseEvent) => {
    setAnchorEl(event.currentTarget);
    setOpen(true);
  };

  const handlePopover = () => {
    setOpen((prev) => !prev);
  };

  return (
    <React.Fragment>
      <MuiChip
        label={label.name}
        className={classes.label}
        onClick={handleChipClick}
        onDelete={onDelete && handleDeleteClick}
        icon={<LabelColorDot color={label.color} />}
        variant="outlined"
        deleteIcon={
          (onDelete && (
            <MuiTooltip title="Kliknutím odeberete štítek.">
              <MuiClearIcon
                fontSize="small"
                className={classes.deleteLabelIcon}
              />
            </MuiTooltip>
          )) ||
          undefined
        }
      />
      <MuiPopover
        open={open}
        anchorEl={anchorEl}
        disablePortal={false}
        anchorOrigin={{
          vertical: "bottom",
          horizontal: "center",
        }}
        transformOrigin={{
          vertical: "top",
          horizontal: "center",
        }}
        onBackdropClick={handlePopover}
      >
        <MuiCard>
          <MuiCardContent>
            <MuiTypography variant="subtitle1">Odebrat štítek?</MuiTypography>
          </MuiCardContent>
          <MuiCardActions>
            <MuiButton
              variant="text"
              style={{ color: "red" }}
              onClick={handleDelete}
              size="small"
            >
              Ano
            </MuiButton>
            <MuiButton variant="text" onClick={handlePopover} size="small">
              Ne
            </MuiButton>
          </MuiCardActions>
        </MuiCard>
      </MuiPopover>
    </React.Fragment>
  );
};

export const Label = withRouter(LabelView);
