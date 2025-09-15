<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
	<head>
	    <meta charset="UTF-8">
	    <title>KBO 팀 순위</title>
	    <style>
	        table { width: 100%; border-collapse: collapse; }
	        th, td { border: 1px solid #ddd; padding: 8px; text-align: center; }
	        th { background-color: #f2f2f2; }
	    </style>
	</head>
	
	<body>
	
	    <h1>KBO 2025 팀 순위</h1>
	
	    <table>
	        <thead>
	            <tr>
	                <th>순위</th>
	                <th>팀명</th>
	                <th>경기</th>
	                <th>승</th>
	                <th>패</th>
	                <th>무</th>
	                <th>승률</th>
	                <th>게임차</th>
	            </tr>
	        </thead>
	        
	        <tbody>
	            <c:forEach var="team" items="${teamStats}" varStatus="loop">
	                <tr>
	                    <td>${loop.index + 1}</td>
	                    <td>${team.teamName}</td>
	                    <td>${team.gameNum}</td>
	                    <td>${team.win}</td>
	                    <td>${team.lose}</td>
	                    <td>${team.draw}</td>
	                    <td>${team.winPercentage}</td>
	                    <td>${team.gamesBehind}</td>
	                </tr>
	            </c:forEach>
	        </tbody>
	    </table>
	
	</body>
</html>