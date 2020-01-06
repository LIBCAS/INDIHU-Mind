import { useRef, useEffect } from "react";

// returns viewport height for mobile (without keyboard interface)

export function useWindowHeight(percent: number) {
  // The ref object is a generic container whose current property is mutable ...
  // ... and can hold any value, similar to an instance property on a class
  const ref = useRef<number>(window.innerHeight * 0.01 * percent);

  // Store current value in ref
  useEffect(() => {
    ref.current = window.innerHeight * 0.01 * percent;
  }, [percent, window.innerHeight]); // Only re-run if value changes

  // Return previous value (happens before update in useEffect above)
  return ref.current;
}
