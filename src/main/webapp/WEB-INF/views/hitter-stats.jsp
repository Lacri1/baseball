<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>

<html>
	<head>
	    <meta charset="UTF-8">
	    <title>KBO 타자 순위</title>
	    
	    <style>
	        table { width: 100%; border-collapse: collapse; }
	        th, td { border: 1px solid #ddd; padding: 8px; text-align: center; }
	        th { background-color: #f2f2f2; }
	    </style>
	</head>
	
	<body>
	
	    <h1>KBO 2025 타자 순위</h1>
	
	    <table>
	        <thead>
	            <tr>
	                <th>순위</th>
	                <th>선수명</th>
	                <th>팀</th>
	                <th><a href="?sortBy=battingAverage">타율</a></th>
	                <th><a href="?sortBy=numberOfGames">경기</a></th>
	                <th><a href="?sortBy=plateAppearance">타석</a></th>
	                <th><a href="?sortBy=run">득점</a></th>
	                <th><a href="?sortBy=hit">안타</a></th>
	                <th><a href="?sortBy=twoBase">2루타</a></th>
	                <th><a href="?sortBy=threeBase">3루타</a></th>
	                <th><a href="?sortBy=homeRun">홈런</a></th>
	                <th><a href="?sortBy=runsBattedIn">타점</a></th>
	                <th><a href="?sortBy=fourBall">볼넷</a></th>
	                <th><a href="?sortBy=strikeOut">삼진</a></th>
	                <th><a href="?sortBy=onBasePercentage">출루율</a></th>
	                <th><a href="?sortBy=ops">OPS</a></th>
	            </tr>
	        </thead>
	        
	        <tbody>
	            <c:forEach var="hitter" items="${hitterStats}" varStatus="loop">
	                <tr>
	                    <td>${loop.index + 1}</td>
	                    <td>${hitter.playerName}</td>
	                    <td>${hitter.playerTeam}</td>
	                    <td>${hitter.battingAverage}</td>
	                    <td>${hitter.gameNum}</td>
	                    <td>${hitter.plateAppearance}</td>
	                    <td>${hitter.run}</td>
	                    <td>${hitter.hit}</td>
	                    <td>${hitter.twoBase}</td>
	                    <td>${hitter.threeBase}</td>
	                    <td>${hitter.homeRun}</td>
	                    <td>${hitter.runsBattedIn}</td>
	                    <td>${hitter.fourBall}</td>
	                    <td>${hitter.strikeOut}</td>
	                    <td>${hitter.onBasePercentage}</td>
	                    <td>${hitter.onbasePlusSlug}</td>
	                </tr>
	            </c:forEach>
	        </tbody>
	        
	    </table>
	
	</body>
	
</html>