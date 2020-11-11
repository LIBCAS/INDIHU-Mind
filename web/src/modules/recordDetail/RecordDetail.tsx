import React, { useState, useEffect, useCallback } from "react";
import { RouteComponentProps } from "react-router";
import { RecordProps } from "../../types/record";
import { Loader } from "../../components/loader/Loader";
import classNames from "classnames";

import { api } from "../../utils/api";
import { Typography } from "@material-ui/core";
import { RecordDetailActions } from "./RecordDetailActions";
import { RecordDetailContent } from "./RecordDetailContent";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";

export const RecordDetail: React.FC<RouteComponentProps> = ({
  history,
  match,
}) => {
  // @ts-ignore
  const recordId = match.params.id;
  const classesSpacing = useSpacingStyles();
  const [loading, setLoading] = useState<boolean>(true);
  const [detailContentKey, setDetailContentKey] = useState<boolean>(true);
  const [record, setRecord] = useState<RecordProps | null>(null);
  const loadRecord = useCallback(
    (refresh?: boolean) => {
      api()
        .get(`record/${recordId}`)
        .json<RecordProps>()
        .then((res: any) => {
          setLoading(false);
          if (res) {
            setRecord(res);
            if (refresh) {
              setDetailContentKey((detailContentKey) => !detailContentKey);
            }
          }
          return;
        })
        .catch(() => {
          setLoading(false);
          // TODO error
        });
    },
    [recordId]
  );
  useEffect(() => {
    loadRecord();
  }, [loadRecord]);
  return (
    <>
      <Loader loading={loading} />
      {!record && !loading && <div>Žádná citace</div>}
      {record && (
        <>
          <RecordDetailActions
            record={record}
            refresh={() => loadRecord(true)}
            history={history}
          />
          <Typography variant="h5" className={classNames(classesSpacing.mb1)}>
            Citace {record.name}
          </Typography>
          <RecordDetailContent key={`${detailContentKey}`} item={record} />
        </>
      )}
    </>
  );
};
