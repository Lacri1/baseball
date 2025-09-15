import React, { useState, useRef, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom"; // useParams 추가
import api from "../api";
import Scoreboard from "./Scoreboard";
import Bases from "./Bases";
import { PitchGauge, SwingGauge } from "./PitchGauge";
import MessageBox from "./MessageBox";
import Scoreboard22 from "./Scoreboard22";
import StrikeZoneContainer from "./StrikeZoneContainer";

const GamePage = () => {
  const { gameId } = useParams(); // URL에서 gameId 추출
  const navigate = useNavigate();

  // gameState를 null로 초기화하여 로딩 상태 관리
  const [gameState, setGameState] = useState(null);
  const [message, setMessage] = useState("게임을 불러오는 중...");

  // --- 기존 게이지 및 애니메이션 상태는 유지 ---
  const [pitchGauge, setPitchGauge] = useState(0);
  const [swingGauge, setSwingGauge] = useState(0);
  const [animating, setAnimating] = useState(false);
  const [currentType, setCurrentType] = useState(null);
  const gaugeInterval = useRef(null);

  // 게임 상태를 서버에서 불러오는 함수
  const fetchGameState = async () => {
    try {
      const res = await api.get(`/api/baseball/game/${gameId}`);
      setGameState(res.data.data); // ApiResponse 형식에 맞게 데이터 설정
      // 메시지는 액션 발생 시에만 갱신되도록 fetch에서는 메시지 업데이트 제외
    } catch (err) {
      console.error("게임 상태 불러오기 실패:", err);
      if (err.response && err.response.status === 404) {
        setMessage("게임을 찾을 수 없습니다. 새로운 게임을 시작해주세요.");
        setTimeout(() => navigate("/game/setup"), 3000);
      } else {
        setMessage("게임 상태를 불러오는 데 실패했습니다. 페이지를 새로고침해주세요.");
      }
    }
  };

  // 컴포넌트 마운트 시 및 2초마다 게임 상태 폴링
  useEffect(() => {
    fetchGameState(); // 초기 데이터 로드

    const interval = setInterval(fetchGameState, 2000); // 2초마다 폴링

    return () => clearInterval(interval); // 언마운트 시 인터벌 정리
  }, [gameId]); // gameId가 변경되면 useEffect 재실행

  // --- 기존 핸들러 함수들 (startGauge, stopGauge 등)은 거의 동일 ---
  // 자동 액션 처리 (투구와 타격 번갈아가며)
  const handleAutoAction = () => {
    if (animating) return;
    const nextType = currentType === "pitch" ? "swing" : "pitch";
    setMessage(`자동 ${nextType === "pitch" ? "투구" : "타격"} 진행 중...`);
    startGauge(nextType);
    setTimeout(() => {
      if (animating && currentType === nextType) {
        stopGauge();
      }
    }, 1000);
  };

  // 게이지 시작
  const startGauge = (type) => {
    // 사용자의 공격/수비 턴에 맞는 액션만 시작하도록 제한
    if (!gameState) return;
    if (type === 'swing' && gameState.offenseSide !== 'USER') {
        setMessage("사용자의 공격 턴이 아닙니다.");
        return;
    }
    if (type === 'pitch' && gameState.offenseSide === 'USER') {
        setMessage("사용자의 수비 턴이 아닙니다.");
        return;
    }

    if (!animating) {
      setCurrentType(type);
      setAnimating(true);
      let val = 0;
      gaugeInterval.current = setInterval(() => {
        val = Math.min(val + Math.floor(Math.random() * 6 + 1), 100);
        if (type === "pitch") setPitchGauge(val);
        else setSwingGauge(val);
        if (val >= 100) stopGauge();
      }, 20);
    } else if (currentType === type) stopGauge();
  };

  // 게이지 멈춤
  const stopGauge = async () => {
    if (!animating) return;
    clearInterval(gaugeInterval.current);
    setAnimating(false);
    
    const action = currentType;
    const requestBody = {
        pitchType: 'FASTBALL', // TODO: 실제 UI에서 선택된 값으로 변경 필요
        swing: true,           // TODO: 실제 UI에서 선택된 값으로 변경 필요
        timing: true           // TODO: 실제 UI에서 선택된 값으로 변경 필요
    };

    try {
        const res = await api.post(`/api/baseball/game/${gameId}/${action}`, requestBody);
        setGameState(res.data.data); // 응답 받은 최신 게임 상태로 업데이트
        setMessage(res.data.message || `${action === "pitch" ? "투구" : "타격"} 완료!`);
    } catch (err) {
        console.error(`${action} 액션 실패:`, err);
        setMessage(err.response?.data?.message || "액션 처리에 실패했습니다.");
    }

    setCurrentType(null);
  };

  // 로딩 중 UI
  if (!gameState) {
    return <div>{message}</div>;
  }

  // --- 렌더링 부분은 gameState를 사용하도록 통일 ---
  return (
    <div style={{ padding: 20 }}>
      <MessageBox message={message} />

      <div style={{ display: "flex", gap: 24, alignItems: "flex-start" }}>
        {/* 좌측 점수판 */}
        <div>
          <Scoreboard gameState={gameState} />
          <MessageBox 
            message={message} 
            style={{
              position: "fixed",
              bottom: 20,
              left: 20,
              width: 300,
              padding: 10,
              backgroundColor: "#fff",
              border: "1px solid #ccc",
              borderRadius: 8,
              boxShadow: "0 2px 6px rgba(0,0,0,0.2)",
              zIndex: 100
            }}
          />
        </div>

        {/* 가운데 Bases */}
        <div style={{ margin: 180, display: "flex", flexDirection: "column", alignItems: "center" }}>
          <div style={{ transform: "scale(1.8)" }}>
            <Bases bases={gameState.bases} />
          </div>
        </div>

        {/* 우측: StrikeZone + 게이지 + 버튼 + Scoreboard22 */}
        <div style={{ display: "flex", flexDirection: "column", alignItems: "center" }}>
          <StrikeZoneContainer
            gameState={gameState}
            setGameState={setGameState}
            currentType={currentType}
            gaugeValue={currentType === "pitch" ? pitchGauge : swingGauge}
            setMessage={setMessage}
            onAutoAction={handleAutoAction}
          />

          <PitchGauge value={pitchGauge} />
          <SwingGauge value={swingGauge} />

          <div style={{ marginTop: 10 }}>
            <button onClick={() => startGauge("pitch")}>
              {animating && currentType === "pitch" ? "던지기 클릭!" : "투구 시작"}
            </button>
            <button onClick={() => startGauge("swing")} style={{ marginLeft: 10 }}>
              {animating && currentType === "swing" ? "치기 클릭!" : "타격 시작"}
            </button>
          </div>

          <div>
            <Scoreboard22 strike={gameState.strike} ball={gameState.ball} out={gameState.out} />
            <div style={{ display: "flex", justifyContent: "flex-end", marginTop: 20 }}>
              <button onClick={() => navigate("/game/result", { state: { gameState } })}>
                결과 확인
              </button>
              <button onClick={() => navigate("/game/setup")} style={{ marginLeft: 10 }}>
                다시하기
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default GamePage;
