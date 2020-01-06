import React, { useContext, useEffect } from "react";

import {
  STATUS_ERROR_COUNT_CHANGE,
  STATUS_ERROR_TEXT_SET
} from "../../context/reducers/status";
import { GlobalContext, StateProps } from "../../context/Context";
import { Loader } from "../../components/loader/Loader";
import { MessageSnackbar } from "../../components/messages/MessageSnackbar";
import { RECORD_MARC_GET } from "../../context/reducers/record";
import { recordGetMarc } from "../../context/actions/record";

export const Status: React.FC = () => {
  const context: any = useContext(GlobalContext);
  const state: StateProps = context.state;
  const { marc } = state.record;
  const dispatch: Function = context.dispatch;
  const setVisible = () => {
    dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: -1 });
    if (state.status.errorCount <= 1) {
      dispatch({ type: STATUS_ERROR_TEXT_SET, payload: "" });
    }
  };
  useEffect(() => {
    if (marc) {
      return;
    } else {
      recordGetMarc(dispatch);
    }
  }, [marc]);
  return (
    <>
      <Loader loading={state.status.loadingCount !== 0} />
      {state.status.errorCount !== 0 && (
        <MessageSnackbar
          setVisible={setVisible}
          message={
            state.status.errorText !== "" ? state.status.errorText : undefined
          }
        />
      )}
    </>
  );
};
