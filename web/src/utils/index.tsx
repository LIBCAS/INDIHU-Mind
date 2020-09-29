import React from "react";

export const formatBytes = (bytes: number, decimals = 2) => {
  if (bytes === 0) return "0 bajtů";
  if (!bytes) return "Neznámé";

  const k = 1024;
  const dm = decimals < 0 ? 0 : decimals;
  const sizes = ["bajtů", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"];

  const i = Math.floor(Math.log(bytes) / Math.log(k));

  return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + " " + sizes[i];
};

export const formatMultiline = (value?: string | null) =>
  value
    ? value.split("").map((char: string, i) => {
        const key = `${char}-${i}`;
        return char === "\n" ? <br key={key} /> : <span key={key}>{char}</span>;
      })
    : "";

export const openInNewTab = (url: string) => {
  const a = document.createElement("a");
  a.href = /^https?:\/\/.*$/.test(url) ? url : `http://${url}`;
  a.target = "_blank";
  a.className = "hidden";
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
};
