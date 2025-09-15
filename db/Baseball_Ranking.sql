use my_cat;

-- 기록 표시용 타자 table
CREATE TABLE kbo_hitter_stats_2025 (
	No INT AUTO_INCREMENT PRIMARY KEY, -- 자동 증가하는 고유 ID 컬럼
    Player_Name VARCHAR(50) NOT NULL,
    Player_Team VARCHAR(50) NOT NULL,
    Batting_average DECIMAL(4, 3) NOT NULL, -- 타율 (예: 0.326, 파이썬에서 0으로 채움)
	Game_Num INT NOT NULL,                  -- 경기 수
	Plate_Appearance INT NOT NULL,           -- 타석
    Run INT NOT NULL,                       -- 득점
    Hit INT NOT NULL,                       -- 안타
    two_Base INT NOT NULL,                  -- 2루타
    three_Base INT NOT NULL,                -- 3루타
    Home_Run INT NOT NULL,                  -- 홈런
    Runs_Batted_In INT NOT NULL,            -- 타점
    Four_Ball INT NOT NULL,                 -- 볼넷
	Strike_Out INT NOT NULL,                -- 삼진
    On_Base_Percentage DECIMAL(4, 3) NOT NULL, -- 출루율 (예: 0.388, 파이썬에서 0으로 채움)
    Onbase_Plus_Slug DECIMAL(4, 3) NOT NULL,   -- OPS (예: 0.887, 파이썬에서 0으로 채움)
    UNIQUE (Player_Name, Player_Team)       -- 선수명과 팀명 조합은 고유해야 함
);

LOAD DATA LOCAL INFILE 'C:/Users/user/IdeaProjects/baseball/db/2025_useBoard_hitter.csv'
INTO TABLE kbo_hitter_stats_2025
CHARACTER SET utf8mb4
FIELDS TERMINATED BY ','
OPTIONALLY ENCLOSED BY '"' -- 필드가 따옴표로 묶여있을 수 있음을 명시
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(Player_Name, Player_Team, Batting_average, Game_Num, Plate_Appearance, Run, Hit, two_Base, three_Base, Home_Run, Runs_Batted_In, 
Four_Ball, Strike_Out, On_Base_Percentage, Onbase_Plus_Slug);

select * from kbo_hitter_stats_2025;
drop table kbo_hitter_stats_2025;
TRUNCATE TABLE kbo_hitter_stats_2025;

# 예외사항 : 타율은 규정 타석(팀게임수 * 3.1)을 채운 선수들에 한해서 순위를 정할 수 있다.
# -> kbo_hitter_stats_2025과 kbo_team_stats_2025의 스탯이 같이 사용되어야 한다.
# -> kbo_hitter_stats_2025과 kbo_team_stats_2025을 join하는 sql문을 사용해야 한다.
SELECT h.* FROM kbo_hitter_stats_2025 h
JOIN kbo_team_stats_2025 t ON h.Player_Team = t.Team_Name
WHERE h.Plate_Appearance >= t.Game_Num * 3.1
ORDER BY h.Batting_average DESC LIMIT 10;

###################################################################################################

-- 기록 표시용 투수 table
CREATE TABLE kbo_pitcher_stats_2025 (
	No INT AUTO_INCREMENT PRIMARY KEY, -- 자동 증가하는 고유 ID 컬럼
    Player_Name VARCHAR(50) NOT NULL,
    Player_Team VARCHAR(50) NOT NULL,
    Earned_Run_Average DECIMAL(5, 2) NOT NULL, -- 평균자책점 (예: 3.25, 소수점 2자리)
    Game_Num INT NOT NULL,                  -- 경기 수
    Win INT NOT NULL,                       -- 승
    Lose INT NOT NULL,                      -- 패
    Save INT NOT NULL,                      -- 세이브
    Hold INT NOT NULL,                      -- 홀드
    Innings_Pitched DECIMAL(6, 3) NOT NULL, -- 이닝 (예: 123.1, 소수점 1자리)
    Hits INT NOT NULL,                      -- 피안타
    Home_Run INT NOT NULL,                  -- 피홈런
    Base_On_Balls INT NOT NULL,             -- 볼넷
    Strike_Out INT NOT NULL,                -- 탈삼진
    Runs INT NOT NULL,                      -- 실점
    Earned_Run INT NOT NULL,                -- 자책
    WHIP DECIMAL(4, 2) NOT NULL,            -- WHIP (예: 1.23, 소수점 2자리)
    UNIQUE (Player_Name, Player_Team)       -- 선수명과 팀명 조합은 고유해야 함
);

