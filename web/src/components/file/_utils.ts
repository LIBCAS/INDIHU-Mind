import { FileProps } from "../../types/file";
import { FileType } from "../../enums";
import { api } from "../../utils/api";

export const downloadFile = (file: FileProps) => {
  if (file.providerType === FileType.LOCAL) {
    api({ noContentType: true })
      .get(`attachment-file/${file.id}/download`)
      .then((response: any) => response.blob())
      .then((blob: any) => {
        const url = window.URL.createObjectURL(blob);
        let a = document.createElement("a");
        a.href = url;
        a.download = file.name;
        document.body.appendChild(a);
        a.click();
        a.remove();
      });
  } else {
    window.open(file.link, "_blank");
  }
};
