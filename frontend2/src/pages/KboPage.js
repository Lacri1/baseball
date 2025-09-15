import React, { useState, useEffect } from 'react';
import axios from 'axios';
import '../styles/kboPage.css';

const KboPage = () => {
  const [selectedTab, setSelectedTab] = useState('team');
  const [selectedHitter, setSelectedHitter] = useState('');
  const [top5Hitters, setTop5Hitters] = useState([]);

  // 타자와 투수 기록의 정렬 기준을 각각 관리할 상태 추가
  const [hitterSortBy, setHitterSortBy] = useState('battingAverage');
  const [pitcherSortBy, setPitcherSortBy] = useState('era');

  const [teamStats, setTeamStats] = useState([]);
  const [hitterStats, setHitterStats] = useState([]);
  const [pitcherStats, setPitcherStats] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let isMounted = true;
    const fetchData = async () => {
      setLoading(true);
      try {
        if (selectedTab === 'team') {
          // Assuming /kbo/team-stats still returns HTML or is handled separately
          const response = await axios.get('/api/kbo/team-stats');
          // You might need to keep parseTeamHtmlToArray if this endpoint still returns HTML
          if (isMounted) setTeamStats(response.data);
        } else if (selectedTab === 'hitter' && selectedHitter === '타자') {
          const response = await axios.get('/api/kbo/hitter-stats', {
            params: { sortBy: hitterSortBy }
          });
          if (isMounted) {
            setHitterStats(response.data);
            setTop5Hitters(response.data.slice(0, 5));
          }
        } else if (selectedTab === 'hitter' && selectedHitter === '투수') {
          const response = await axios.get('/api/kbo/pitcher-stats', {
            params: { sortBy: pitcherSortBy }
          });
          if (isMounted) setPitcherStats(response.data);
        }
        if (isMounted) setError(null);
      } catch (err) {
        if (isMounted) setError('데이터를 불러오는 데 실패했습니다.');
        console.error(err);
      } finally {
        if (isMounted) setLoading(false);
      }
    };
    fetchData();

    return () => {
      isMounted = false;
    };
  }, [selectedTab, selectedHitter, hitterSortBy, pitcherSortBy]); 

  const handleTabClick = (tab) => {
    setSelectedTab(tab);
    if (tab !== 'hitter') {
      setSelectedHitter('');
    } else {
      setSelectedHitter('타자');
    }
  };
  
  // 타자 기록 정렬 기준을 업데이트하는 함수
  const handleHitterSort = (key) => {
    setHitterSortBy(key);
  };

  // 투수 기록 정렬 기준을 업데이트하는 함수
  const handlePitcherSort = (key) => {
    setPitcherSortBy(key);
  };

  const renderTeamInfo = () => {
    if (loading) return <p>팀 순위 정보를 불러오는 중...</p>;
    if (error) return <p className="error">{error}</p>;

    return (

      <table className="record-table">
        <thead>
            <tr>
                <th>순위</th>
                <th>팀명</th>
                <th>경기 수</th>
                <th>승</th>
                <th>패</th>
                <th>무</th>
                <th>승률</th>
                <th>게임차</th>
            </tr>
        </thead>

        <tbody>
            {Array.isArray(teamStats) && teamStats.map((team, index) => (
                <tr key={index}>
                    <td>{index + 1}</td>
                    <td>{team.teamName}</td>
                    <td>{team.gameNum}</td>
                    <td>{team.win}</td>
                    <td>{team.lose}</td>
                    <td>{team.draw}</td>
                    <td>{team.winPercentage}</td>
                    <td>{team.gamesBehind}</td>
                </tr>
            ))}
        </tbody>
      </table>
    );
  };

  const renderHitterInfo = (hitter) => {
    if (loading) return <p>선수 기록을 불러오는 중...</p>;
    if (error) return <p className="error">{error}</p>;

    switch (hitter) {
      case '타자':
        return (
          <>
            <div className="top5-ranking">
              <h2>KBO 타자 TOP5 랭킹</h2>
              <table className="record-table">
                <thead>
                  <tr>
                    <th>순위</th>
                    <th>선수명</th>
                    <th>팀명</th>
                    <th>타율</th>
                  </tr>
                </thead>
                <tbody>
                  {top5Hitters.map((hitter, index) => (
                    <tr key={index}>
                      <td>{index + 1}</td>
                      <td>{hitter.name}</td>
                      <td>{hitter.team}</td>
                      <td>{hitter.battingAverage}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            <table className="record-table">
            <thead>
              <tr>
                {/* 각 th에 onClick 이벤트와 정렬 키 연결 */}
                <th>순위</th>
                <th>선수명</th>
                <th>팀명</th>
                <th><button onClick={() => handleHitterSort('battingAverage')} className="sort-btn">타율</button></th>
                <th><button onClick={() => handleHitterSort('gameNum')} className="sort-btn">경기 수</button></th>
                <th><button onClick={() => handleHitterSort('plateAppearances')} className="sort-btn">타석</button></th>
                <th><button onClick={() => handleHitterSort('run')} className="sort-btn">득점</button></th>
                <th><button onClick={() => handleHitterSort('hits')} className="sort-btn">안타</button></th>
                <th><button onClick={() => handleHitterSort('twoBases')} className="sort-btn">2루타</button></th>
                <th><button onClick={() => handleHitterSort('threeBases')} className="sort-btn">3루타</button></th>
                <th><button onClick={() => handleHitterSort('homeRun')} className="sort-btn">홈런</button></th>
                <th><button onClick={() => handleHitterSort('runsBattedIn')} className="sort-btn">타점</button></th>
                <th><button onClick={() => handleHitterSort('fourBall')} className="sort-btn">볼넷</button></th>
                <th><button onClick={() => handleHitterSort('strikeOut')} className="sort-btn">삼진</button></th>
                <th><button onClick={() => handleHitterSort('onBasePercentage')} className="sort-btn">출루율</button></th>
                <th><button onClick={() => handleHitterSort('onbasePlusSlug')} className="sort-btn">OPS</button></th>
                <th><button onClick={() => handleHitterSort('totalBases')} className="sort-btn">총루타</button></th>
                <th><button onClick={() => handleHitterSort('sacrificeBunts')} className="sort-btn">희생번트</button></th>
                <th><button onClick={() => handleHitterSort('sacrificeFly')} className="sort-btn">희생플라이</button></th>
                <th><button onClick={() => handleHitterSort('ibb')} className="sort-btn">고의사구</button></th>
                <th><button onClick={() => handleHitterSort('hitByPitch')} className="sort-btn">사구</button></th>
                <th><button onClick={() => handleHitterSort('doubleOut')} className="sort-btn">병살타</button></th>
                <th><button onClick={() => handleHitterSort('slugging')} className="sort-btn">장타율</button></th>
                <th><button onClick={() => handleHitterSort('multiHit')} className="sort-btn">멀티히트</button></th>
                <th><button onClick={() => handleHitterSort('scoringPositionAvg')} className="sort-btn">득점권타율</button></th>
                <th><button onClick={() => handleHitterSort('pinchHitAvg')} className="sort-btn">대타타율</button></th>
              </tr>
            </thead>
            <tbody>
              {Array.isArray(hitterStats) && hitterStats.map((hitter, index) => (
                <tr key={index}>
                  <td>{index + 1}</td>
                  <td>{hitter.name}</td>
                  <td>{hitter.team}</td>
                  <td>{hitter.battingAverage}</td>
                  <td>{hitter.gameNum}</td>
                  <td>{hitter.plateAppearances}</td>
                  <td>{hitter.run}</td>
                  <td>{hitter.hits}</td>
                  <td>{hitter.twoBases}</td>
                  <td>{hitter.threeBases}</td>
                  <td>{hitter.homeRun}</td>
                  <td>{hitter.runsBattedIn}</td>
                  <td>{hitter.fourBall}</td>
                  <td>{hitter.strikeOut}</td>
                  <td>{hitter.onBasePercentage}</td>
                  <td>{hitter.onbasePlusSlug}</td>
                  <td>{hitter.totalBases}</td>
                  <td>{hitter.sacrificeBunts}</td>
                  <td>{hitter.sacrificeFly}</td>
                  <td>{hitter.ibb}</td>
                  <td>{hitter.hitByPitch}</td>
                  <td>{hitter.doubleOut}</td>
                  <td>{hitter.slugging}</td>
                  <td>{hitter.multiHit}</td>
                  <td>{hitter.scoringPositionAvg}</td>
                  <td>{hitter.pinchHitAvg}</td>
                </tr>
              ))}
            </tbody>
          </table>
          </>
        );

      case '투수':
        return (
          <table className="record-table">
            <thead>
              <tr>
                {/* 각 th에 onClick 이벤트와 정렬 키 연결 */}
                <th>순위</th>
                <th>선수명</th>
                <th>팀명</th>
                <th><button onClick={() => handlePitcherSort('era')} className="sort-btn">평균 자책점</button></th>
                <th><button onClick={() => handlePitcherSort('gameNum')} className="sort-btn">경기 수</button></th>
                <th><button onClick={() => handlePitcherSort('win')} className="sort-btn">승</button></th>
                <th><button onClick={() => handlePitcherSort('lose')} className="sort-btn">패</button></th>
                <th><button onClick={() => handlePitcherSort('save')} className="sort-btn">세이브</button></th>
                <th><button onClick={() => handlePitcherSort('hold')} className-="sort-btn">홀드</button></th>
                <th><button onClick={() => handlePitcherSort('winningPercentage')} className="sort-btn">승률</button></th>
                <th><button onClick={() => handlePitcherSort('inningsPitched')} className="sort-btn">이닝</button></th>
                <th><button onClick={() => handlePitcherSort('hits')} className="sort-btn">피안타</button></th>
                <th><button onClick={() => handlePitcherSort('homeRun')} className="sort-btn">피홈런</button></th>
                <th><button onClick={() => handlePitcherSort('baseOnBalls')} className="sort-btn">볼넷</button></th>
                <th><button onClick={() => handlePitcherSort('hitByPitch')} className="sort-btn">사구</button></th>
                <th><button onClick={() => handlePitcherSort('strikeOut')} className="sort-btn">삼진</button></th>
                <th><button onClick={() => handlePitcherSort('runs')} className="sort-btn">실점</button></th>
                <th><button onClick={() => handlePitcherSort('earnedRun')} className="sort-btn">자책</button></th>
                <th><button onClick={() => handlePitcherSort('whip')} className="sort-btn">WHIP</button></th>
                <th><button onClick={() => handlePitcherSort('completeGame')} className="sort-btn">완투</button></th>
                <th><button onClick={() => handlePitcherSort('shutout')} className="sort-btn">완봉</button></th>
                <th><button onClick={() => handlePitcherSort('qualityStart')} className="sort-btn">QS</button></th>
                <th><button onClick={() => handlePitcherSort('blownSave')} className="sort-btn">블론세이브</button></th>
                <th><button onClick={() => handlePitcherSort('totalBattersFaced')} className="sort-btn">상대타자</button></th>
                <th><button onClick={() => handlePitcherSort('numberOfPitching')} className="sort-btn">투구수</button></th>
                <th><button onClick={() => handlePitcherSort('opponentBattingAverage')} className="sort-btn">피안타율</button></th>
                <th><button onClick={() => handlePitcherSort('twoBases')} className="sort-btn">2루타</button></th>
                <th><button onClick={() => handlePitcherSort('threeBases')} className="sort-btn">3루타</button></th>
                <th><button onClick={() => handlePitcherSort('sacrificeBunt')} className="sort-btn">희생번트</button></th>
                <th><button onClick={() => handlePitcherSort('sacrificeFly')} className="sort-btn">희생플라이</button></th>
                <th><button onClick={() => handlePitcherSort('ibb')} className="sort-btn">고의사구</button></th>
                <th><button onClick={() => handlePitcherSort('wildPitch')} className="sort-btn">폭투</button></th>
                <th><button onClick={() => handlePitcherSort('balk')} className="sort-btn">보크</button></th>
              </tr>
            </thead>
            <tbody>
              {Array.isArray(pitcherStats) && pitcherStats.map((pitcher, index) => (
                <tr key={index}>
                  <td>{index + 1}</td>
                  <td>{pitcher.name}</td>
                  <td>{pitcher.team}</td>
                  <td>{pitcher.era}</td>
                  <td>{pitcher.gameNum}</td>
                  <td>{pitcher.win}</td>
                  <td>{pitcher.lose}</td>
                  <td>{pitcher.save}</td>
                  <td>{pitcher.hold}</td>
                  <td>{pitcher.winningPercentage}</td>
                  <td>{pitcher.inningsPitched}</td>
                  <td>{pitcher.hits}</td>
                  <td>{pitcher.homeRun}</td>
                  <td>{pitcher.baseOnBalls}</td>
                  <td>{pitcher.hitByPitch}</td>
                  <td>{pitcher.strikeOut}</td>
                  <td>{pitcher.runs}</td>
                  <td>{pitcher.earnedRun}</td>
                  <td>{pitcher.whip}</td>
                  <td>{pitcher.completeGame}</td>
                  <td>{pitcher.shutout}</td>
                  <td>{pitcher.qualityStart}</td>
                  <td>{pitcher.blownSave}</td>
                  <td>{pitcher.totalBattersFaced}</td>
                  <td>{pitcher.numberOfPitching}</td>
                  <td>{pitcher.opponentBattingAverage}</td>
                  <td>{pitcher.twoBases}</td>
                  <td>{pitcher.threeBases}</td>
                  <td>{pitcher.sacrificeBunt}</td>
                  <td>{pitcher.sacrificeFly}</td>
                  <td>{pitcher.ibb}</td>
                  <td>{pitcher.wildPitch}</td>
                  <td>{pitcher.balk}</td>
                </tr>
              ))}
            </tbody>
          </table>
        );

      default:
        return null;
    }
  };

  return (
    <div className="about-container">
      <h1>KBO</h1>

      <div className="player-record-container">

        <div className="record-buttons">
          <button className="team-record" onClick={() => handleTabClick('team')}>
            팀 순위
          </button>

          <button className="team-record" onClick={() => handleTabClick('hitter')}>
            선수 기록
          </button>
        </div>

        {selectedTab === 'team' && (
          <div className="team-info-container">{renderTeamInfo()}</div>
        )}

        {selectedTab === 'hitter' && (
          <div className="hitter-section">
            <div className="hitter-buttons">
              <button
                className={`hitter-btn ${selectedHitter === '타자' ? 'active' : ''}`}
                onClick={() => setSelectedHitter('타자')}
              >
                타자
              </button>

              <button
                className={`hitter-btn ${selectedHitter === '투수' ? 'active' : ''}`}
                onClick={() => setSelectedHitter('투수')}
              >
                투수
              </button>
            </div>

            <div className="hitter-info-container">{renderHitterInfo(selectedHitter)}</div>
          </div>
        )}
      </div>
    </div>
  );
};

export default KboPage;