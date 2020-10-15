import { RecordTemplateDetail } from "./../modules/recordTemplateDetail/RecordTemplateDetail";
import { RecordDetail } from "./../modules/recordDetail/RecordDetail";
import { RecordsTemplates } from "./../modules/recordsTemplates/RecordsTemplates";
import { Login } from "../modules/login/Login";
import { Cards } from "../modules/cards/Cards";
import { CardDetail } from "../modules/cardDetail/CardDetail";
import { Records } from "../modules/records/Records";
import { Templates } from "../modules/templates/Templates";
import { Categories } from "../modules/categories/Categories";
import { Search } from "../modules/search/Search";
import { UserCreate } from "../modules/userCreate/UserCreate";

export interface RoutesProps {
  path: string;
  label: string;
  component: any;
  exact: boolean;
}

export const routesTabs: RoutesProps[] = [
  {
    path: "/cards",
    label: "Karty",
    component: Cards,
    exact: false
  },
  {
    path: "/templates",
    label: "Šablony",
    component: Templates,
    exact: true
  },
  {
    path: "/categories",
    label: "Kategorie",
    component: Categories,
    exact: true
  },
  {
    path: "/records",
    label: "Citace",
    component: Records,
    exact: true
  },
  {
    path: "/records-templates",
    label: "Citační šablony",
    component: RecordsTemplates,
    exact: true
  }
];

export const adminRoutes: RoutesProps[] = [
  {
    path: "/create-user",
    label: "Správa uživatelů",
    component: UserCreate,
    exact: true
  }
];

export const routes: RoutesProps[] = [
  {
    path: "/",
    label: "Přihlášení",
    component: Login,
    exact: true
  },
  {
    path: "/card/:id",
    label: "Detail karty",
    component: CardDetail,
    exact: true
  },
  {
    path: "/record/:id",
    label: "Detail citace",
    component: RecordDetail,
    exact: true
  },
  {
    path: "/template/:id",
    label: "Detail citační šablony",
    component: RecordTemplateDetail,
    exact: true
  },
  {
    path: "/search",
    label: "Vyhledávání",
    component: Search,
    exact: true
  },
  ...routesTabs
];
