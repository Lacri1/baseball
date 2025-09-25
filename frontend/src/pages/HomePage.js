import React, { useState, useEffect, useContext } from 'react';
import axios from 'axios';
import '../styles/HomePage.css';
import Slider from "react-slick";
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import { useNavigate } from 'react-router-dom';
import { parseHitterHtmlToArray, parsePitcherHtmlToArray, normalizeList } from '../utils/htmlTableParser';
import { AuthContext } from '../context/AuthContext';

const HomePage = () => {
    const [hitterStats, setHitterStats] = useState([]);
    const [pitcherStats, setPitcherStats] = useState([]);
    const [teamStats, setTeamStats] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const { user, login, logout } = useContext(AuthContext);
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [loginMessage, setLoginMessage] = useState('');

    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        setLoginMessage('');
        try {
            const res = await axios.post('http://localhost:8080/api/login/login', {
                id: username,
                pw: password
            });

            if (res.data.success) {
                // userInfo가 없으면 최소 Id를 넣어줌
                const userInfo = res.data.userInfo || { Id: username };
                login(userInfo);
                setLoginMessage('');
                setUsername('');
                setPassword('');
            } else {
                setLoginMessage(res.data.message || '로그인 실패');
            }
        } catch (err) {
            console.error(err);
            setLoginMessage('로그인 처리 중 오류 발생');
        }
    };

    const handleLogout = () => {
        logout();  // Context user 상태를 null로
        setLoginMessage('');
    };

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

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [hitterRes, pitcherRes, teamRes] = await Promise.all([
                    axios.get('/kbo/hitter-stats', { params: { sortBy: 'run' } }),
                    axios.get('/kbo/pitcher-stats', { params: { sortBy: 'era' } }),
                    axios.get('/kbo/team-stats')
                ]);

                setHitterStats(Array.isArray(hitterRes.data) ? hitterRes.data : (typeof hitterRes.data === 'string' ? parseHitterHtmlToArray(hitterRes.data) : normalizeList(hitterRes.data)));
                setPitcherStats(Array.isArray(pitcherRes.data) ? pitcherRes.data : (typeof pitcherRes.data === 'string' ? parsePitcherHtmlToArray(pitcherRes.data) : normalizeList(pitcherRes.data)));

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

    const sliderSettings = {
        dots: true,
        infinite: true,
        speed: 500,
        slidesToShow: 1,
        slidesToScroll: 1,
        autoplay: true,
        fade: true,
    };

    if (loading) return <div className="loading-container">데이터를 불러오는 중입니다...</div>;
    if (error) return <div className="error-container">{error}</div>;

    return (
        <div className="home-container">
            {/* Header */}
            <div className="header-content-banner">
                <h1 className="banner-title">KBO 야구의 모든 것</h1>
                <p className="banner-subtitle">최신 뉴스부터 경기 정보, 팀 순위까지 한 곳에서 확인하세요</p>
                <div className="banner-buttons">
                    <button className="kbo-button" onClick={() => navigate("/kbo")}>팀 정보 보기</button>
                    <button className="kbo-button" onClick={() => navigate("/kboboard")}>팬 게시판</button>
                    <button className="kbo-button" onClick={() => navigate("/game/game")}>야구 게임</button>
                </div>
            </div>

            {/* Main Content */}
            <div className="content-area">

                {/* Left Section */}
                <div className="main-left-section">
                    {/* Hitter & Pitcher Sliders */}
                    <div className="ranking-container">
                        <div className="ranking-header"><h2 className="ranking-title">선수 랭킹 TOP 5</h2></div>

                        {/* Hitter Slider */}
                        <div className="ranking-slider-hitter">
                            <Slider {...sliderSettings}>
                                <div className="ranking-box">
                                    <h3>타자 랭킹 (타율)</h3>
                                    <ol className="player-ranking-list">
                                        {[...hitterStats].sort((a,b)=>b.battingAverage-a.battingAverage).slice(0,5).map((p,i)=>(
                                            <li key={i}>
                                                <span className="rank-number">{i+1}.</span>
                                                <span className="player-name">{p.playerName} ({p.playerTeam})</span>
                                                <span className="player-stat">{p.battingAverage}</span>
                                            </li>
                                        ))}
                                    </ol>
                                </div>
                                <div className="ranking-box">
                                    <h3>타자 랭킹 (타점)</h3>
                                    <ol className="player-ranking-list">
                                        {[...hitterStats].sort((a,b)=>b.runsBattedIn-a.runsBattedIn).slice(0,5).map((p,i)=>(
                                            <li key={i}>
                                                <span className="rank-number">{i+1}.</span>
                                                <span className="player-name">{p.playerName} ({p.playerTeam})</span>
                                                <span className="player-stat">{p.runsBattedIn}</span>
                                            </li>
                                        ))}
                                    </ol>
                                </div>
                                <div className="ranking-box">
                                    <h3>타자 랭킹 (홈런)</h3>
                                    <ol className="player-ranking-list">
                                        {[...hitterStats].sort((a,b)=>b.homeRun-a.homeRun).slice(0,5).map((p,i)=>(
                                            <li key={i}>
                                                <span className="rank-number">{i+1}.</span>
                                                <span className="player-name">{p.playerName} ({p.playerTeam})</span>
                                                <span className="player-stat">{p.homeRun}</span>
                                            </li>
                                        ))}
                                    </ol>
                                </div>
                            </Slider>
                        </div>

                        {/* Pitcher Slider */}
                        <div className="ranking-slider-pitcher">
                            <Slider {...sliderSettings}>
                                <div className="ranking-box">
                                    <h3>투수 랭킹 (평균자책점)</h3>
                                    <ol className="player-ranking-list">
                                        {[...pitcherStats].sort((a,b)=>a.earnedRunAverage-b.earnedRunAverage).slice(0,5).map((p,i)=>(
                                            <li key={i}>
                                                <span className="rank-number">{i+1}.</span>
                                                <span className="player-name">{p.playerName} ({p.playerTeam})</span>
                                                <span className="player-stat">{p.earnedRunAverage}</span>
                                            </li>
                                        ))}
                                    </ol>
                                </div>
                                <div className="ranking-box">
                                    <h3>투수 랭킹 (승리)</h3>
                                    <ol className="player-ranking-list">
                                        {[...pitcherStats].sort((a,b)=>b.win-a.win).slice(0,5).map((p,i)=>(
                                            <li key={i}>
                                                <span className="rank-number">{i+1}.</span>
                                                <span className="player-name">{p.playerName} ({p.playerTeam})</span>
                                                <span className="player-stat">{p.win}</span>
                                            </li>
                                        ))}
                                    </ol>
                                </div>
                                <div className="ranking-box">
                                    <h3>투수 랭킹 (탈삼진)</h3>
                                    <ol className="player-ranking-list">
                                        {[...pitcherStats].sort((a,b)=>b.strikeOut-a.strikeOut).slice(0,5).map((p,i)=>(
                                            <li key={i}>
                                                <span className="rank-number">{i+1}.</span>
                                                <span className="player-name">{p.playerName} ({p.playerTeam})</span>
                                                <span className="player-stat">{p.strikeOut}</span>
                                            </li>
                                        ))}
                                    </ol>
                                </div>
                            </Slider>
                        </div>
                    </div>
                </div>

                {/* Right Section */}
                <div className="main-right-content-section">
                    {/* Login Box */}
                    <div className="login-container">
                        <h3></h3>
                        {user ? (
                            <div>
                                <p>{user.nickname}님 환영합니다!!</p>
                                <button className="login-button" onClick={handleLogout}>로그아웃</button>
                                <button className="login-button" onClick={() => navigate('/edit-profile')}>회원정보 수정</button>
                            </div>
                        ) : (
                            <form onSubmit={handleLogin} className="login-form">
                                <input type="text" placeholder="아이디" value={username} onChange={e=>setUsername(e.target.value)} required/>
                                <input type="password" placeholder="비밀번호" value={password} onChange={e=>setPassword(e.target.value)} required/>
                                <button type="submit" className="login-button">로그인</button>
                                <button type="button" className="login-button" onClick={()=>navigate('/ProfilePage')}>회원가입</button>
                            </form>
                        )}
                        {loginMessage && <div className="login-message">{loginMessage}</div>}
                    </div>

                    {/* Team Ranking */}
                    <div className="team-ranking-container">
                        <div className="ranking-header">
                            <h3 className="ranking-title"><span>🏆</span> 팀 순위 (상위 5팀)</h3>
                        </div>
                        <ol className="team-ranking-list">
                            {teamStats.slice(0,5).map((team,idx)=>(
                                <li key={idx}>
                                    <span className="rank-number">{idx+1}.</span>
                                    <span className="team-name">{team.teamName}</span>
                                    <span className="team-wins">{team.win}승</span>
                                </li>
                            ))}
                        </ol>
                    </div>

                    {/* KBO 뉴스 포탈 버튼 */}
                    <div className="news-portal-container">
                        <button onClick={()=>window.open('https://www.koreabaseball.com','_blank')}>
                            KBO 뉴스 포탈
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default HomePage;
