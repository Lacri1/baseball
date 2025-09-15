import React, { useRef, useState } from "react";
import StrikeZone from "./StrikeZone";
import api from "../api";

const CM_IN_PX = 38;
const STRIKE_ZONE_SIZE = 220;

const StrikeZoneContainer = ({ gameState, setGameState, currentType, gaugeValue, setMessage }) => {
  const [shots, setShots] = useState([]);
  const wrapperRef = useRef(null);

  const handleClick = async (e) => {
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
    const newShot = { relX: exX, relY: exY, color, insideCore, insideExtended, fading: false };

    // 이전 클릭 제거, 새 클릭만 표시
    setShots([newShot]);

    // 0.05초 후 점점 사라지도록
   

    // 0.55초 후 완전히 제거
   

    try {
      await api.post("/api/game/strikezone", newShot);
    } catch (err) {
      console.error("클릭 전송 실패:", err);
    }

    if (!currentType) return;

    setGameState(prev => {
      const updated = { ...prev };
      if (currentType === "pitch") {
        const isStrike = insideCore || Math.random() < gaugeValue / 100;
        if (isStrike) updated.strikes = Math.min(updated.strikes + 1, 3);
        else updated.balls = Math.min(updated.balls + 1, 4);
        setMessage(`투구 완료! 게이지: ${gaugeValue.toFixed(0)}`);
      } else if (currentType === "swing") {
        const isHit = insideCore || Math.random() < gaugeValue / 100;
        if (isHit) {
          const team = updated.isTop ? "opponent" : "my";
          updated.score[team][updated.inning - 1] += 1;
        }
        setMessage(`타격 완료! 게이지: ${gaugeValue.toFixed(0)}`);
      }
      return updated;
    });

    try {
      await api.post("/game/update", { type: currentType, gaugeValue, gameState });
    } catch (err) {
      console.error("게임 상태 전송 실패:", err);
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
