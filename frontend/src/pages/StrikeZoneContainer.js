import React, { useRef, useState } from "react";
import StrikeZone from "./StrikeZone";

const CM_IN_PX = 38;
const STRIKE_ZONE_SIZE = 220;

const StrikeZoneContainer = ({ selectedShot, setSelectedShot }) => {
  const wrapperRef = useRef(null);

  const handleClick = (e) => {
    const wrapper = wrapperRef.current;
    if (!wrapper) return;

    const rect = wrapper.getBoundingClientRect();
    const exX = e.clientX - rect.left;
    const exY = e.clientY - rect.top;

    const relX = exX - CM_IN_PX;
    const relY = exY - CM_IN_PX;

    const insideCore = relX >= 0 && relY >= 0 && relX <= STRIKE_ZONE_SIZE && relY <= STRIKE_ZONE_SIZE;
    const insideExtended = exX >= 0 && exY >= 0 && exX <= STRIKE_ZONE_SIZE + CM_IN_PX * 2 && exY <= STRIKE_ZONE_SIZE + CM_IN_PX * 2;

    const color = insideCore ? "blue" : insideExtended ? "green" : "red";

    const newShot = { relX: exX, relY: exY, color, insideCore, insideExtended };
    setSelectedShot(newShot);
  };

  return (
    <div ref={wrapperRef}>
      <StrikeZone shots={selectedShot ? [selectedShot] : []} CM_IN_PX={CM_IN_PX} STRIKE_ZONE_SIZE={STRIKE_ZONE_SIZE} onClick={handleClick} />
    </div>
  );
};

export default StrikeZoneContainer;