LOAD DATA LOCAL INFILE 'C:/Users/user/IdeaProjects/baseball/db/2025_useBoard_pitcher.csv'
INTO TABLE kbo_pitcher_stats_2025
CHARACTER SET utf8mb4
FIELDS TERMINATED BY ','
OPTIONALLY ENCLOSED BY '"' -- 필드가 따옴표로 묶여있을 수 있음을 명시
LINES TERMINATED BY '\r\n' -- Windows 스타일 줄바꿈 문자 (CRLF)
IGNORE 1 LINES -- CSV 파일의 첫 번째 줄(헤더) 무시
(Player_Name, Player_Team, Earned_Run_Average, Game_Num, Win, Lose, Save, Hold, 
Innings_Pitched, Hits, Home_Run, Base_On_Balls, Strike_Out, Runs, Earned_Run, WHIP);

# 예외사항 : 평균 자책점은 규정 이닝(팀게임수 * 1)을 채운 선수들에 한해서 순위를 정할 수 있다.
# -> kbo_pitcher_stats_2025과 kbo_team_stats_2025의 스탯이 같이 사용되어야 한다.
# -> kbo_pitcher_stats_2025과 kbo_team_stats_2025을 join하는 sql문을 사용해야 한다.
SELECT p.* FROM kbo_pitcher_stats_2025 p
JOIN kbo_team_stats_2025 t ON p.Player_Team = t.Team_Name
WHERE p.Innings_Pitched >= t.Game_Num
ORDER BY p.Earned_Run_Average DESC LIMIT 10;

select * from kbo_pitcher_stats_2025;
drop table kbo_pitcher_stats_2025;
TRUNCATE TABLE kbo_pitcher_stats_2025;

###################################################################################################

-- 기록 표시용 팀 table
CREATE TABLE kbo_team_stats_2025 (
    No INT AUTO_INCREMENT PRIMARY KEY,   -- 자동 증가하는 고유 ID (기본 키)
    Team_Name VARCHAR(50) NOT NULL,      -- 팀 이름 (한글 포함 가능, 최대 50자)
    Game_Num INT,                        -- 총 경기 수 (정수)
    Win INT,                             -- 승리 수 (정수)
    Lose INT,                            -- 패배 수 (정수)
    Draw INT,                            -- 무승부 수 (정수)
    Win_Percentage DECIMAL(5, 3),        -- 승률 (예: 0.612, 전체 5자리 중 소수점 이하 3자리)
    Games_Behind DECIMAL(5, 2)           -- 게임차 (예: 4.5, 전체 5자리 중 소수점 이하 2자리)
);

LOAD DATA LOCAL INFILE 'C:/Users/user/IdeaProjects/baseball/db/2025_useBoard_team.csv'
INTO TABLE kbo_team_stats_2025
CHARACTER SET utf8mb4
FIELDS TERMINATED BY ','
OPTIONALLY ENCLOSED BY '"' -- 필드가 따옴표로 묶여있을 수 있음을 명시
LINES TERMINATED BY '\r\n' -- Windows 스타일 줄바꿈 문자 (CRLF)
IGNORE 1 LINES -- CSV 파일의 첫 번째 줄(헤더) 무시
(Team_Name, Game_Num, Win, Lose, Draw, Win_Percentage, Games_Behind);

select * from kbo_team_stats_2025;
drop table kbo_team_stats_2025;
TRUNCATE TABLE kbo_team_stats_2025;
