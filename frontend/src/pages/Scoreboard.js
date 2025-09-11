// Scoreboard.js
import React, { useEffect, useRef, useState } from "react";

const Scoreboard = ({ gameState, homeTeam, awayTeam, lineups, inningCount }) => {
  const totalInnings = inningCount || 9;

  // ===== 서버 제공 이닝별 득점 배열을 그대로 사용 (누적 아님) =====
  const homeScore = (Array.isArray(gameState.homeByInning)
    ? gameState.homeByInning
    : Array(totalInnings).fill(0)).slice(0, totalInnings);

  const awayScore = (Array.isArray(gameState.awayByInning)
    ? gameState.awayByInning
    : Array(totalInnings).fill(0)).slice(0, totalInnings);
  // ==========================================

  const isTop = (() => {
    const val = gameState?.isTop ?? gameState?.top ?? gameState?.offenseSide;
    if (typeof val === "boolean") return val;
    if (typeof val === "number") return val === 0;
    return String(val).toUpperCase() === "TOP";
  })();

  const homeTeamName = lineups?.home?.teamName || homeTeam || gameState?.homeTeam || "홈 팀";
  const awayTeamName = lineups?.away?.teamName || awayTeam || gameState?.awayTeam || "원정 팀";

  // 현재 타자/투수 정보 (서버에서 직접 받은 데이터 사용)
  const currentBatter = gameState?.currentBatter;
  const currentPitcher = gameState?.currentPitcher;

  // 현재 타자/투수 이름
  const currentBatterName = currentBatter?.name || "타자";
  const currentPitcherName = currentPitcher?.name || "투수";

  const [initialTopIsAway, setInitialTopIsAway] = useState(null);
  useEffect(() => { setInitialTopIsAway(isTop); }, []); // 최초 장면에서 TOP=원정 공격 기준 고정

  const topTeam = initialTopIsAway ? awayTeamName : homeTeamName;
  const bottomTeam = initialTopIsAway ? homeTeamName : awayTeamName;

  const topScore = initialTopIsAway ? awayScore : homeScore;
  const bottomScore = initialTopIsAway ? homeScore : awayScore;

  // ✅ 현재 하프이닝에 맞춰 라벨 동적 표시
  const topRole = isTop ? "공격" : "수비";
  const bottomRole = isTop ? "수비" : "공격";

  const prevState = useRef({ inning: null, currentBatter: null, currentPitcher: null });
  const [changeMsg, setChangeMsg] = useState("");
  useEffect(() => {
    let msg = "";
    if (prevState.current.inning !== null && prevState.current.inning !== gameState.inning)
      msg += ` (이닝 변경: ${prevState.current.inning}회 → ${gameState.inning}회 ${isTop ? "초" : "말"})`;
    if (prevState.current.currentBatter && prevState.current.currentBatter !== currentBatterName)
      msg += ` (타자 교체: ${prevState.current.currentBatter} → ${currentBatterName})`;
    if (prevState.current.currentPitcher && prevState.current.currentPitcher !== currentPitcherName)
      msg += ` (투수 교체: ${prevState.current.currentPitcher} → ${currentPitcherName})`;
    if (msg) setChangeMsg(msg);
    prevState.current = { inning: gameState.inning, currentBatter: currentBatterName, currentPitcher: currentPitcherName };
  }, [gameState.inning, currentBatterName, currentPitcherName, isTop]);

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
            {topScore.map((s, i) => (
              <td key={i} style={{ fontWeight: "normal", backgroundColor: "transparent" }}>{s}</td>
            ))}
            <td style={{ fontWeight: "bold" }}>{topScore.reduce((a, b) => a + b, 0)}</td>
            <td style={{ fontWeight: "bold" }}>{initialTopIsAway ? gameState?.awayHit ?? 0 : gameState?.homeHit ?? 0}</td>
            <td style={{ fontWeight: "bold" }}>{initialTopIsAway ? gameState?.awayStrikeOut ?? 0 : gameState?.homeStrikeOut ?? 0}</td>
            <td style={{ fontWeight: "bold" }}>{initialTopIsAway ? gameState?.awayWalks ?? 0 : gameState?.homeWalks ?? 0}</td>
          </tr>
          <tr>
            <td>{bottomTeam}</td>
            {bottomScore.map((s, i) => (
              <td key={i} style={{ fontWeight: "normal", backgroundColor: "transparent" }}>{s}</td>
            ))}
            <td style={{ fontWeight: "bold" }}>{bottomScore.reduce((a, b) => a + b, 0)}</td>
            <td style={{ fontWeight: "bold" }}>{initialTopIsAway ? gameState?.homeHit ?? 0 : gameState?.awayHit ?? 0}</td>
            <td style={{ fontWeight: "bold" }}>{initialTopIsAway ? gameState?.homeStrikeOut ?? 0 : gameState?.awayStrikeOut ?? 0}</td>
            <td style={{ fontWeight: "bold" }}>{initialTopIsAway ? gameState?.homeWalks ?? 0 : gameState?.awayWalks ?? 0}</td>
          </tr>

          {/* 현재 투수/타자 */}
          <tr>
            <td colSpan={totalInnings + 5} style={{ backgroundColor: "#e0f0ff", fontWeight: "bold" }}>
              {`${topTeam} (${topRole}) - ${isTop ? `타자: ${currentBatterName}` : `투수: ${currentPitcherName}`}`}
            </td>
          </tr>
          <tr>
            <td colSpan={totalInnings + 5} style={{ backgroundColor: "#fff0e0", fontWeight: "bold" }}>
              {`${bottomTeam} (${bottomRole}) - ${isTop ? `투수: ${currentPitcherName}` : `타자: ${currentBatterName}`}`}
            </td>
          </tr>

          {/* 이닝 */}
          <tr>
            <td colSpan={totalInnings + 5} style={{ fontWeight: "bold" }}>
              {gameState.inning}회 {isTop ? "초" : "말"}
            </td>
          </tr>
        </tbody>
      </table>

    </div>
  );
};

export default Scoreboard;
