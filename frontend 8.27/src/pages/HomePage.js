import React, { useState, useEffect } from 'react';
import api from '../api'; // Axios 대신 중앙 API 인스턴스 임포트
import '../styles/HomePage.css';
import Slider from "react-slick";
import { parseTeamHtmlToArray, parseHitterHtmlToArray, parsePitcherHtmlToArray, normalizeList } from '../utils/htmlTableParser';
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";

const HomePage = () => {
    // API로부터 가져온 데이터를 저장할 상태
    const [hitterStats, setHitterStats] = useState([]);
    const [pitcherStats, setPitcherStats] = useState([]);
    const [teamStats, setTeamStats] = useState([]);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // HTML 응답(서버 렌더링 테이블)에서 팀 순위를 추출하는 파서
    const parseTeamHtmlToArray = (htmlString) => {
        try {
            const parser = new DOMParser();
            const doc = parser.parseFromString(htmlString, 'text/html');
            const rows = doc.querySelectorAll('tbody tr');
            const result = [];
            rows.forEach((tr) => {
                const tds = Array.from(tr.querySelectorAll('td')).map(td => td.textContent.trim());
                if (tds.length >= 8) {
                    result.push({
                        teamName: tds[1],
                        gameNum: Number(tds[2]),
                        win: Number(tds[3]),
                        lose: Number(tds[4]),
                        draw: Number(tds[5]),
                        winPercentage: tds[6],
                        gamesBehind: tds[7]
                    });
                }
            });
            return result;
        } catch (e) {
            return [];
        }
    };

    // 컴포넌트 마운트 시 API 호출
    useEffect(() => {
        const fetchData = async () => {
            try {
                // 타자 기록 (홈런 순)
                // JSP 파일의 `<c:forEach>`에서 사용된 속성 이름과 일치하도록 `hitter.homeRun` 등을 사용
                const hitterRes = await api.get('/api/kbo/hitter-stats', {
                    params: { sortBy: 'run' }
                });
                setHitterStats(Array.isArray(hitterRes.data) ? hitterRes.data : (typeof hitterRes.data === 'string' ? parseHitterHtmlToArray(hitterRes.data) : normalizeList(hitterRes.data)));

                // 투수 기록 (승리 순)
                // JSP 파일의 `<c:forEach>`에서 사용될 속성 이름과 일치하도록 `pitcher.win` 등을 사용
                const pitcherRes = await api.get('/api/kbo/pitcher-stats', {
                    params: { sortBy: 'era' }
                });
                setPitcherStats(Array.isArray(pitcherRes.data) ? pitcherRes.data : (typeof pitcherRes.data === 'string' ? parsePitcherHtmlToArray(pitcherRes.data) : normalizeList(pitcherRes.data)));

                // 팀 기록
                const teamRes = await api.get('/api/kbo/team-stats');
                const rawTeam = teamRes.data;
                const normalizedTeam = Array.isArray(rawTeam)
                    ? rawTeam
                    : (typeof rawTeam === 'string' ? parseTeamHtmlToArray(rawTeam) : normalizeList(rawTeam));
                setTeamStats(normalizedTeam);

            } catch (err) {
                setError("데이터를 불러오는 데 실패했습니다.");
                console.error("Error fetching data:", err);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, []);

    var settings = {
        dots: true,
        infinite: true,
        speed: 500,
        slidesToShow: 1,
        slidesToScroll: 1,
        autoplay: true,
        fade: true
    };

    if (loading) {
        return <div className="loading-container">데이터를 불러오는 중입니다...</div>;
    }

    if (error) {
        return <div className="error-container">{error}</div>;
    }

    return (
        <div className="home-container">
            <section className="hero-section">
                <Slider {...settings}>

                    <div>
                        <h1>KBO 타자 TOP5 랭킹</h1>

                        <div className="image-container">
                            <div className="ranking-category">
                                <h2>타율</h2>
                                {Array.isArray(hitterStats) && hitterStats.slice(0, 5).map((player, index) => (
                                    <p key={index}>{index + 1}. {player.name} ({player.battingAverage}) ({player.team})</p>
                                ))}
                            </div>

                            <div className="ranking-category">
                                <h2>타점</h2>
                                {Array.isArray(hitterStats) && hitterStats.slice(0, 5).map((player, index) => (
                                    <p key={index}>{index + 1}. {player.name} ({player.runsBattedIn}) ({player.team})</p>
                                ))}
                            </div>

                            <div className="ranking-category">
                                <h2>홈런</h2>
                                {Array.isArray(hitterStats) && hitterStats.slice(0, 5).map((player, index) => (
                                    <p key={index}>{index + 1}. {player.name} ({player.homeRuns}) ({player.team})</p>
                                ))}
                            </div>
                        </div>
                    </div>

                    <div>
                        <h1>KBO 투수 TOP5 랭킹</h1>
                          <div className="image-container">
                            <div className="ranking-category">
                                <h2>ERA</h2>
                                {Array.isArray(pitcherStats) && pitcherStats.slice(0, 5).map((player, index) => (
                                    <p key={index}>{index + 1}. {player.name} ({player.era}) ({player.team})</p>
                                ))}
                            </div>

                            <div className="ranking-category">
                                <h2>승리</h2>
                                {Array.isArray(pitcherStats) && pitcherStats.slice(0, 5).map((player, index) => (
                                    <p key={index}>{index + 1}. {player.name} ({player.win}) ({player.team})</p>
                                ))}
                            </div>

                            <div className="ranking-category">
                                <h2>탈삼진</h2>
                                {Array.isArray(pitcherStats) && pitcherStats.slice(0, 5).map((player, index) => (
                                    <p key={index}>{index + 1}. {player.name} ({player.strikeOut}) ({player.team})</p>
                                ))}
                            </div>
                        </div>
                    </div>

                </Slider>
            </section>
            
            <section className="team-ranking-row">
                <div className="team-ranking">
                    <h1 className="team-ranking-title">KBO팀순위</h1>

                    <div className="team-ranking-container">
                        <div className="team-ranking-item">
                            {/* teamStats가 배열인지 확인 후 map 함수 사용 */}
                            {Array.isArray(teamStats) && teamStats.map((team, index) => (
                                <h2 key={index}>{index + 1}. {team.teamName} ({team.winPercentage})</h2>
                            ))}
                        </div>
                    </div>

                </div>
            </section>
        </div>
    );
};

export default HomePage;