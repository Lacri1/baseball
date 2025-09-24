// Scoreboard.js
import React, { useEffect, useRef, useState } from "react";

const Scoreboard = ({ gameState, homeTeam, awayTeam, lineups, inningCount }) => {
  const totalInnings = inningCount || 9;
  const homeScore = (Array.isArray(gameState.homeByInning)
    ? gameState.homeByInning
    : Array(totalInnings).fill(0)).slice(0, totalInnings);
  const awayScore = (Array.isArray(gameState.awayByInning)
    ? gameState.awayByInning
    : Array(totalInnings).fill(0)).slice(0, totalInnings);

  const isTop = (() => {
    const val = gameState?.isTop ?? gameState?.top ?? gameState?.offenseSide;
    if (typeof val === "boolean") return val;
    return String(val).toUpperCase() === "TOP";
  })();

  const homeTeamName = homeTeam || gameState?.homeTeam || "홈 팀";
  const awayTeamName = awayTeam || gameState?.awayTeam || "원정 팀";

  const currentBatterName = gameState?.currentBatter?.name || "타자";
  const currentPitcherName = gameState?.currentPitcher?.name || "투수";

  const [initialTopIsAway, setInitialTopIsAway] = useState(null);
  useEffect(() => { setInitialTopIsAway(isTop); }, []);

  const topTeam = initialTopIsAway ? awayTeamName : homeTeamName;
  const bottomTeam = initialTopIsAway ? homeTeamName : awayTeamName;
  const topScore = initialTopIsAway ? awayScore : homeScore;
  const bottomScore = initialTopIsAway ? homeScore : awayScore;

  const topRole = isTop ? "공격" : "수비";
  const bottomRole = isTop ? "수비" : "공격";

  return (
    <div style={{ overflowX: "auto" }}>
      <table border="1" cellPadding="10" style={{ width: "100%", textAlign: "center", borderCollapse: "collapse" }}>
        <thead>
          <tr>
            <th>Team</th>
            {Array.from({ length: totalInnings }).map((_, i) => <th key={i}>{i + 1}</th>)}
            <th>R</th><th>H</th><th>E</th><th>B</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>{topTeam}</td>
            {topScore.map((s, i) => <td key={i}>{s}</td>)}
            <td>{topScore.reduce((a, b) => a + b, 0)}</td>
            <td>{initialTopIsAway ? gameState.awayHit ?? 0 : gameState.homeHit ?? 0}</td>
            <td>0</td>
            <td>{initialTopIsAway ? gameState.awayWalks ?? 0 : gameState.homeWalks ?? 0}</td>
          </tr>
          <tr>
            <td>{bottomTeam}</td>
            {bottomScore.map((s, i) => <td key={i}>{s}</td>)}
            <td>{bottomScore.reduce((a, b) => a + b, 0)}</td>
            <td>{initialTopIsAway ? gameState.homeHit ?? 0 : gameState.awayHit ?? 0}</td>
            <td>0</td>
            <td>{initialTopIsAway ? gameState.homeWalks ?? 0 : gameState.awayWalks ?? 0}</td>
          </tr>
          <tr>
            <td colSpan={totalInnings + 5} style={{ backgroundColor: "#e0f0ff" }}>
              {`${topTeam} (${topRole}) - ${isTop ? `타자: ${currentBatterName}` : `투수: ${currentPitcherName}`}`}
            </td>
          </tr>
          <tr>
            <td colSpan={totalInnings + 5} style={{ backgroundColor: "#fff0e0" }}>
              {`${bottomTeam} (${bottomRole}) - ${isTop ? `투수: ${currentPitcherName}` : `타자: ${currentBatterName}`}`}
            </td>
          </tr>
          <tr>
            <td colSpan={totalInnings + 5}>{gameState.inning}회 {isTop ? "초" : "말"}</td>
          </tr>
        </tbody>
      </table>
    </div>
  );
};

export default Scoreboard;
