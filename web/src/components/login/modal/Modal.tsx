import * as React from "react";
import {
  createStyles,
  Theme,
  withStyles,
  WithStyles
} from "@material-ui/core/styles";
import {
  Dialog,
  DialogContent,
  Typography,
  IconButton
} from "@material-ui/core";
import MuiDialogTitle from "@material-ui/core/DialogTitle";
import CloseIcon from "@material-ui/icons/Close";

const styles = (theme: Theme) =>
  createStyles({
    root: {
      margin: 0,
      padding: theme.spacing(2)
    },
    closeButton: {
      position: "absolute",
      right: theme.spacing(1),
      top: theme.spacing(1),
      color: theme.palette.grey[500]
    }
  });

export interface ModalProps {
  openModalInfo: any;
  setOpenModalInfo: any;
}

export interface DialogTitleProps extends WithStyles<typeof styles> {
  id: string;
  children: React.ReactNode;
  onClose: () => void;
}

const DialogTitle = withStyles(styles)((props: DialogTitleProps) => {
  const { children, classes, onClose, ...other } = props;
  return (
    <MuiDialogTitle disableTypography className={classes.root} {...other}>
      <Typography variant="h6">{children}</Typography>
      {onClose ? (
        <IconButton
          aria-label="close"
          className={classes.closeButton}
          onClick={onClose}
        >
          <CloseIcon />
        </IconButton>
      ) : null}
    </MuiDialogTitle>
  );
});

export const Modal: React.SFC<ModalProps> = ({
  openModalInfo,
  setOpenModalInfo
}) => {
  const handleClose = () => {
    setOpenModalInfo(false);
  };

  return (
    <Dialog
      onClose={handleClose}
      aria-labelledby="customized-dialog-title"
      open={openModalInfo}
      fullWidth
      maxWidth={"lg"}
    >
      <DialogTitle id="customized-dialog-title" onClose={handleClose}>
        INDIHU Mind – základní informace
      </DialogTitle>
      <DialogContent dividers>
        <Typography gutterBottom>
          INDIHU Mind je virtuální znalostní báze umožňující vědeckým a odborným
          pracovníkům zejména z humanitních oborů shromažďovat informace a
          znalosti. V praxi by tato aplikace měla nahradit lístkové excerpční
          kartotéky, které v minulosti sloužily k tomuto účelu. Uložené
          informace budou obsahovat údaje o zdroji, aby byly kdykoliv v
          budoucnosti zpětně vyhledatelné. Využití nástroje IndihuMind by mělo
          být dlouhodobé, ale lze ho použít i krátkodobě pro realizaci časově
          omezených projektů. Výhodou je možnost sdílení s dalšími
          spolupracovníky. Uložená data mohou být zabezpečena proti
          neautorizovanému použití.
        </Typography>
        <Typography gutterBottom>
          Základ tvoří databáze karet obsahujících krátké texty včetně tabulek a
          grafů ale součástí mohou být též obrazové a zvukové dokumenty a krátké
          videosekvence připojené ke kartám jako příloha. Každá karta může být
          vybavena dalšími funkčními prvky jako budou kategorie, atributy,
          štítky a přílohy, které mohou uživatelé vytvářet podle vlastní
          potřeby. Možnost využití rozsáhlého podpůrného aparátu umožňuje třídit
          a řadit informace podle různých kritérií a zejména umožňuje různé
          způsoby vyhledávání a jejich vzájemné kombinace. Pomocí kategorií lze
          např. budovat i strukturovaný hierarchický systém. Tyto vlastnosti
          budou užitečné zejména v případě dlouhodobého používání nástroje v
          horizontu více let a velkému množství shromážděných informací
          zachycených na kartách. Uživatelé nejsou limitováni předem danými
          strukturami nebo předepsanými prvky a mohou tak nástroj přizpůsobit
          charakteru sbíraných informací/znalosti a tvůrčím způsobem ho
          využívat. Nástroj umožní uchovat i kritické připomínky nebo vlastní
          texty a nápady.
        </Typography>
        <Typography gutterBottom>
          Aplikace umožňuje práci s kartami, jejich vytváření, editaci, zrušení
          a opatřování kategoriemi a atributy podle vlastní volby. Každá karta
          je identifikována názvem a jedinečným identifikátorem. Při uložení je
          karta označena datem vzniku. Z nástrojové lišty je možné volit operace
          s kartami, kategoriemi, citacemi, správou uživatelů a vyhledávací okno
          umožňuje vyhledávání v textových atributech a obsahu karet. Vzhledem k
          tomu, že se jedná o otevřený systém (open source) s volným zdrojovým
          kódem, je možné systém i v budoucnosti rozvíjet a obohacovat o další
          funkční komponenty.
        </Typography>
      </DialogContent>
    </Dialog>
  );
};
