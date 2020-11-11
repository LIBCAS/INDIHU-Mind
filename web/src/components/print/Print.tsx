import React, { useRef, CSSProperties, ReactElement } from "react";
import { useReactToPrint } from "react-to-print";
import Button from "@material-ui/core/Button";
import PrintIcon from "@material-ui/icons/Print";

import { useStyles } from "./_styles";

interface PrintProps {
  ComponentToPrint: (_: any) => ReactElement | null;
  label?: string;
  variant?: "outlined";
  size?: "small";
  className?: string;
  style?: CSSProperties;
  componentToPrintProps?: any;
  disabled?: boolean;
}

class PrintComponent extends React.Component<any, any> {
  render() {
    const { content, classes } = this.props;
    return <div className={classes.printContent}>{content}</div>;
  }
}

export const Print: React.FC<PrintProps> = ({
  ComponentToPrint,
  label = "Tisk",
  style,
  componentToPrintProps = {},
  ...props
}) => {
  const classes = useStyles();

  const componentRef = useRef<any>();

  const handlePrint = useReactToPrint({
    content: () => componentRef.current,
    copyStyles: true,
    bodyClass: classes.printWrapper,
  });

  return (
    <div style={style}>
      <Button
        variant="contained"
        {...props}
        startIcon={<PrintIcon />}
        onClick={handlePrint}
      >
        {label}
      </Button>
      <PrintComponent
        ref={componentRef}
        classes={classes}
        content={
          <ComponentToPrint {...componentToPrintProps} classes={classes} />
        }
      />
    </div>
  );
};
