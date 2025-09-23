// src/pages/TeamSetupPage.js
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { DragDropContext, Droppable, Draggable } from '@hello-pangea/dnd';
import { gameAPI, playerAPI } from '../api/api';

const teams = ['두산', 'LG', 'SSG', '키움', '한화', '롯데', '삼성', 'KT', 'KIA', 'NC'];

// 선수 데이터 정규화
const normalizeRoster = (data, teamName) => {
  const defaultBatters = Array.from({ length: 12 }, (_, i) => ({
    Player_ID: 1000 + i,
    Player_Name: `${teamName} 타자${i + 1}`,
  }));
  const defaultPitchers = Array.from({ length: 5 }, (_, i) => ({
    Player_ID: 2000 + i,
    Player_Name: `${teamName} 투수${i + 1}`,
  }));

  if (!data || !data.batters || !data.pitchers) {
    return { batters: defaultBatters, pitchers: defaultPitchers };
  }

  const batters = data.batters.map((b, i) => ({
    Player_ID: b.Player_ID || i,
    Player_Name: b.Player_Name || b.name || `타자${i + 1}`,
    ...b,
  }));

  const pitchers = data.pitchers.map((p, i) => ({
    Player_ID: p.Player_ID || i + 100,
    Player_Name: p.Player_Name || p.name || `투수${i + 1}`,
    ...p,
  }));

  return { batters, pitchers };
};

