import React, { useEffect, useState, useMemo } from "react";
import { useLocation, useNavigate } from "react-router-dom";

const ResultPage = () => {
  const { state } = useLocation();
  const navigate = useNavigate();
  const [winner, setWinner] = useState(null);

  const gameState = state?.gameState;
  const homeTeam =
  state?.homeTeam || gameState?.homeTeam || "홈 팀";
const awayTeam =
  state?.awayTeam || gameState?.awayTeam || "원정 팀";

  const totalInnings = gameState?.inning || 9;


  // ✅ eventLog 기반 이닝별 점수 계산
  const { homeScores, awayScores } = useMemo(() => {
    const homeScores = Array(totalInnings).fill(0);
    const awayScores = Array(totalInnings).fill(0);

    if (!gameState?.eventLog) return { homeScores, awayScores };

    gameState.eventLog.forEach(event => {
      if (event.type === "PA_END" || event.type === "GAME_END") {
        const inningIndex = event.inning - 1;
        if (inningIndex >= 0) {
          homeScores[inningIndex] = event.homeScore ?? homeScores[inningIndex];
          awayScores[inningIndex] = event.awayScore ?? awayScores[inningIndex];
        }
      }
    });

    // 점수 변화만 반영 (누적 → 차이값 계산)
    for (let i = totalInnings - 1; i > 0; i--) {
      homeScores[i] -= homeScores[i - 1];
      awayScores[i] -= awayScores[i - 1];
    }

    return { homeScores, awayScores };
  }, [gameState, totalInnings]);

  const totalRuns = arr => arr.reduce((a, b) => a + b, 0);

  useEffect(() => {
    if (!gameState) {
      navigate("/");
      return;
    }

    const homeTotal = totalRuns(homeScores);
    const awayTotal = totalRuns(awayScores);

    let result = "무승부";
    if (homeTotal > awayTotal) result = `${homeTeam} 승리!`;
    else if (homeTotal < awayTotal) result = `${awayTeam} 승리!`;

    if (Math.abs(homeTotal - awayTotal) >= 10)
      result += " ⚡ 콜드게임 종료!";

    setWinner(result);
  }, [gameState, homeTeam, awayTeam, navigate, homeScores, awayScores]);

  if (!gameState) return null;

  return (
    <div style={{ padding: 20 }}>
      <h2>🏆 경기 결과</h2>
      <p>{winner}</p>
      <table border="1" cellPadding="5" style={{ width: "100%", marginBottom: 20 }}>
        <thead>
          <tr>
            <th>팀</th>
            {homeScores.map((_, i) => <th key={i}>{i + 1}이닝</th>)}
            <th>합계</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>{homeTeam}</td>
            {homeScores.map((s, i) => <td key={i}>{s}</td>)}
            <td>{totalRuns(homeScores)}</td>
          </tr>
          <tr>
            <td>{awayTeam}</td>
            {awayScores.map((s, i) => <td key={i}>{s}</td>)}
            <td>{totalRuns(awayScores)}</td>
          </tr>
        </tbody>
      </table>
      <div style={{ display: "flex", gap: 10 }}>
        <button onClick={() => navigate("/")}>메인 화면</button>
        <button onClick={() => navigate("/game/setup")}>다시하기</button>
      </div>
    </div>
  );
};

export default ResultPage;
