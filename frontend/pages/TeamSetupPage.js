import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api';

const TeamSetupPage = () => {
  const navigate = useNavigate();
  const [inningCount, setInningCount] = useState(9);
  const [teams, setTeams] = useState([]); // API로부터 팀 목록을 받아올 상태
  const [userTeam, setUserTeam] = useState('');

  // 컴포넌트가 마운트될 때 팀 목록을 불러옵니다.
  useEffect(() => {
    const fetchTeams = async () => {
      try {
        const response = await api.get('/api/team/list');
        setTeams(response.data);
        if (response.data.length > 0) {
          setUserTeam(response.data[0]); // 기본으로 첫 번째 팀을 선택
        }
      } catch (error) {
        console.error("팀 목록을 불러오는 데 실패했습니다:", error);
      }
    };

    fetchTeams();
  }, []); // 빈 배열을 전달하여 컴포넌트가 처음 렌더링될 때 한 번만 실행

  const handleStart = async () => {
    if (!userTeam) {
      alert("팀을 선택해주세요.");
      return;
    }

    // 사용자가 선택하지 않은 팀 목록에서 랜덤으로 상대팀 선택
    const opponentTeams = teams.filter(team => team !== userTeam);
    if (opponentTeams.length === 0) {
      alert("상대할 팀이 없습니다. 팀 목록을 확인해주세요.");
      return;
    }
    const awayTeam = opponentTeams[Math.floor(Math.random() * opponentTeams.length)];

    try {
      const gameData = {
        homeTeam: userTeam,       // 사용자가 선택한 팀
        awayTeam: awayTeam,         // 랜덤으로 선택된 상대팀
        maxInning: inningCount,
        isUserOffense: true,      // 사용자가 공격팀으로 시작
        userId: 'temp-user'       // TODO: 실제 사용자 ID로 교체
      };
      const response = await api.post('/api/baseball/game', gameData);
      const game = response.data.data; 
      console.log('게임 생성:', game);
      navigate(`/game/${game.gameId}/play`);
    } catch (err) {
      console.error('게임 초기화 실패', err);
      alert(`게임 생성에 실패했습니다: ${err.response?.data?.message || err.message}`);
    }
  };

  return (
    <div style={{ padding: 20 }}>
      <h2>팀 선택 & 이닝 설정</h2>
      <label>
        총 이닝 수:
        <select value={inningCount} onChange={(e) => setInningCount(Number(e.target.value))}>
          {Array.from({ length: 9 }, (_, i) => i + 1).map(num => (
            <option key={num} value={num}>{num} 이닝</option>
          ))}
        </select>
      </label>
      <div style={{ marginTop: 10 }}>
        <label>
          팀 선택:
          <select value={userTeam} onChange={(e) => setUserTeam(e.target.value)}>
            <option value="">팀을 선택하세요</option>
            {teams.map(teamName => <option key={teamName} value={teamName}>{teamName}</option>)}
          </select>
        </label>
      </div>
      <button onClick={handleStart} style={{ marginTop: 20 }} disabled={!userTeam}>게임 시작</button>
    </div>
  );
};

export default TeamSetupPage;
