<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
	<head>
		<title>Home</title>
	</head>
	
	<body>
		<h1>
			Hello world!
		</h1>
		
		<P>  The time on the server is ${serverTime}. </P>
		
		<a href="/kbo/hitter-stats">타자</a>
		
		<a href="/kbo/pitcher-stats">투수</a>
		
		<a href="/kbo/team-stats">팀</a>
	</body>
</html>
