// GameLogSide.js
import React, { useState } from "react";

const GameLogSide = ({ eventLog }) => {
  const [showLatest, setShowLatest] = useState(false);

  const handleClick = () => {
    setShowLatest(true); // 클릭하면 최신 로그 보여주기
  };

  return (
    <div style={{ display: "flex", gap: "20px" }}>
      {/* 클릭용 영역 */}
      <div
        onClick={handleClick}
        style={{
          border: "1px solid #ccc",
          padding: "10px",
          cursor: "pointer",
        }}
      >
        로그 보기
      </div>

      {/* 클릭 후 최신 로그 표시 */}
      {showLatest && eventLog.length > 0 && (
        <div
          style={{
            border: "1px solid #4a69bd",
            padding: "10px",
            background: "#f0f4ff",
          }}
        >
          <div><strong>Offense Team:</strong> {eventLog[eventLog.length - 1].offenseTeam}</div>
          <div><strong>Batter:</strong> {eventLog[eventLog.length - 1].batter}</div>
          <div><strong>Result:</strong> {eventLog[eventLog.length - 1].result}</div>
        </div>
      )}
    </div>
  );
};

export default GameLogSide;
