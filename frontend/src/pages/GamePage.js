// GamePage.js
import React, { useState, useEffect, useRef, useMemo, useCallback } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { gameAPI } from "../api/api";
import Scoreboard from "./Scoreboard";
import Scoreboard22 from "./Scoreboard22";
import StrikeZoneContainer from "./StrikeZoneContainer";
import Bases from "./Bases";

import MessageBox from "./MessageBox";

import "../styles/GamePage.css";

const GamePage = () => {
  const { state } = useLocation();
  const navigate = useNavigate();

  // 로컬 스토리지에서 자동으로 불러오는 로직 제거
  const gameInfo = state || {};
  const { gameId, homeTeam, awayTeam, inningCount, userTeam } = gameInfo;

  // 페이지 로드 시, 전달된 state가 있으면 localStorage에 저장하는 로직 제거
  // useEffect(() => {
  //   if (state) localStorage.setItem("gameInfo", JSON.stringify(state));
  // }, [state]);

  useEffect(() => {
    if (!gameId) navigate("/game/setup");
  }, [gameId, navigate]);

  // ===== 게임 상태 =====
  const [gameState, setGameState] = useState({
    inning: 1,
    isTop: true,
    balls: 0,
    strikes: 0,
    outs: 0,
    bases: [false, false, false],
    basePlayers: [null, null, null],
    currentBatter: null,
    currentPitcher: null,
    offenseTeam: awayTeam || "원정팀",
    defenseTeam: homeTeam || "홈팀",
    homeScore: 0,
    awayScore: 0,
    homeHit: 0,
    awayHit: 0,
    homeWalks: 0,
    awayWalks: 0,
    inningCount: inningCount || 9,
    eventLog: [],
  });

  const gaugeInterval = useRef(null);

  // ===== 사용자 공격 여부 판단 =====
  const userIsHome = homeTeam === userTeam;
  const offenseIsTop =
    String(gameState.isTop ? "TOP" : "BOTTOM").toUpperCase() === "TOP";
  const isUserOffenseNow = offenseIsTop ? !userIsHome : userIsHome;

  // ===== 메시지 & 게이지 상태 =====
  const [message, setMessage] = useState("");

  const [swingGauge, setSwingGauge] = useState(0);
  const [animating, setAnimating] = useState(false);
  const [currentType, setCurrentType] = useState(null);
  const [selectedShot, setSelectedShot] = useState(null);

  // ===== 이벤트 로그 상태 =====
  const [showEventLog, setShowEventLog] = useState(false);
  const [selectedEvent, setSelectedEvent] = useState(null);

  const handleMessageBoxClick = () => {
    setShowEventLog(!showEventLog); // 클릭 시 토글
  };

  // ----- 서버 메시지 파싱 -----
  const parseServerMessage = (rawMsg = "") => {
    let msg = rawMsg || "";
    msg = msg.replace("스윙/노스윙 처리 완료:", "").trim();
    const [leftRaw, rightRaw] = msg
      .split("|")
      .map((s) => (s || "").trim());
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

  // ----- 볼넷/삼진 판정용 이전 카운트 저장 -----
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

  // ----- 메시지 결정 -----
  const inferAndSetMessage = (rawMsg = "", resData = null) => {
    const prev = countsBeforeActionRef.current;
    const parsed = parseServerMessage(rawMsg);

    if (/볼넷|4구/u.test(rawMsg)) return setMessage("볼넷");
    if (parsed === "볼") {
      const newBall = (prev.balls ?? 0) + 1;
      if (newBall >= 4) return setMessage("볼넷");
      return setMessage(`${newBall}볼`);
    }
    if (parsed === "스트라이크") {
      const newStrike = (prev.strikes ?? 0) + 1;
      if (newStrike >= 3) return setMessage("삼진 아웃");
      return setMessage(`${newStrike}스트라이크`);
    }
    setMessage(parsed || "타석 결과");
  };

  const intervalRef = useRef(null);

  // ===== 게임 상태 fetch =====
  const fetchGameState = useCallback(async () => {
    if (!gameId) return;
    try {
      const res = await gameAPI.getGameView(gameId);
      const data = res.data.data;
      const basesArray = data.bases
        ? data.bases.map((b) => b !== null)
        : [false, false, false];

      setGameState((prev) => ({
        ...prev,
        inning: data.inning,
        isTop: data.offenseSide === "TOP",
        balls: data.ball,
        strikes: data.strike,
        outs: data.out,
        bases: basesArray,
        basePlayers: data.bases || [null, null, null],
        currentBatter: data.currentBatter,
        currentPitcher: data.currentPitcher,
        offenseTeam: data.offenseTeam,
        defenseTeam: data.defenseTeam,
        homeScore: data.homeScore,
        awayScore: data.awayScore,
        homeHit: data.homeHit,
        awayHit: data.awayHit,
        homeWalks: data.homeWalks,
        awayWalks: data.awayWalks,
        eventLog: data.eventLog || [],
      }));
    } catch (err) {
      console.error("게임 상태 로딩 실패:", err);
      if (err.response && err.response.status === 404) {
        clearInterval(intervalRef.current); // 반복 중단
        alert("종료되었거나 유효하지 않은 게임입니다. 메인 화면으로 이동합니다.");
        navigate('/'); // 메인으로 이동
      }
    }
  }, [gameId, navigate]);

  useEffect(() => {
    if (!gameId) return;
    fetchGameState();
    intervalRef.current = setInterval(fetchGameState, 5000);
    return () => clearInterval(intervalRef.current);
  }, [gameId, navigate, fetchGameState]);

  // ===== 이닝별 점수 계산 =====
  const inningScores = useMemo(() => {
    const myScores = Array(gameState.inningCount).fill(0);
    const opponentScores = Array(gameState.inningCount).fill(0);

    gameState.eventLog.forEach((event) => {
      const idx = event.inning - 1;
      if (event.team === homeTeam) myScores[idx] += event.run || 0;
      else if (event.team === awayTeam) opponentScores[idx] += event.run || 0;
    });

    return { my: myScores, opponent: opponentScores };
  }, [gameState.eventLog, gameState.inningCount, homeTeam, awayTeam]);

  // ===== GAME_END 이벤트 감지 =====
  useEffect(() => {
    if (!gameState.eventLog || gameState.eventLog.length === 0) return;

    const gameEndEvent = gameState.eventLog.find((e) => e.type === "GAME_END");
    if (gameEndEvent) {
      navigate("/game/result", {
        state: {
          homeTeam: gameState.homeTeam || homeTeam,
          awayTeam: gameState.awayTeam || awayTeam,
          gameState: { ...gameState, score: inningScores },
        },
      });
    }
  }, [gameState.eventLog, navigate, gameState, inningScores, homeTeam, awayTeam]);

  // ===== 타격/투구 관련 =====
  const startSwingGauge = () => {
    setAnimating(true);
    setCurrentType("swing");
    let val = 0;
    gaugeInterval.current = setInterval(() => {
      val += 2;
      if (val > 100) val = 0;
      setSwingGauge(val);
    }, 20);
  };

  const handleSwing = async () => {
    clearInterval(gaugeInterval.current);
    setAnimating(false);
    setCurrentType(null);
    setSwingGauge(0);
    try {
      snapshotCounts();
      const res = await gameAPI.swing(gameId, { swing: true, timing: true });
      inferAndSetMessage(res.data.message, res.data.data);
      await fetchGameState();
    } catch (err) {
      console.error(err);
      setMessage("스윙 실패");
    }
  };

  const handleNoSwing = async () => {
    clearInterval(gaugeInterval.current);
    setAnimating(false);
    setCurrentType(null);
    setSwingGauge(0);
    try {
      snapshotCounts();
      const res = await gameAPI.swing(gameId, { swing: false, timing: false });
      inferAndSetMessage(res.data.message, res.data.data);
      await fetchGameState();
    } catch (err) {
      console.error(err);
      setMessage("노스윙 실패");
    }
  };

  if (!gameId)
    return (
      <div style={{ padding: 20, textAlign: "center" }}>
        <h2>게임 정보를 불러오는 중...</h2>
      </div>
    );

  return (
    <div className="game-container">
      <div className="game-content">
        <div className="game-left-section">
          <Scoreboard
            gameState={{ ...gameState, score: inningScores }}
            homeTeam={homeTeam}
            awayTeam={awayTeam}
            lineups={{}}
            inningCount={gameState.inningCount}
          />
          <Scoreboard22
            strike={gameState.strikes}
            ball={gameState.balls}
            out={gameState.outs}
            innings={inningScores}
            bases={gameState.bases}
          />
        </div>

        <div className="game-center-section">
          <div className="bases-container">
            <Bases bases={gameState.bases} basePlayers={gameState.basePlayers} />
          </div>
        </div>

        <div className="game-right-section">
          <div onClick={handleMessageBoxClick}>
            <MessageBox message={message} />
          </div>

          {/* 이벤트 로그 목록 */}
          {showEventLog && (
            <div
              style={{
                marginTop: "10px",
                maxHeight: "200px",
                overflowY: "auto",
                border: "1px solid #4a69bd",
                padding: "10px",
                background: "#f0f4ff",
                minWidth: "200px",
              }}
            >
              {gameState.eventLog.map((event, idx) => (
                <div
                  key={idx}
                  onClick={() => setSelectedEvent(event)}
                  style={{
                    padding: "5px",
                    marginBottom: "5px",
                    cursor: "pointer",
                    background: selectedEvent === event ? "#d0d8ff" : "transparent",
                  }}
                >
                  {event.inning}회 {event.offenseTeam} - {event.batter} ({event.result})
                </div>
              ))}
            </div>
          )}


          <div className={`game-status ${isUserOffenseNow ? "offense" : "defense"}`}>
            <p className="game-status-text">
              {isUserOffenseNow ? "⚾ 공격 중" : "🥎 수비 중"}
            </p>
            <p className="game-status-info">
              {gameState.inning}회 {gameState.isTop ? "초" : "말"} |{" "}
              {gameState.offenseTeam} vs {gameState.defenseTeam}
            </p>
          </div>


          <div className="game-controls">
            {isUserOffenseNow && (
              <div className="game-controls-container">
          <div className="gauge-container">
            <div className="gauge-label">타격 게이지</div>
            <div className="gauge-bar">
              <div className="gauge-fill swing" style={{ width: `${swingGauge}%` }} />
            </div>
          </div>
                <button
                  className="game-button swing"
                  onClick={startSwingGauge}
                  disabled={animating}
                >
                  ⚾ 타격 준비
                </button>
                <button
                  className="game-button swing"
                  onClick={handleSwing}
                  disabled={!animating || currentType !== "swing"}
                >
                  🏏 스윙
                </button>
                <button className="game-button no-swing" onClick={handleNoSwing}>
                  ❌ 노스윙
                </button>
              </div>
            )}

            {!isUserOffenseNow && (
              <div className="game-controls-container">
                <div className="strike-zone-container">
                  <StrikeZoneContainer
                    selectedShot={selectedShot}
                    setSelectedShot={setSelectedShot}
                  />
                </div>
                
                <button
                  className="game-button pitch"
                  onClick={async () => {
                    if (!selectedShot) {
                      setMessage("스트라이크존을 선택하세요!");
                      return;
                    }
                    snapshotCounts();
                    const type = selectedShot.color === "blue" ? "strike" : "ball";
                    const res = await gameAPI.pitch(gameId, {
                      type,
                      pitchType: type,
                      zoneColor: selectedShot.color,
                    });
                    inferAndSetMessage(res.data.message, res.data.data);
                    setSelectedShot(null);
                    await fetchGameState();
                  }}
                >
                  🥎 투구
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default GamePage;
