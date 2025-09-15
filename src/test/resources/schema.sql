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

-- 기록 표시용 타자 table
DROP TABLE IF EXISTS kbo_hitter_stats_2025;
CREATE TABLE kbo_hitter_stats_2025 (
	No INT AUTO_INCREMENT PRIMARY KEY,
    Player_Name VARCHAR(50) NOT NULL,
    Player_Team VARCHAR(50) NOT NULL,
    Batting_average DECIMAL(4, 3) NOT NULL,
	Game_Num INT NOT NULL,
	Plate_Appearance INT NOT NULL,
    Run INT NOT NULL,
    Hit INT NOT NULL,
    two_Base INT NOT NULL,
    three_Base INT NOT NULL,
    Home_Run INT NOT NULL,
    Runs_Batted_In INT NOT NULL,
    Four_Ball INT NOT NULL,
	Strike_Out INT NOT NULL,
    On_Base_Percentage DECIMAL(4, 3) NOT NULL,
    Onbase_Plus_Slug DECIMAL(4, 3) NOT NULL,
    UNIQUE (Player_Name, Player_Team)
);

-- 기록 표시용 투수 table
DROP TABLE IF EXISTS kbo_pitcher_stats_2025;
CREATE TABLE kbo_pitcher_stats_2025 (
	No INT AUTO_INCREMENT PRIMARY KEY,
    Player_Name VARCHAR(50) NOT NULL,
    Player_Team VARCHAR(50) NOT NULL,
    Earned_Run_Average DECIMAL(5, 2) NOT NULL,
    Game_Num INT NOT NULL,
    Win INT NOT NULL,
    Lose INT NOT NULL,
    Save INT NOT NULL,
    Hold INT NOT NULL,
    Innings_Pitched DECIMAL(6, 3) NOT NULL,
    Hits INT NOT NULL,
    Home_Run INT NOT NULL,
    Base_On_Balls INT NOT NULL,
    Strike_Out INT NOT NULL,
    Runs INT NOT NULL,
    Earned_Run INT NOT NULL,
    WHIP DECIMAL(4, 2) NOT NULL,
    UNIQUE (Player_Name, Player_Team)
);

-- 기록 표시용 팀 table
DROP TABLE IF EXISTS kbo_team_stats_2025;
CREATE TABLE kbo_team_stats_2025 (
    No INT AUTO_INCREMENT PRIMARY KEY,
    Team_Name VARCHAR(50) NOT NULL,
    Game_Num INT,
    Win INT,
    Lose INT,
    Draw INT,
    Win_Percentage DECIMAL(5, 3),
    Games_Behind DECIMAL(5, 2)
);