import React, { useState } from "react";
import Button from "@material-ui/core/Button";

import { Loader } from "../../components/loader/Loader";
import { reindex } from "./_utils";

export const Administration: React.FC = () => {
  const [loading, setLoading] = useState(false);

  const handleReindex = async () => {
    setLoading(true);
    await reindex();
    setLoading(false);
  };

  return (
    <>
      <Loader loading={loading} />
      <div style={{ width: "100%" }}>
        <Button
          onClick={handleReindex}
          fullWidth
          color="primary"
          variant="outlined"
          disabled={loading}
        >
          Reindex
        </Button>
      </div>
    </>
  );
};
