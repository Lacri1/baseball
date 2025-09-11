import React, { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";

const sum = (arr) => (Array.isArray(arr) ? arr.reduce((a, b) => a + b, 0) : 0);

const ResultPage = () => {
  const { state } = useLocation();
  const navigate = useNavigate();
  const [winnerText, setWinnerText] = useState(null);

  // state 없을 때를 대비해 저장/복구
  useEffect(() => {
    if (state) {
      try {
        localStorage.setItem("resultPayload", JSON.stringify(state));
      } catch { }
    }
  }, [state]);

  const payload = useMemo(() => {
    if (state) return state;
    try {
      return JSON.parse(localStorage.getItem("resultPayload") || "{}");
    } catch {
      return {};
    }
  }, [state]);

  // gameState와 팀명 안전 추출
  const gameState = payload?.gameState || {};
  const homeTeam = payload?.homeTeam || gameState?.homeTeam || "홈 팀";
  const awayTeam = payload?.awayTeam || gameState?.awayTeam || "원정 팀";

  // 이닝별 득점 배열: 새 포맷( homeByInning/awayByInning ) 우선, 구포맷(score.my/opponent) 폴백
  const homeByInning =
    (Array.isArray(gameState?.homeByInning) && gameState.homeByInning) ||
    (Array.isArray(payload?.score?.my) && payload.score.my) ||
    [];

  const awayByInning =
    (Array.isArray(gameState?.awayByInning) && gameState.awayByInning) ||
    (Array.isArray(payload?.score?.opponent) && payload.score.opponent) ||
    [];

  // R(합계): 서버 총점이 있으면 우선 사용, 없으면 이닝 합
  const homeR =
    payload?.homeScore ??
    gameState?.homeScore ??
    gameState?.homeScoreTotal ??
    sum(homeByInning);

  const awayR =
    payload?.awayScore ??
    gameState?.awayScore ??
    gameState?.awayScoreTotal ??
    sum(awayByInning);

  // H/BB
  const homeHit = payload?.homeHit ?? gameState?.homeHit ?? 0;
  const awayHit = payload?.awayHit ?? gameState?.awayHit ?? 0;
  const homeWalks = payload?.homeWalks ?? gameState?.homeWalks ?? 0;
  const awayWalks = payload?.awayWalks ?? gameState?.awayWalks ?? 0;

  // 테이블 칼럼 길이 정렬
  const inningsCount = Math.max(homeByInning.length, awayByInning.length, 1);
  const norm = (arr) =>
    Array.from({ length: inningsCount }, (_, i) => (arr[i] ?? 0));

  const homeInnings = norm(homeByInning);
  const awayInnings = norm(awayByInning);

  // 승자 텍스트
  useEffect(() => {
    // gameState 자체도 전혀 없으면 홈으로
    if (!state && !localStorage.getItem("resultPayload")) {
      navigate("/");
      return;
    }

    const declaredWinner =
      payload?.winner ?? gameState?.winner ?? (homeR === awayR ? "무승부" : homeR > awayR ? homeTeam : awayTeam);

    let text = declaredWinner === "무승부" ? "무승부" : `${declaredWinner} 승리!`;
    if (Math.abs(homeR - awayR) >= 10) text += " ⚡ 콜드게임 종료!";
    setWinnerText(text);
  }, [state, payload, gameState, homeR, awayR, homeTeam, awayTeam, navigate]);

  return (
    <div style={{ padding: 20 }}>
      <h2>🏆 경기 결과</h2>
      <p>{winnerText}</p>

      <table border="1" cellPadding="5" style={{ width: "100%", marginBottom: 20 }}>
        <thead>
          <tr>
            <th>TEAM</th>
            {Array.from({ length: inningsCount }).map((_, i) => (
              <th key={i}>{i + 1}</th>
            ))}
            <th>R</th>
            <th>H</th>
            <th>BB</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>{homeTeam}</td>
            {homeInnings.map((s, i) => (
              <td key={i}>{s}</td>
            ))}
            <td>{homeR}</td>
            <td>{homeHit}</td>
            <td>{homeWalks}</td>
          </tr>
          <tr>
            <td>{awayTeam}</td>
            {awayInnings.map((s, i) => (
              <td key={i}>{s}</td>
            ))}
            <td>{awayR}</td>
            <td>{awayHit}</td>
            <td>{awayWalks}</td>
          </tr>
        </tbody>
      </table>

      <button onClick={() => navigate("/")}>메인 화면</button>
      <button onClick={() => navigate("/game/setup")} style={{ marginLeft: 10 }}>
        다시하기
      </button>
    </div>
  );
};

export default ResultPage;
