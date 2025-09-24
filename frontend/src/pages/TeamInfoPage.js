import React, { useState, useEffect } from 'react';
import axios from 'axios';

const teams = ['두산', 'LG', 'SSG', '키움', '한화', '롯데', '삼성', 'KT', 'KIA', 'NC'];

const TeamInfoPage = () => {
  const [selectedTeam, setSelectedTeam] = useState(teams[0]);
  const [teamData, setTeamData] = useState(null);

  useEffect(() => {
    const fetchTeamData = async () => {
      try {
        const res = await axios.get(`/api/team/${selectedTeam}`);
        setTeamData(res.data);
      } catch (err) {
        console.error(err);
      }
    };
    fetchTeamData();
  }, [selectedTeam]);

  if (!teamData) return <div>팀 데이터를 불러오는 중...</div>;

  return (
    <div style={{ padding: 20 }}>
      <h1>KBO 팀 정보 🏟️</h1>

      {/* 팀 선택 드롭다운 */}
      <label>
        팀 선택:&nbsp;
        <select
          value={selectedTeam}
          onChange={e => setSelectedTeam(e.target.value)}
          style={{ marginBottom: 20 }}
        >
          {teams.map(team => (
            <option key={team} value={team}>{team}</option>
          ))}
        </select>
      </label>

      {/* 타자 테이블 */}
      <h2>{teamData.teamId} 타자</h2>
      <table border="1" cellPadding="6" style={{ borderCollapse: 'collapse', marginBottom: 30 }}>
        <thead>
          <tr>
            <th>이름</th>
            <th>타수</th>
            <th>안타</th>
            <th>홈런</th>
            <th>타율</th>
          </tr>
        </thead>
        <tbody>
          {teamData.batters.map(b => (
            <tr key={b.name}>
              <td>{b.name}</td>
              <td>{b.atBats}</td>
              <td>{b.hits}</td>
              <td>{b.homeRuns}</td>
              <td>{b.battingAverage.toFixed(3)}</td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* 투수 테이블 */}
      <h2>{teamData.teamId} 투수</h2>
      <table border="1" cellPadding="6" style={{ borderCollapse: 'collapse' }}>
        <thead>
          <tr>
            <th>이름</th>
            <th>이닝</th>
            <th>삼진</th>
            <th>볼넷</th>
            <th>ERA</th>
          </tr>
        </thead>
        <tbody>
          {teamData.pitchers.map(p => (
            <tr key={p.name}>
              <td>{p.name}</td>
              <td>{p.inningsPitched}</td>
              <td>{p.strikeouts}</td>
              <td>{p.walks}</td>
              <td>{p.era.toFixed(2)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default TeamInfoPage;
