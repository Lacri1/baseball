import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { gameAPI } from "../api/api";

const TeamSetup = ({ gameId, homeTeam, awayTeam }) => {
  const navigate = useNavigate();

  const [homeLineup] = useState([]);
  const [awayLineup] = useState([]);
  const [homePitcher] = useState("");
  const [awayPitcher] = useState("");

  const handleSubmit = async () => {
    try {
      await gameAPI.setLineup(gameId, {
        homeLineup,
        awayLineup,
        homePitcher,
        awayPitcher,
      });
      // 라인업 등록 성공 → GamePage 이동
      navigate("/game/play", { state: { gameId, homeTeam, awayTeam } });
    } catch (err) {
      console.error("라인업 등록 실패:", err);
      alert("라인업 등록 실패 😢 다시 시도해주세요.");
    }
  };

  return (
    <div>
      <h2>라인업 설정</h2>
      {/* 여기에 select 박스나 input으로 선수 선택 UI 만들기 */}
      <button onClick={handleSubmit}>라인업 확정</button>
    </div>
  );
};

export default TeamSetup;
