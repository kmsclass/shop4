<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/jspHeader.jsp" %>    
<%-- /webapp/WEB-INF/view/user/delete.jsp --%>    
<!DOCTYPE html>
<html><head><meta charset="UTF-8"><title>회원 탈퇴</title></head>
<body>
<table>
  <tr><td>아이디</td><td>${user.userid}</td></tr>
  <tr><td>이름</td><td>${user.username}</td></tr>
  <tr><td>생년월일</td><td><fmt:formatDate value="${user.birthday}"
     pattern="yyyy-MM-dd" /></td></tr>
</table>
<%--
    1. 파라미터 : userid, password
    2. 로그인 정보
        본인탈퇴   : 본인 비밀번호 검증
        관리자탈퇴 : 관리자 비밀번호 검증
    3. 비밀번호 일치
        본인탈퇴 : 로그아웃. login.shop 페이지 이동
        관리자탈퇴 : ../admin/list.shop
    4. 비밀번호 불일치
        delete.shop 페이지 이동        
 --%>
<form method="post" action="delete.shop" name="deleteform">
    <input type="hidden" name="userid" value="${param.id}">
    비밀번호 <input type="password" name="password">
	<a href="javascript:deleteform.submit()">[회원탈퇴]</a>    
  </form></body></html>