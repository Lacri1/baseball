import React, { useState, useEffect } from 'react';
import api from '../api'; // 실제 API 경로로 수정

const Scoreboard = ({ gameState }) => {
  const [homePitcher, setHomePitcher] = useState("");
  const [homeBatter, setHomeBatter] = useState("");
  const [awayPitcher, setAwayPitcher] = useState("");
  const [awayBatter, setAwayBatter] = useState("");

  useEffect(() => {
    async function fetchNames() {
      try {
        const res = await api.get("/game/names"); // 예시 API
        setHomePitcher(res.data.homePitcher);
        setHomeBatter(res.data.homeBatter);
        setAwayPitcher(res.data.awayPitcher);
        setAwayBatter(res.data.awayBatter);
      } catch (err) {
        console.error(err);
      }
    }
    fetchNames();
  }, []);

  if (!gameState || !gameState.score) {
    return <div>Loading...</div>;
  }
  const { score, inning, isTop } = gameState;
  

  return (
    <div>
      <table border="1" cellPadding="10" style={{ width: '100%' }}>
        <thead>
          <tr>
            <th>이닝</th>
            {score.my.map((_, i) => <th key={i}>{i + 1}</th>)}
            <th>합계</th>
          </tr>
        </thead>
        <tbody>
          {/* 홈팀 점수 */}
          <tr>
            <td>홈 팀</td>
            {score.my.map((s,i) => <td key={i}>{s}</td>)}
            <td>{score.my.reduce((a,b)=>a+b,0)}</td>
          </tr>

          {/* 원정팀 점수 */}
          <tr>
            <td>원정 팀</td>
            {score.opponent.map((s,i) => <td key={i}>{s}</td>)}
            <td>{score.opponent.reduce((a,b)=>a+b,0)}</td>
          </tr>
          {/* 홈팀 투수/타자 */}
          <tr>
            <td colSpan={score.my.length + 2}>
              홈팀 = {isTop ? `투수: ${homePitcher}` : `타자: ${homeBatter}`}
            </td>
          </tr>
          {/* 원정팀 투수/타자 */}
          <tr>
            <td colSpan={score.my.length + 2}>
              원정팀 = {isTop ?  `타자: ${awayBatter}`:`투수: ${awayPitcher}`}
            </td>
          </tr>

          {/* 이닝 표시 */}
          <tr>
            <td colSpan={score.my.length + 2}>
              이닝: {inning}회 {isTop ? '초' : '말'} 
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  );
};

export default Scoreboard;
