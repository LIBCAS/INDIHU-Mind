import React from "react";

import { LabelProps } from "../types/label";
import { CategoryProps } from "../types/category";
import { CardTemplateProps } from "../types/cardTemplate";
import { ContextProvider } from "./ContextProvider";
import { RecordProps } from "../types/record";

export interface StateProps {
  // search for table component
  search: {
    categoryName: string;
    labelName: string;
    // json body to filter cards table in POST request
    category: any;
    label: any;
  };
  category: {
    categoryActive: CategoryProps | undefined;
    categories: CategoryProps[];
  };
  label: {
    labelActive: LabelProps | undefined;
    labels: LabelProps[];
  };
  template: {
    templates: CardTemplateProps[];
  };
  status: {
    // number of loading & error events
    loadingCount: number;
    errorCount: number;
    errorText: string;
  };
  users: {
    // if user table should fetch new data
    // created new user outside table component
    updated: boolean;
  };
  record: {
    marc: any;
    records: RecordProps[];
  };
  recordTemplate: {
    recordsTemplates: any[];
  };
}

export interface ActionProps {
  type: string;
  payload: any;
}

export const initialState: StateProps = {
  search: {
    categoryName: "",
    labelName: "",
    category: {},
    label: {},
  },
  category: {
    categoryActive: undefined,
    categories: [],
  },
  label: {
    labelActive: undefined,
    labels: [],
  },
  template: {
    templates: [],
  },
  status: {
    loadingCount: 0,
    errorCount: 0,
    errorText: "",
  },
  users: {
    updated: false,
  },
  record: {
    marc: null,
    records: [],
  },
  recordTemplate: {
    recordsTemplates: [],
  },
};

export const GlobalContext = React.createContext({});

export const GlobalProvider = GlobalContext.Provider;
export const GlobalConsumer = GlobalContext.Consumer;

export const Context: React.FC = ({ children }) => {
  // const isDevelop = process.env.NODE_ENV === "development";
  return (
    <>
      <ContextProvider>{children}</ContextProvider>
    </>
  );
};
