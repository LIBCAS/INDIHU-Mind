import React, { useCallback, useEffect, useState } from "react";
import MuiTypography from "@material-ui/core/Typography";
import MuiPagination from "@material-ui/lab/Pagination";

import { AttachmentsSearch } from "./AttachmentsSearch";
import { AttachmentsAdd } from "./AttachmentsAdd";
import { Attachment } from "./_types";
import { AttachmentCard } from "./AttachmentCard";
import { getFiles } from "./_utils";

const pageSize = 20;

export const Attachments: React.FC = () => {
  const searchValue = new URLSearchParams(location.search).get("search");

  const [searchText, setSearchText] = useState<string | undefined>(
    searchValue ? decodeURI(searchValue) : undefined
  );
  const [searchKey, setSearchKey] = useState<boolean>(false);

  const [attachments, setAttachments] = useState<
    { items: Attachment[]; count: number } | undefined
  >(undefined);

  const [page, setPage] = useState<number>(0);

  const { items = [], count = 0 } = attachments || {};

  const refreshSearch = (newSearchText?: string) => {
    setSearchText(newSearchText);
    setSearchKey(!searchKey);
  };

  const handleSearch = (searchText: string) => {
    setSearchText(searchText);
  };

  const loadAttachments = useCallback(async (page: number, text?: string) => {
    const items = await getFiles(text, page, pageSize);
    if (items) {
      setAttachments(items);
    }
  }, []);

  const handlePagination = (_: React.ChangeEvent<unknown>, value: number) => {
    const page = value - 1;
    setPage(page);
    loadAttachments(page, searchText);
  };

  /** Loads attachments on search text change */
  useEffect(() => {
    setPage(0);
    loadAttachments(0, searchText);
  }, [searchText]);

  return (
    <div
      style={{
        padding: "15px",
        display: "flex",
        flexDirection: "column",
        height: "100%"
      }}
    >
      <div
        style={{
          display: "flex",
          flexDirection: "row",
          justifyContent: "space-between",
          flexWrap: "wrap"
        }}
      >
        <MuiTypography variant="h4" color="textPrimary">
          Seznam dokumentů
        </MuiTypography>
        <AttachmentsSearch
          key={`${searchKey}`}
          onChange={handleSearch}
          searchText={searchText}
        />
        <AttachmentsAdd
          update={() => {
            refreshSearch();
            setPage(0);
            loadAttachments(0);
          }}
        />
      </div>

      <div
        style={{
          display: "flex",
          flexDirection: "row",
          flexWrap: "wrap",
          justifyContent: items.length ? "start" : "center"
        }}
      >
        {items.length ? (
          items.map((attachment, index) => (
            <AttachmentCard
              key={`attachment-${attachment.name}-${index}`}
              attachment={attachment}
              update={() => loadAttachments(page, searchText)}
            />
          ))
        ) : (
          <></>
        )}
      </div>
      <div
        style={{
          display: "flex",
          flexDirection: "column",
          justifyContent: "center",
          alignItems: "center",
          marginTop: "auto",
          width: "100%"
        }}
      >
        {attachments && !count ? (
          <MuiTypography
            style={{ marginBottom: "1em" }}
            variant="h5"
            color="textSecondary"
          >
            Nebyly nalezeny žádné dokumenty.
          </MuiTypography>
        ) : count ? (
          <MuiPagination
            count={Math.ceil(count / pageSize)}
            page={page + 1}
            onChange={handlePagination}
          />
        ) : (
          <></>
        )}
      </div>
    </div>
  );
};
