// StrikeZoneContainer.js
import React, { useRef, useState } from "react";
import StrikeZone from "./StrikeZone";
import { gameAPI } from "../api/api";

const CM_IN_PX = 38;
const STRIKE_ZONE_SIZE = 220;

const StrikeZoneContainer = ({ gameId, currentType, onServerUpdate, onActionComplete, onPitchMessage }) => {
  const [shots, setShots] = useState([]);
  const wrapperRef = useRef(null);

  const handleClick = async (e) => {
    if (!currentType) return;

    const rect = wrapperRef.current.getBoundingClientRect();
    const exX = e.clientX - rect.left;
    const exY = e.clientY - rect.top;

    const relX = exX - CM_IN_PX;
    const relY = exY - CM_IN_PX;

    const insideCore = relX >= 0 && relY >= 0 && relX <= STRIKE_ZONE_SIZE && relY <= STRIKE_ZONE_SIZE;
    const insideExtended = exX >= 0 && exY >= 0 && exX <= STRIKE_ZONE_SIZE + CM_IN_PX * 2 && exY <= STRIKE_ZONE_SIZE + CM_IN_PX * 2;

    const color = insideCore ? "blue" : insideExtended ? "green" : "red";
    setShots([{ relX: exX, relY: exY, color }]);

    try {
      if (currentType === "pitch" && gameId) {
        const type = color === "blue" ? "strike" : "ball";
        const res = await gameAPI.pitch(gameId, { type, pitchType: type, zoneColor: color });

        // 서버 메시지를 상위로 전달 → GamePage가 message 기반으로 표시
        if (onPitchMessage) {
          onPitchMessage(res?.data?.message || "");
        }

        if (onServerUpdate) await onServerUpdate();
        if (onActionComplete) onActionComplete();
      }
    } catch (err) {
      console.error("투구 처리 실패:", err);
    }
  };

  return (
    <div ref={wrapperRef}>
      <StrikeZone
        shots={shots}
        CM_IN_PX={CM_IN_PX}
        STRIKE_ZONE_SIZE={STRIKE_ZONE_SIZE}
        onClick={handleClick}
      />
    </div>
  );
};

export default StrikeZoneContainer;
