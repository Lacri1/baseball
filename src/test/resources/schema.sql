DROP TABLE IF EXISTS member;
CREATE TABLE member (
  Id   VARCHAR(50) PRIMARY KEY,
  Pw   VARCHAR(100),
  Team VARCHAR(50),
  Game INT DEFAULT 0,
  Win  INT DEFAULT 0,
  Lose INT DEFAULT 0,
  Draw INT DEFAULT 0
);

DROP TABLE IF EXISTS board;
CREATE TABLE board (
  no INT AUTO_INCREMENT PRIMARY KEY,
  category VARCHAR(50),
  title VARCHAR(200) NOT NULL,
  text TEXT,
  writer VARCHAR(50),
  view INT DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 게임 기본 라인업 테이블 (테스트용)
DROP TABLE IF EXISTS team_lineup;
CREATE TABLE team_lineup (
  id INT AUTO_INCREMENT PRIMARY KEY,
  team_name VARCHAR(50),
  user_id VARCHAR(50),
  position VARCHAR(50),
  player_name VARCHAR(100),
  is_active BOOLEAN,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);