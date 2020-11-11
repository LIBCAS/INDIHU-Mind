import { useState, useEffect } from "react";
import { debounce } from "lodash";

export const useWindowSize = (): { width: number; height: number } => {
  const [windowSize, setWindowSize] = useState(() => ({
    width: window.innerWidth,
    height: window.innerHeight,
  }));

  useEffect(() => {
    const onResize = debounce(
      () =>
        setWindowSize({ width: window.innerWidth, height: window.innerHeight }),
      150
    );
    window.addEventListener("resize", onResize);
    return () => window.removeEventListener("resize", onResize);
  });

  return windowSize;
};
