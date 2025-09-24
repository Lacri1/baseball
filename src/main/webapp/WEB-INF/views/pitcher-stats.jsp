<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
		<!DOCTYPE html>
		<html>

		<head>
			<meta charset="UTF-8">
			<title>KBO 투수 순위</title>
			<style>
				table {
					width: 100%;
					border-collapse: collapse;
				}

				th,
				td {
					border: 1px solid #ddd;
					padding: 8px;
					text-align: center;
				}

				th {
					background-color: #f2f2f2;
				}
			</style>
		</head>

		<body>

			<h1>KBO 2025 투수 순위</h1>

			<table>
				<thead>
					<tr>
						<th>순위</th>
						<th>선수명</th>
						<th>팀</th>
						<th><a href="?sortBy=era">평균자책점</a></th>
						<th><a href="?sortBy=numberOfGame">경기</a></th>
						<th><a href="?sortBy=win">승</a></th>
						<th><a href="?sortBy=lose">패</a></th>
						<th><a href="?sortBy=save">세이브</a></th>
						<th><a href="?sortBy=hold">홀드</a></th>
						<th><a href="?sortBy=numberOfInning">이닝</a></th>
						<th><a href="?sortBy=hit">피안타</a></th>
						<th><a href="?sortBy=homeRun">피홈런</a></th>
						<th><a href="?sortBy=fourBall">볼넷</a></th>
						<th><a href="?sortBy=strikeOut">탈삼진</a></th>
						<th><a href="?sortBy=run">실점</a></th>
						<th><a href="?sortBy=earnedRun">자책점</a></th>
						<th><a href="?sortBy=whip">WHIP</a></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="pitcher" items="${pitcherStats}" varStatus="loop">
						<tr>
							<td>${loop.index + 1}</td>
							<td>${pitcher.playerName}</td>
							<td>${pitcher.playerTeam}</td>
							<td>${pitcher.earnedRunAverage}</td>
							<td>${pitcher.gameNum}</td>
							<td>${pitcher.win}</td>
							<td>${pitcher.lose}</td>
							<td>${pitcher.save}</td>
							<td>${pitcher.hold}</td>
							<td>${pitcher.inningsPitchedDisplay}</td>
							<td>${pitcher.hits}</td>
							<td>${pitcher.homeRun}</td>
							<td>${pitcher.baseOnBalls}</td>
							<td>${pitcher.strikeOut}</td>
							<td>${pitcher.runs}</td>
							<td>${pitcher.earnedRun}</td>
							<td>${pitcher.whip}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>

		</body>

		</html>