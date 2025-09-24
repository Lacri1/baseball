// server.js
const express = require('express');
const cors = require('cors');
const app = express();
const PORT = process.env.PORT ? Number(process.env.PORT) : 8081;

app.use(cors()); // React에서 요청 가능
app.use(express.json()); // JSON 바디 파싱

// 샘플 데이터 (프론트엔드 기대 형태에 맞춤)
// Player_ID, Player_Name 사용
const sampleTeams = {
  삼성: {
    batters: [
      { Player_ID: 101, Player_Name: '김현수' },
      { Player_ID: 102, Player_Name: '박병호' },
      { Player_ID: 103, Player_Name: '오재일' },
      { Player_ID: 104, Player_Name: '구자욱' },
      { Player_ID: 105, Player_Name: '강민호' },
      { Player_ID: 106, Player_Name: '김지찬' },
      { Player_ID: 107, Player_Name: '이재현' },
      { Player_ID: 108, Player_Name: '류지혁' },
      { Player_ID: 109, Player_Name: '최영진' },
      { Player_ID: 110, Player_Name: '김동진' },
    ],
    pitchers: [
      { Player_ID: 201, Player_Name: '원태인' },
      { Player_ID: 202, Player_Name: '뷰캐넌' },
      { Player_ID: 203, Player_Name: '백정현' },
    ],
  },
  LG: {
    batters: [
      { Player_ID: 301, Player_Name: '홍창기' },
      { Player_ID: 302, Player_Name: '박해민' },
      { Player_ID: 303, Player_Name: '김현수' },
      { Player_ID: 304, Player_Name: '오스틴' },
      { Player_ID: 305, Player_Name: '문보경' },
      { Player_ID: 306, Player_Name: '박동원' },
      { Player_ID: 307, Player_Name: '문성주' },
      { Player_ID: 308, Player_Name: '신민재' },
      { Player_ID: 309, Player_Name: '허도환' },
      { Player_ID: 310, Player_Name: '송찬의' },
    ],
    pitchers: [
      { Player_ID: 401, Player_Name: '켈리' },
      { Player_ID: 402, Player_Name: '임찬규' },
      { Player_ID: 403, Player_Name: '최원태' },
    ],
  },
  두산: {
    batters: [
      { Player_ID: 501, Player_Name: '정수빈' },
      { Player_ID: 502, Player_Name: '허경민' },
      { Player_ID: 503, Player_Name: '김재환' },
      { Player_ID: 504, Player_Name: '양석환' },
      { Player_ID: 505, Player_Name: '양의지' },
      { Player_ID: 506, Player_Name: '박건우' },
      { Player_ID: 507, Player_Name: '강승호' },
      { Player_ID: 508, Player_Name: '김인태' },
      { Player_ID: 509, Player_Name: '안재석' },
      { Player_ID: 510, Player_Name: '김대한' },
    ],
    pitchers: [
      { Player_ID: 601, Player_Name: '곽빈' },
      { Player_ID: 602, Player_Name: '알칸타라' },
      { Player_ID: 603, Player_Name: '최승용' },
    ],
  },
};

// 간단한 인메모리 게임 저장소
const games = new Map();
let nextGameId = 1;

// 타자 목록
app.get('/api/team/:teamName/batters', (req, res) => {
  const teamName = decodeURIComponent(req.params.teamName);
  const team = sampleTeams[teamName];
  if (!team) {
    // 팀 데이터가 없으면 기본 더미 데이터를 생성하여 반환
    const fallbackBatters = Array.from({ length: 12 }, (_, idx) => ({
      Player_ID: 1000 + idx,
      Player_Name: `${teamName} 타자${idx + 1}`,
    }));
    return res.json(fallbackBatters);
  }
  return res.json(team.batters);
});

// 투수 목록
app.get('/api/team/:teamName/pitchers', (req, res) => {
  const teamName = decodeURIComponent(req.params.teamName);
  const team = sampleTeams[teamName];
  if (!team) {
    // 팀 데이터가 없으면 기본 더미 투수 목록 반환
    const fallbackPitchers = Array.from({ length: 3 }, (_, idx) => ({
      Player_ID: 2000 + idx,
      Player_Name: `${teamName} 투수${idx + 1}`,
    }));
    return res.json(fallbackPitchers);
  }
  return res.json(team.pitchers);
});

// 게임 생성
app.post('/api/baseball/game', (req, res) => {
  const gameId = String(nextGameId++);
  const payload = req.body || {};
  games.set(gameId, { id: gameId, payload });
  // 프론트는 res.data.data.gameId 형태를 기대함
  return res.status(201).json({ data: { gameId } });
});

// 라인업 설정
app.post('/api/baseball/game/:gameId/lineup', (req, res) => {
  const { gameId } = req.params;
  const game = games.get(gameId);
  if (!game) {
    return res.status(404).json({ message: '게임을 찾을 수 없습니다.' });
  }
  game.lineup = req.body || {};
  games.set(gameId, game);
  return res.json({ data: { ok: true } });
});

// 게임 조회
app.get('/api/baseball/game/:gameId', (req, res) => {
  const { gameId } = req.params;
  const game = games.get(gameId);
  if (!game) {
    return res.status(404).json({ message: '게임을 찾을 수 없습니다.' });
  }
  return res.json({ data: game });
});

// 헬스체크
app.get('/api/health', (_req, res) => {
  return res.json({ status: 'ok' });
});

app.listen(PORT, () => {
  console.log(`Express Server running on http://localhost:${PORT}`);
});
