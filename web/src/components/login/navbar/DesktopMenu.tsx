import * as React from "react";
import { Link } from "@material-ui/core";
import { useStyles as useTextStyles } from "../../../theme/styles/textStyles";

export interface DesktopMenuProps {
  setOpenModalInfo: any;
}

const DesktopMenu: React.SFC<DesktopMenuProps> = ({ setOpenModalInfo }) => {
  const classesText = useTextStyles();

  const clickHandle = () => {
    setOpenModalInfo(true);
  };

  return (
    <div>
      <Link onClick={clickHandle} className={classesText.textLink}>
        O aplikaci
      </Link>
      <Link href="https://indihu.cz/" className={classesText.textLink}>
        O projektu INDIHU
      </Link>
      <Link
        href="https://github.com/LIBCAS/INDIHU-Mind"
        className={classesText.textLink}
      >
        Info o INDIHU Mind
      </Link>
      <Link
        href="https://exhibition.indihu.cz/"
        className={classesText.textLink}
      >
        INDIHU Exhibition
      </Link>
      <Link href="https://ocr.indihu.cz/" className={classesText.textLink}>
        INDIHU OCR
      </Link>
    </div>
  );
};

export default DesktopMenu;
