import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api";

const TeamInfoPage = () => {
  const [teams, setTeams] = useState([]);
  const [selectedTeam, setSelectedTeam] = useState("");
  const [teamInfo, setTeamInfo] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    // 모든 팀 정보 가져오기
    const fetchTeams = async () => {
      try {
        const res = await api.get("/api/team/list");
        setTeams(res.data);
      } catch (err) {
        console.error("팀 목록 불러오기 실패", err);
      }
    };
    fetchTeams();
  }, []);

  useEffect(() => {
    if (!selectedTeam) return;
    const fetchTeamInfo = async () => {
      try {
        const res = await api.get(`/api/team/${selectedTeam}/roster`);
        setTeamInfo(res.data);
      } catch (err) {
        console.error("팀 정보 불러오기 실패", err);
      }
    };
    fetchTeamInfo();
  }, [selectedTeam]);

  return (
    <div style={{ padding: 20 }}>
      <h2>팀 정보</h2>
      <select value={selectedTeam} onChange={(e)=>setSelectedTeam(e.target.value)}>
        <option value="">팀 선택</option>
        {teams.map(t=><option key={t} value={t}>{t}</option>)}
      </select>

      {teamInfo && (
        <div style={{ marginTop: 20 }}>
          <h3>{selectedTeam} 라인업</h3>
          <table border="1" cellPadding="5">
            <thead>
              <tr>
                <th>No</th>
                <th>선수</th>
                <th>팀</th>
                <th>타율</th>
                <th>경기수</th>
                <th>타석</th>
                <th>타수</th>
                <th>득점</th>
                <th>안타</th>
                <th>2루타</th>
                <th>3루타</th>
                <th>홈런</th>
                <th>총루타</th>
                <th>타점</th>
                <th>희생번트</th>
                <th>희생플라이</th>
                <th>볼넷</th>
                <th>고의사구</th>
                <th>사구</th>
                <th>삼진</th>
                <th>병살타</th>
                <th>장타율</th>
                <th>출루율</th>
                <th>OPS</th>
                <th>멀티히트</th>
                <th>득점권타율</th>
                <th>대타타율</th>
              </tr>
            </thead>
            <tbody>
              {teamInfo.batters.map((p,i)=>(
                <tr key={i}>
                  <td>{p.no}</td>
                  <td>{p.name}</td>
                  <td>{p.team}</td>
                  <td>{p.battingAverage}</td>
                  <td>{p.gameNum}</td>
                  <td>{p.plateAppearances}</td>
                  <td>{p.atBats}</td>
                  <td>{p.run}</td>
                  <td>{p.hits}</td>
                  <td>{p.twoBases}</td>
                  <td>{p.threeBases}</td>
                  <td>{p.homeRuns}</td>
                  <td>{p.totalBases}</td>
                  <td>{p.runsBattedIn}</td>
                  <td>{p.sacrificeBunts}</td>
                  <td>{p.sacrificeFly}</td>
                  <td>{p.fourBall}</td>
                  <td>{p.ibb}</td>
                  <td>{p.hitByPitch}</td>
                  <td>{p.strikeOut}</td>
                  <td>{p.doubleOut}</td>
                  <td>{p.slugging}</td>
                  <td>{p.onBasePercentage}</td>
                  <td>{p.onbasePlusSlug}</td>
                  <td>{p.multiHit}</td>
                  <td>{p.scoringPositionAvg}</td>
                  <td>{p.pinchHitAvg}</td>
                </tr>
              ))}
            </tbody>
          </table>

          <h3 style={{ marginTop: 10 }}>투수 정보</h3>
          <table border="1" cellPadding="5">
            <thead>
              <tr>
                <th>No</th>
                <th>선수</th>
                <th>팀</th>
                <th>평균자책점</th>
                <th>경기수</th>
                <th>승</th>
                <th>패</th>
                <th>세이브</th>
                <th>홀드</th>
                <th>승률</th>
                <th>이닝</th>
                <th>피안타</th>
                <th>피홈런</th>
                <th>볼넷</th>
                <th>사구</th>
                <th>삼진</th>
                <th>실점</th>
                <th>자책점</th>
                <th>WHIP</th>
                <th>완투</th>
                <th>완봉</th>
                <th>QS</th>
                <th>블론세이브</th>
                <th>상대타자</th>
                <th>투구수</th>
                <th>피안타율</th>
                <th>2루타</th>
                <th>3루타</th>
                <th>희생번트</th>
                <th>희생플라이</th>
                <th>고의사구</th>
                <th>폭투</th>
                <th>보크</th>
              </tr>
            </thead>
            <tbody>
              {teamInfo.pitchers.map((p,i)=>(
                <tr key={i}>
                  <td>{p.no}</td>
                  <td>{p.name}</td>
                  <td>{p.team}</td>
                  <td>{p.earnedRunAverage}</td>
                  <td>{p.gameNum}</td>
                  <td>{p.win}</td>
                  <td>{p.lose}</td>
                  <td>{p.save}</td>
                  <td>{p.hold}</td>
                  <td>{p.winningPercentage}</td>
                  <td>{p.inningsPitched}</td>
                  <td>{p.hits}</td>
                  <td>{p.homeRun}</td>
                  <td>{p.baseOnBalls}</td>
                  <td>{p.hitByPitch}</td>
                  <td>{p.strikeOut}</td>
                  <td>{p.runs}</td>
                  <td>{p.earnedRun}</td>
                  <td>{p.whip}</td>
                  <td>{p.completeGame}</td>
                  <td>{p.shutout}</td>
                  <td>{p.qualityStart}</td>
                  <td>{p.blownSave}</td>
                  <td>{p.totalBattersFaced}</td>
                  <td>{p.numberOfPitching}</td>
                  <td>{p.opponentBattingAverage}</td>
                  <td>{p.twoBases}</td>
                  <td>{p.threeBases}</td>
                  <td>{p.sacrificeBunt}</td>
                  <td>{p.sacrificeFly}</td>
                  <td>{p.ibb}</td>
                  <td>{p.wildPitch}</td>
                  <td>{p.balk}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <div style={{ marginTop: 20 }}>
        <button onClick={()=>navigate("/")}>메인 화면</button>
      </div>
    </div>
  );
};

export default TeamInfoPage;
