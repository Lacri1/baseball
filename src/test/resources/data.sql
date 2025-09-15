INSERT INTO member (Id, Pw, Team, Game, Win, Lose, Draw)
VALUES ('u1', 'pw', 'Giants', 0, 0, 0, 0);

INSERT INTO board (category, title, text, writer, view)
VALUES ('notice', 'hello', 'world', 'u1', 0);

-- kbo_hitter_stats_2025
INSERT INTO kbo_hitter_stats_2025 (Player_Name, Player_Team, Batting_average, Game_Num, Plate_Appearance, Run, Hit, two_Base, three_Base, Home_Run, Runs_Batted_In, Four_Ball, Strike_Out, On_Base_Percentage, Onbase_Plus_Slug)
VALUES
('황성빈', 'Giants', 0.300, 100, 400, 50, 120, 20, 5, 10, 60, 40, 80, 0.380, 0.800),
('윤동희', 'Giants', 0.280, 100, 380, 45, 100, 15, 3, 8, 50, 35, 70, 0.350, 0.750),
('김재호', '두산 베어스', 0.250, 100, 350, 30, 80, 10, 2, 5, 40, 30, 60, 0.320, 0.650);

-- kbo_pitcher_stats_2025
INSERT INTO kbo_pitcher_stats_2025 (Player_Name, Player_Team, Earned_Run_Average, Game_Num, Win, Lose, Save, Hold, Innings_Pitched, Hits, Home_Run, Base_On_Balls, Strike_Out, Runs, Earned_Run, WHIP)
VALUES
('박세웅', 'Giants', 3.50, 20, 10, 5, 0, 0, 120.0, 100, 10, 30, 100, 50, 40, 1.20),
('김광현', 'SSG 랜더스', 2.80, 25, 12, 3, 0, 0, 150.0, 120, 15, 25, 130, 40, 35, 1.10);

-- kbo_team_stats_2025
INSERT INTO kbo_team_stats_2025 (Team_Name, Game_Num, Win, Lose, Draw, Win_Percentage, Games_Behind)
VALUES
('Giants', 144, 70, 70, 4, 0.500, 0.0),
('SSG 랜더스', 144, 75, 65, 4, 0.530, 5.0),
('두산 베어스', 144, 68, 72, 4, 0.480, 7.0);