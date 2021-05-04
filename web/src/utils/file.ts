import {
  STATUS_ERROR_COUNT_CHANGE,
  STATUS_ERROR_TEXT_SET,
} from "../context/reducers/status";
import { api } from "./api";

export const generateFile = (
  endpoint: string,
  json: unknown,
  dispatch: Function,
  setLoading: (loading: boolean) => void,
  fileType?: string
) => {
  api()
    .post(endpoint, {
      json,
    })
    .then((response: any) => {
      let filename = "";
      let disposition = response.headers.get("Content-Disposition");
      if (disposition && disposition.indexOf("attachment") !== -1) {
        let filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
        let matches = filenameRegex.exec(disposition);
        if (matches != null && matches[1])
          filename = matches[1].replace(/['"]/g, "");
      }
      return response.blob().then((blob: any) => ({ blob, name: filename }));
    })
    .then(({ blob, name }: { blob: any; name: any }) => {
      const url = window.URL.createObjectURL(blob);
      let a = document.createElement("a");
      a.href = url;
      a.download = name;
      document.body.appendChild(a);
      a.click();
      dispatch({
        type: STATUS_ERROR_TEXT_SET,
        payload: `${
          fileType ? `${fileType} soubor` : "Soubor"
        } byl úspěšně vygenerován`,
      });
      dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
      setLoading(false);
      a.remove();
    })
    .catch((e: any) => {
      setLoading(false);
      dispatch({
        type: STATUS_ERROR_TEXT_SET,
        payload: `${
          fileType ? `${fileType} soubor` : "Soubor"
        } se nepovedlo vygenerovat`,
      });
      dispatch({ type: STATUS_ERROR_COUNT_CHANGE, payload: 1 });
    });
};
