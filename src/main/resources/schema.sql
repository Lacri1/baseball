DROP TABLE IF EXISTS kbo_hitter_stats_2024;
DROP TABLE IF EXISTS kbo_pitcher_stats_2024;

CREATE TABLE kbo_hitter_stats_2024 (
    No INT AUTO_INCREMENT PRIMARY KEY,
    Player_Name VARCHAR(50) NOT NULL,
    Player_Team VARCHAR(50) NOT NULL,
    Batting_average DECIMAL(4, 3) NOT NULL,
    Game_Num INT NOT NULL,
    Plate_Appearance INT NOT NULL,
    At_Bat INT NOT NULL,
    Run INT NOT NULL,
    Hit INT NOT NULL,
    two_Base INT NOT NULL,
    three_Base INT NOT NULL,
    Home_Run INT NOT NULL,
    Total_Base INT NOT NULL,
    Runs_Batted_In INT NOT NULL,
    Sacrifice_Bunts INT NOT NULL,
    Sacrifice_Fly INT NOT NULL,
    Four_Ball INT NOT NULL,
    IBB INT NOT NULL,
    Hit_by_Pitch INT NOT NULL,
    Strike_Out INT NOT NULL,
    Double_out INT NOT NULL,
    Slugging DECIMAL(4, 3) NOT NULL,
    On_Base_Percentage DECIMAL(4, 3) NOT NULL,
    Onbase_Plus_Slug DECIMAL(4, 3) NOT NULL,
    Multi_Hit INT NOT NULL,
    Scoring_Position_AVG DECIMAL(4, 3) NOT NULL,
    Pinch_Hit_AVG DECIMAL(4, 3) NOT NULL,
    UNIQUE (Player_Name, Player_Team)
);

CREATE TABLE kbo_pitcher_stats_2024 (
    No INT AUTO_INCREMENT PRIMARY KEY,
    Player_Name VARCHAR(50) NOT NULL,
    Player_Team VARCHAR(50) NOT NULL,
    Earned_Run_Average DECIMAL(5, 2) NOT NULL,
    Game_Num INT NOT NULL,
    Win INT NOT NULL,
    Lose INT NOT NULL,
    Save INT NOT NULL,
    Hold INT NOT NULL,
    Winning_Percentage DECIMAL(4, 3) NOT NULL,
    Innings_Pitched DECIMAL(6, 3) NOT NULL,
    Hits INT NOT NULL,
    Home_Run INT NOT NULL,
    Base_On_Balls INT NOT NULL,
    Hit_By_Pitch INT NOT NULL,
    Strike_Out INT NOT NULL,
    Runs INT NOT NULL,
    Earned_Run INT NOT NULL,
    WHIP DECIMAL(4, 2) NOT NULL,
    Complete_Game INT NOT NULL,
    Shutout INT NOT NULL,
    Quality_Start INT NOT NULL,
    Blown_Save INT NOT NULL,
    Total_Batters_Faced INT NOT NULL,
    Number_Of_Pitching INT NOT NULL,
    Opponent_Batting_Average DECIMAL(4, 3) NOT NULL,
    two_Base INT NOT NULL,
    three_Base INT NOT NULL,
    Sacrifice_Bunt INT NOT NULL,
    Sacrifice_Fly INT NOT NULL,
    IBB INT NOT NULL,
    Wild_Pitch INT NOT NULL,
    Balk INT NOT NULL,
    UNIQUE (Player_Name, Player_Team)
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