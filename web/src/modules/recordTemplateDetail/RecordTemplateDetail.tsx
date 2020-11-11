import React, { useState, useEffect, useCallback } from "react";
import { RouteComponentProps } from "react-router";
import { Loader } from "../../components/loader/Loader";
import classNames from "classnames";

import { api } from "../../utils/api";
import { Typography } from "@material-ui/core";
import { RecordTemplateDetailActions } from "./RecordTemplateDetailActions";
import { RecordTemplateDetailContent } from "./RecordTemplateDetailContent";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { RecordTemplateProps } from "../../types/recordTemplate";

export const RecordTemplateDetail: React.FC<RouteComponentProps> = ({
  history,
  match,
}) => {
  // @ts-ignore
  const recordTemplateId = match.params.id;
  const classesSpacing = useSpacingStyles();
  const [loading, setLoading] = useState<boolean>(true);
  const [recordTemplate, setRecord] = useState<RecordTemplateProps | null>(
    null
  );
  const loadRecordTemplate = useCallback(() => {
    api()
      .get(`template/${recordTemplateId}`)
      .json<RecordTemplateProps>()
      .then((res: any) => {
        setLoading(false);
        if (res) {
          setRecord(res);
        }
        return;
      })
      .catch(() => {
        setLoading(false);
        // TODO error
      });
  }, [recordTemplateId]);

  useEffect(() => {
    loadRecordTemplate();
  }, [loadRecordTemplate]);

  return (
    <>
      <Loader loading={loading} />
      {!recordTemplate && !loading && <div>Žádná citační šablona</div>}
      {recordTemplate && (
        <>
          <RecordTemplateDetailActions
            recordTemplate={recordTemplate}
            loadRecordTemplate={loadRecordTemplate}
            history={history}
          />
          <Typography variant="h5" className={classNames(classesSpacing.mb1)}>
            Citační šablona {recordTemplate.name}
          </Typography>
          <RecordTemplateDetailContent item={recordTemplate} />
        </>
      )}
    </>
  );
};