const TeamSetupPage = () => {
  const navigate = useNavigate();
  const [inningCount, setInningCount] = useState(9);
  const [userTeam, setUserTeam] = useState('');
  const [cpuTeam, setCpuTeam] = useState('');
  const [userBatters, setUserBatters] = useState([]);
  const [userPitchers, setUserPitchers] = useState([]);
  const [cpuBatters, setCpuBatters] = useState([]);
  const [cpuPitchers, setCpuPitchers] = useState([]);
  const [userBattingOrder, setUserBattingOrder] = useState([]);
  const [selectedUserPitcher, setSelectedUserPitcher] = useState(null);
  const [isUserOffense, setIsUserOffense] = useState(true);
  const [loading, setLoading] = useState(false);

  // CPU 팀 자동 선택
  useEffect(() => {
    if (userTeam) {
      const available = teams.filter(t => t !== userTeam);
      setCpuTeam(available[Math.floor(Math.random() * available.length)]);
    } else {
      setCpuTeam('');
    }
  }, [userTeam]);

  // 선수 불러오기
  useEffect(() => {
    const fetchPlayers = async (team, setBatters, setPitchers) => {
      if (!team) return;
      try {
        const res = await playerAPI.getTeamRoster(team);
        const { batters, pitchers } = normalizeRoster(res.data, team);
        setBatters(batters);
        setPitchers(pitchers);
      } catch (err) {
        console.error('선수 정보 로딩 실패', err);
        const { batters, pitchers } = normalizeRoster(null, team);
        setBatters(batters);
        setPitchers(pitchers);
      }
    };

    fetchPlayers(userTeam, setUserBatters, setUserPitchers);
    fetchPlayers(cpuTeam, setCpuBatters, setCpuPitchers);
  }, [userTeam, cpuTeam]);

  // 초기 타순 설정
  useEffect(() => {
    if (userBatters.length >= 9) setUserBattingOrder(userBatters.slice(0, 9));
    else setUserBattingOrder(userBatters);
  }, [userBatters]);

  // 투수 자동 선택
  useEffect(() => {
    if (!selectedUserPitcher && userPitchers.length > 0) setSelectedUserPitcher(userPitchers[0]);
  }, [userPitchers, selectedUserPitcher]);

  // 타순 드래그
  const handleDragEnd = (result) => {
    if (!result.destination) return;
    const newOrder = Array.from(userBattingOrder);
    const [moved] = newOrder.splice(result.source.index, 1);
    newOrder.splice(result.destination.index, 0, moved);
    setUserBattingOrder(newOrder);
  };

  // 게임 시작
  const handleStart = async () => {
    if (!userTeam) return alert('사용자 팀을 선택하세요.');
    if (!cpuTeam) return alert('CPU 팀이 아직 선택되지 않았습니다.');
    if (!selectedUserPitcher) return alert('사용자 팀 투수를 선택하세요.');
    if (userBattingOrder.length !== 9) return alert('사용자 팀 타순을 모두 선택하세요.');

    try {
      setLoading(true);

      const payload = {
        homeTeam: isUserOffense ? cpuTeam : userTeam,
        awayTeam: isUserOffense ? userTeam : cpuTeam,
        maxInning: inningCount,
        isUserOffense,
        userId: localStorage.getItem("userId") || "guest",
      };

      const res = await gameAPI.createGame(payload);
      const gameId = res.data?.data?.gameId;
      if (!gameId) throw new Error('게임 ID를 서버에서 가져오지 못했습니다.');

      // 사용자/CPU 라인업 준비
      const userLineup = {
        teamName: userTeam,
        battingOrder: userBattingOrder.map(p => p.Player_Name),
        startingPitcher: selectedUserPitcher.Player_Name,
        battingOrderIds: userBattingOrder.map(p => p.Player_ID),
        pitcherId: selectedUserPitcher.Player_ID,
        pitcherName: selectedUserPitcher.Player_Name
      };

      const cpuOrder = cpuBatters.slice(0, 9);
      const cpuPitcher = cpuPitchers[0];
      const cpuLineup = {
        teamName: cpuTeam,
        battingOrder: cpuOrder.map(p => p.Player_Name),
        startingPitcher: cpuPitcher?.Player_Name || '투수1',
        battingOrderIds: cpuOrder.map(p => p.Player_ID),
        pitcherId: cpuPitcher?.Player_ID,
        pitcherName: cpuPitcher?.Player_Name || '투수1'
      };

      const homeLineup = payload.homeTeam === userTeam ? userLineup : cpuLineup;
      const awayLineup = payload.awayTeam === userTeam ? userLineup : cpuLineup;

      // 사용자 팀만 서버에 등록
      const userIsHome = payload.homeTeam === userTeam;
      const userTeamLineup = userIsHome ? homeLineup : awayLineup;
      await gameAPI.setLineup(gameId, userTeamLineup);

      navigate('/game/play', {
        state: {
          gameId,
          homeTeam: payload.homeTeam,
          awayTeam: payload.awayTeam,
          inningCount,
          userTeam,
          isUserOffense,
          lineups: { home: homeLineup, away: awayLineup }
        }
      });

    } catch (err) {
      console.error('게임 시작 실패', err);
      alert('게임 시작 실패: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ padding: 20 }}>
      <h2>팀 설정 & 이닝 선택</h2>

      <div>
        <label>
          총 이닝:
          <select value={inningCount} onChange={e => setInningCount(Number(e.target.value))}>
            {Array.from({ length: 7 }, (_, i) => i + 3).map(n => (
              <option key={n} value={n}>{n} 이닝</option>
            ))}
          </select>
        </label>
      </div>

      <div style={{ marginTop: 10 }}>
        <label>
          사용자 팀:
          <select value={userTeam} onChange={e => setUserTeam(e.target.value)}>
            <option value="">선택</option>
            {teams.map(t => <option key={t} value={t}>{t}</option>)}
          </select>
        </label>
      </div>

      {/* 사용자 타자 선택 & 드래그 */}
      {userTeam && userBatters.length > 0 && (
        <div style={{ marginTop: 10 }}>
          <h4>사용자 팀 선수 목록 ({userTeam})</h4>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: 6, marginBottom: 10 }}>
            {userBatters.map(player => {
              const isSelected = userBattingOrder.find(p => p.Player_ID === player.Player_ID);
              return (
                <button
                  key={player.Player_ID}
                  onClick={() => {
                    if (isSelected) {
                      setUserBattingOrder(userBattingOrder.filter(p => p.Player_ID !== player.Player_ID));
                    } else {
                      if (userBattingOrder.length < 9) {
                        setUserBattingOrder([...userBattingOrder, player]);
                      } else {
                        alert('타순은 최대 9명까지 선택 가능합니다.');
                      }
                    }
                  }}
                  style={{
                    padding: '4px 8px',
                    border: isSelected ? '2px solid #007bff' : '1px solid #ccc',
                    borderRadius: 4,
                    backgroundColor: isSelected ? '#e0f0ff' : '#f9f9f9',
                    cursor: 'pointer'
                  }}
                >
                  {player.Player_Name}
                </button>
              );
            })}
          </div>

          {userBattingOrder.length > 0 && (
            <div style={{ marginTop: 10 }}>
              <h4>타순 선택 (드래그 가능, 최대 9명)</h4>
              <DragDropContext onDragEnd={handleDragEnd}>
                <Droppable droppableId="battingOrder">
                  {(provided) => (
                    <ul {...provided.droppableProps} ref={provided.innerRef} style={{ padding: 0, listStyle: 'none' }}>
                      {userBattingOrder.map((player, index) => (
                        <Draggable key={player.Player_ID} draggableId={String(player.Player_ID)} index={index}>
                          {(provided) => (
                            <li
                              ref={provided.innerRef}
                              {...provided.draggableProps}
                              {...provided.dragHandleProps}
                              style={{
                                padding: 8,
                                margin: '0 0 8px 0',
                                border: '1px solid #ccc',
                                borderRadius: 4,
                                backgroundColor: '#f9f9f9',
                                cursor: 'grab',
                                ...provided.draggableProps.style
                              }}
                            >
                              {index + 1}번: {player.Player_Name}
                            </li>
                          )}
                        </Draggable>
                      ))}
                      {provided.placeholder}
                    </ul>
                  )}
                </Droppable>
              </DragDropContext>
            </div>
          )}
        </div>
      )}

      {/* 사용자 투수 선택 */}
      {userPitchers.length > 0 && (
        <div style={{ marginTop: 10 }}>
          <h4>투수 선택</h4>
          <select
            value={selectedUserPitcher?.Player_Name || ""}
            onChange={e => {
              const selected = userPitchers.find(p => p.Player_Name === e.target.value);
              setSelectedUserPitcher(selected);
            }}
          >
            <option value="">선택</option>
            {userPitchers.map(p => (
              <option key={p.Player_ID} value={p.Player_Name}>{p.Player_Name || `투수${p.Player_ID}`}</option>
            ))}
          </select>
        </div>
      )}

      {/* CPU 라인업 표시 */}
      {cpuTeam && cpuBatters.length > 0 && cpuPitchers.length > 0 && (
        <div style={{ marginTop: 20, borderTop: '1px solid #ccc', paddingTop: 10 }}>
          <h4>CPU 팀 라인업 ({cpuTeam})</h4>
          <ul style={{ padding: 0, listStyle: 'none' }}>
            {cpuBatters.slice(0, 9).map((player, index) => (
              <li key={player.Player_ID} style={{ padding: 4 }}>
                {index + 1}번: {player.Player_Name || `타자${index + 1}`}
              </li>
            ))}
          </ul>
          <p>선발 투수: {cpuPitchers[0]?.Player_Name || '투수1'}</p>
        </div>
      )}

      {/* 선공/후공 선택 */}
      <div style={{ marginTop: 10 }}>
        <label style={{ marginRight: 10 }}>선공/후공 선택:</label>
        <label style={{ marginRight: 10, fontWeight: isUserOffense ? 'bold' : 'normal' }}>
          <input type="radio" name="offense" checked={isUserOffense} onChange={() => setIsUserOffense(true)} />
          {userTeam || "사용자"} 선공
        </label>
        <label style={{ fontWeight: !isUserOffense ? 'bold' : 'normal' }}>
          <input type="radio" name="offense" checked={!isUserOffense} onChange={() => setIsUserOffense(false)} />
          {userTeam || "사용자"} 후공
        </label>
      </div>

      <button onClick={handleStart} style={{ marginTop: 20 }} disabled={loading}>
        {loading ? '게임 생성 중...' : '게임 시작'}
      </button>
    </div>
  );
};

export default TeamSetupPage;
