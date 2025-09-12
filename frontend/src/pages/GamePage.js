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
  const { gameId, userTeam, homeTeam, awayTeam, inningCount } =
    state || savedGameInfo || {};

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

      const nextIsTop =
        typeof data.isTop === "boolean"
          ? data.isTop
          : String(data.offenseSide || "TOP").toUpperCase() === "TOP";

      setGameState((prev) => ({
        ...prev,
        inning: data.inning,
        isTop: nextIsTop,
        balls: data.ball,
        strikes: data.strike,
        outs: data.out,
        bases: data.bases || [false, false, false],
        homeByInning: sb?.homeByInning ?? prev.homeByInning,
        awayByInning: sb?.awayByInning ?? prev.awayByInning,
        homeScoreTotal:
          typeof sb?.homeScore === "number"
            ? sb.homeScore
            : prev.homeScoreTotal,
        awayScoreTotal:
          typeof sb?.awayScore === "number"
            ? sb.awayScore
            : prev.awayScoreTotal,
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
        offenseSide: data.offenseSide || prev.offenseSide || "TOP",
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

  // ----- 메시지 파서 -----
  const parseServerMessage = (rawMsg = "") => {
    let msg = rawMsg || "";
    msg = msg.replace("스윙/노스윙 처리 완료:", "").trim();
    const [leftRaw, rightRaw] = msg.split("|").map((s) => (s || "").trim());
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
    return msg || "타석 결과";
  };

  // ----- 볼넷/삼진 판정 -----
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

  const inferAndSetMessage = (rawMsg = "", resData = null) => {
    const prev = countsBeforeActionRef.current;
    const parsed = parseServerMessage(rawMsg);

    if (/볼넷|4구/u.test(rawMsg)) return setMessage("볼넷");
    if (parsed === "볼" && prev.balls >= 3) return setMessage("볼넷");
    if (
      resData &&
      ((typeof resData.homeWalks === "number" &&
        resData.homeWalks > prev.homeWalks) ||
        (typeof resData.awayWalks === "number" &&
          resData.awayWalks > prev.awayWalks))
    )
      return setMessage("볼넷");
    if (parsed === "스트라이크" && prev.strikes >= 2)
      return setMessage("삼진 아웃");

    setMessage(parsed || "타석 결과");
  };

  // ===== 메시지 지연 표시(이닝 전환 안내가 결과를 덮어쓰지 않도록) =====
  const messageTimerRef = useRef(null);

  const isResultMessage = (msg = "") =>
    /(안타|홈런|2루타|3루타|희생플라이|땅볼 아웃|뜬공 아웃|직선타|병살|삼진|볼넷|사구|득점|실책|주자 아웃)/u.test(
      String(msg)
    );

  const setMessageLater = (text, delayMs = 1200) => {
    if (messageTimerRef.current) {
      clearTimeout(messageTimerRef.current);
      messageTimerRef.current = null;
    }
    messageTimerRef.current = setTimeout(() => {
      setMessage(text);
      messageTimerRef.current = null;
    }, delayMs);
  };

  useEffect(() => {
    return () => {
      if (messageTimerRef.current) clearTimeout(messageTimerRef.current);
    };
  }, []);
  // ===================================================================

  // ----- 공수 전환 -----
  const prevTurnRef = useRef(null);
  const userIsHome = homeTeam === userTeam;
  const offenseIsTop =
    String(gameState.offenseSide || "TOP").toUpperCase() === "TOP";
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

      const nextTurnMsg = isUserOffenseNow
        ? "공격 턴입니다. ‘타격 준비’ 후 스윙하세요."
        : "수비 턴입니다. ‘투구’ 후 존을 클릭하세요.";

      // 직전 메시지가 결과성이라면 1.2초 유지 후 교체, 아니면 0.5초 지연 후 교체
      if (isResultMessage(message)) {
        setMessageLater(nextTurnMsg, 1200);
      } else {
        setMessageLater(nextTurnMsg, 500);
      }
    }
  }, [gameState.inning, gameState.offenseSide, isUserOffenseNow, message]);

  // ----- 게임 종료 이동 -----
  useEffect(() => {
    if (gameState.gameOver) {
      navigate("/game/result", {
        state: {
          gameState,
          homeTeam,
          awayTeam,
          winner: gameState.winner,
          userTeam,
        },
      });
    }
  }, [gameState.gameOver, gameState, homeTeam, awayTeam, navigate, userTeam]);

  // 게이지/액션
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

  const handleSwing = async () => {
    if (!animating || currentType !== "swing") return;
    clearGaugeInterval();
    setAnimating(false);
    snapshotCounts();
    try {
      const res = await gameAPI.swing(gameId, { swing: true, timing: true });
      inferAndSetMessage(res?.data?.message || "", res?.data?.data || null);
      setTimeout(fetchGameState, 100);
    } catch {
      setMessage("스윙 실패");
    }
    setSwingGauge(0);
    setCurrentType(null);
  };

  const handleNoSwing = async () => {
    clearGaugeInterval();
    setAnimating(false);
    setCurrentType(null);
    setSwingGauge(0);
    snapshotCounts();
    try {
      const res = await gameAPI.swing(gameId, { swing: false, timing: false });
      inferAndSetMessage(res?.data?.message || "", res?.data?.data || null);
      setTimeout(fetchGameState, 100);
    } catch {
      setMessage("노스윙 실패");
    }
  };

  const handlePitch = () => {
    if (animating) return;
    setAnimating(true);
    setCurrentType("pitch");
    snapshotCounts();
    setMessage("투구 위치를 선택하세요");
  };

  // ----- 렌더 -----
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

        <div style={{ margin: 180, display: "flex", flexDirection: "column" }}>
          <div style={{ transform: "scale(1.8)" }}>
            <Bases bases={gameState.bases} />
          </div>
        </div>

        <div style={{ display: "flex", flexDirection: "column", alignItems: "center" }}>
          <PitchGauge value={pitchGauge} />
          <SwingGauge value={swingGauge} />

          <div style={{ marginTop: 10, display: "flex", gap: 10 }}>
            {/* 공격 */}
            <button onClick={startSwingGauge} disabled={!isUserOffenseNow || animating}>
              ⚾ 타격 준비
            </button>
            <button onClick={handleSwing} disabled={!isUserOffenseNow || !animating || currentType !== "swing"}>
              🏏 스윙
            </button>
            <button onClick={handleNoSwing} disabled={!isUserOffenseNow}>
              ❌ 노스윙
            </button>

            {/* 수비 */}
            <button onClick={handlePitch} disabled={isUserOffenseNow || animating}>
              🥎 투구
            </button>
          </div>

          <StrikeZoneContainer
            gameId={gameId}
            currentType={currentType}
            onPitchMessage={(rawMsg) => inferAndSetMessage(rawMsg, null)}
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
