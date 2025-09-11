// GamePage.js
import React, { useState, useEffect, useRef } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { gameAPI } from "../api/api";
import Scoreboard from "./Scoreboard";
import Scoreboard22 from "./Scoreboard22";
import StrikeZoneContainer from "./StrikeZoneContainer";
import Bases from "./Bases";
import { PitchGauge, SwingGauge } from "./PitchGauge";
import MessageBox from "./MessageBox";

const GamePage = () => {
  const { state } = useLocation();
  const navigate = useNavigate();

  const savedGameInfo = JSON.parse(localStorage.getItem("gameInfo") || "{}");
  const {
    gameId,
    userTeam,
    homeTeam,
    awayTeam,
    inningCount,
  } = state || savedGameInfo || {};

  useEffect(() => {
    if (state) localStorage.setItem("gameInfo", JSON.stringify(state));
  }, [state]);

  useEffect(() => {
    if (!gameId) navigate("/game/setup");
  }, [gameId, navigate]);

  const [gameState, setGameState] = useState({
    inning: 1,
    isTop: true,
    balls: 0,
    strikes: 0,
    outs: 0,
    bases: [false, false, false],
    currentBatter: null,
    currentPitcher: null,
    eventLog: [],
    gameOver: false,
    winner: undefined,
    homeHit: 0,
    awayHit: 0,
    homeWalks: 0,
    awayWalks: 0,
    offenseTeam: null,
    defenseTeam: null,
    offenseSide: "TOP",
    // 스코어보드(서버 배열/합계 저장)
    homeByInning: Array(inningCount || 9).fill(0),
    awayByInning: Array(inningCount || 9).fill(0),
    homeScoreTotal: 0,
    awayScoreTotal: 0,
    homeTeam,
    awayTeam,
  });

  const [message, setMessage] = useState("");
  const [pitchGauge, setPitchGauge] = useState(0);
  const [swingGauge, setSwingGauge] = useState(0);
  const [animating, setAnimating] = useState(false);
  const [currentType, setCurrentType] = useState(null);
  const gaugeInterval = useRef(null);

  const clearGaugeInterval = () => {
    if (gaugeInterval.current) {
      clearInterval(gaugeInterval.current);
      gaugeInterval.current = null;
    }
  };

  const fetchGameState = async () => {
    if (!gameId) return;
    try {
      const [viewRes, sbRes] = await Promise.all([
        gameAPI.getGameView(gameId),
        gameAPI.getScoreboard(gameId),
      ]);

      const data = viewRes.data.data;
      const sb = sbRes?.data?.data;

      const nextIsTop = typeof data.isTop === "boolean"
        ? data.isTop
        : (String(data.offenseSide || "TOP").toUpperCase() === "TOP");

      setGameState((prev) => ({
        ...prev,
        inning: data.inning,
        isTop: nextIsTop,
        balls: data.ball,
        strikes: data.strike,
        outs: data.out,
        bases: data.bases || [false, false, false],

        // 스코어보드 응답 저장
        homeByInning: sb?.homeByInning ?? prev.homeByInning,
        awayByInning: sb?.awayByInning ?? prev.awayByInning,
        homeScoreTotal: typeof sb?.homeScore === "number" ? sb.homeScore : prev.homeScoreTotal,
        awayScoreTotal: typeof sb?.awayScore === "number" ? sb.awayScore : prev.awayScoreTotal,

        currentBatter: data.currentBatter || prev.currentBatter,
        currentPitcher: data.currentPitcher || prev.currentPitcher,
        eventLog: Array.isArray(data.eventLog) ? data.eventLog : prev.eventLog,
        gameOver: !!data.gameOver,
        winner: data.winner ?? prev.winner,
        homeHit: data.homeHit ?? prev.homeHit,
        awayHit: data.awayHit ?? prev.awayHit,
        homeWalks: data.homeWalks ?? prev.homeWalks,
        awayWalks: data.awayWalks ?? prev.awayWalks,
        offenseTeam: data.offenseTeam ?? prev.offenseTeam,
        defenseTeam: data.defenseTeam ?? prev.defenseTeam,
        offenseSide: (data.offenseSide || prev.offenseSide || "TOP"),
        homeTeam: homeTeam ?? prev.homeTeam,
        awayTeam: awayTeam ?? prev.awayTeam,
      }));
    } catch (err) {
      console.error("게임 상태 로딩 실패:", err);
    }
  };

  useEffect(() => {
    if (!gameId) return;
    fetchGameState();
    const interval = setInterval(fetchGameState, 1000);
    return () => clearInterval(interval);
  }, [gameId]);

  // ----- 메시지 파서 (스윙 안 함이면 좌측 투구 판정만) -----
  const parseServerMessage = (rawMsg = "") => {
    let msg = rawMsg || "";
    msg = msg.replace("스윙/노스윙 처리 완료:", "").trim();
    const [leftRaw, rightRaw] = msg.split("|").map(s => (s || "").trim());
    const leftPitch = (leftRaw || "").replace("투구 처리 완료:", "").trim();
    const rightAction = rightRaw || "";
    const isNoSwing = /스윙\s*안\s*함/u.test(msg);

    if (isNoSwing) return leftPitch || "볼";
    if (msg.includes("타격")) {
      let onlyHit = rightAction || msg;
      onlyHit = onlyHit.replace(/^컴퓨터\s*타격:\s*/u, "").trim();
      return onlyHit || "타석 결과";
    }
    if (leftPitch) return leftPitch;
    msg = msg.replace("투구 처리 완료:", "").trim();
    return msg || "타석 결과";
  };
  // ---------------------------------------------------------

  // ✅ 볼넷/삼진 추론용 스냅샷
  const countsBeforeActionRef = useRef({
    balls: 0,
    strikes: 0,
    homeWalks: 0,
    awayWalks: 0,
  });

  const snapshotCounts = () => {
    countsBeforeActionRef.current = {
      balls: gameState.balls ?? 0,
      strikes: gameState.strikes ?? 0,
      homeWalks: gameState.homeWalks ?? 0,
      awayWalks: gameState.awayWalks ?? 0,
    };
  };

  // ✅ 최종 메시지 확정 (볼넷/삼진 자동 승격)
  const inferAndSetMessage = (rawMsg = "", resData = null) => {
    const prev = countsBeforeActionRef.current;
    const parsed = parseServerMessage(rawMsg);

    // 서버 메시지에 직접 포함
    if (/볼넷|4구/u.test(rawMsg)) {
      setMessage("볼넷");
      return;
    }

    // 직전 3B 이후 "볼" → 볼넷
    if (parsed === "볼" && prev.balls >= 3) {
      setMessage("볼넷");
      return;
    }

    // 서버 데이터 walk 증가 → 볼넷
    if (resData) {
      const walked =
        (typeof resData.homeWalks === "number" && resData.homeWalks > prev.homeWalks) ||
        (typeof resData.awayWalks === "number" && resData.awayWalks > prev.awayWalks);
      if (walked) {
        setMessage("볼넷");
        return;
      }
    }

    // 직전 2S 이후 "스트라이크" → 삼진
    if (parsed === "스트라이크" && prev.strikes >= 2) {
      setMessage("삼진 아웃");
      return;
    }

    setMessage(parsed || "타석 결과");
  };

  // 공수 전환 시 버튼/메시지 초기화
  const prevTurnRef = useRef(null);
  const userIsHome = (homeTeam === userTeam);
  const offenseIsTop = String(gameState.offenseSide || "TOP").toUpperCase() === "TOP";
  const isUserOffenseNow = offenseIsTop ? !userIsHome : userIsHome;

  useEffect(() => {
    const signature = `${gameState.inning}-${gameState.offenseSide}-${isUserOffenseNow}`;
    if (prevTurnRef.current !== signature) {
      prevTurnRef.current = signature;
      clearGaugeInterval();
      setAnimating(false);
      setCurrentType(null);
      setSwingGauge(0);
      setPitchGauge(0);
      setMessage(
        isUserOffenseNow
          ? "공격 턴입니다. ‘타격 준비’ 후 스윙하세요."
          : "수비 턴입니다. ‘투구’ 후 존을 클릭하세요."
      );
    }
  }, [gameState.inning, gameState.offenseSide, isUserOffenseNow]);

  // ✅ 게임 종료 감지 → 결과 페이지 이동
  useEffect(() => {
    if (gameState.gameOver) {
      navigate("/game/result", {
        state: {
          gameState,
          homeTeam,
          awayTeam,
          winner: gameState.winner,
        },
      });
    }
  }, [gameState.gameOver, gameState, homeTeam, awayTeam, navigate]);

  // 스윙 게이지 시작
  const startSwingGauge = () => {
    if (animating) return;
    setCurrentType("swing");
    setAnimating(true);
    let val = 0;
    clearGaugeInterval();
    gaugeInterval.current = setInterval(() => {
      val += 2;
      if (val > 100) val = 0;
      setSwingGauge(val);
    }, 20);
  };

  // 스윙
  const handleSwing = async () => {
    if (!animating || currentType !== "swing") return;
    clearGaugeInterval();
    setAnimating(false);

    snapshotCounts(); // 스냅샷

    try {
      const res = await gameAPI.swing(gameId, { swing: true, timing: true });
      inferAndSetMessage(res?.data?.message || "", res?.data?.data || null);
      setTimeout(fetchGameState, 100);
      // navigate는 useEffect(gameOver)에서 일괄 처리
    } catch (err) {
      console.error("스윙 실패:", err);
      setMessage("스윙 실패");
    }

    setSwingGauge(0);
    setCurrentType(null);
  };

  // 노스윙
  const handleNoSwing = async () => {
    clearGaugeInterval();
    setAnimating(false);
    setCurrentType(null);
    setSwingGauge(0);

    snapshotCounts(); // 스냅샷

    try {
      const res = await gameAPI.swing(gameId, { swing: false, timing: false });
      inferAndSetMessage(res?.data?.message || "", res?.data?.data || null);
      setTimeout(fetchGameState, 100);
    } catch (err) {
      console.error("노스윙 실패:", err);
      setMessage("노스윙 실패");
    }
  };

  // 투구 시작
  const handlePitch = () => {
    if (animating) return;
    setAnimating(true);
    setCurrentType("pitch");
    snapshotCounts(); // 스냅샷
    setMessage("투구 위치를 선택하세요");
  };

  if (!gameId) {
    return (
      <div style={{ padding: 20, textAlign: "center" }}>
        <h2>게임 정보를 불러오는 중...</h2>
        <p>잠시만 기다려주세요.</p>
      </div>
    );
  }

  return (
    <div style={{ padding: 20 }}>
      <MessageBox message={message} />

      <div style={{ display: "flex", gap: 24, alignItems: "flex-start" }}>
        <Scoreboard
          gameState={gameState}
          homeTeam={homeTeam}
          awayTeam={awayTeam}
          inningCount={inningCount || 9}
        />

        <div style={{ margin: 180, display: "flex", flexDirection: "column", alignItems: "center" }}>
          <div style={{ transform: "scale(1.8)" }}>
            <Bases bases={gameState.bases} />
          </div>
        </div>

        <div style={{ display: "flex", flexDirection: "column", alignItems: "center" }}>
          <PitchGauge value={pitchGauge} />
          <SwingGauge value={swingGauge} />

          <div style={{ marginTop: 10, display: "flex", gap: 10, flexWrap: "wrap", justifyContent: "center" }}>
            {/* 공격 */}
            <button onClick={startSwingGauge} disabled={!isUserOffenseNow || animating}>⚾ 타격 준비</button>
            <button onClick={handleSwing} disabled={!isUserOffenseNow || !animating || currentType !== "swing"}>🏏 스윙</button>
            <button onClick={handleNoSwing} disabled={!isUserOffenseNow}>❌ 노스윙</button>

            {/* 수비 */}
            <button onClick={handlePitch} disabled={isUserOffenseNow || animating}>🥎 투구</button>
          </div>

          <StrikeZoneContainer
            gameId={gameId}
            currentType={currentType}
            onPitchMessage={(rawMsg) => inferAndSetMessage(rawMsg, null)} // 메시지 확정
            onServerUpdate={fetchGameState}
            onActionComplete={() => {
              clearGaugeInterval();
              setAnimating(false);
              setCurrentType(null);
            }}
          />

          <Scoreboard22
            strike={gameState.strikes}
            ball={gameState.balls}
            out={gameState.outs}
            innings={{ home: gameState.homeByInning, away: gameState.awayByInning }}
            bases={gameState.bases}
          />
        </div>
      </div>
    </div>
  );
};

export default GamePage;
