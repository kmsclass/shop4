<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- /webapp/WEB-INF/view/chat/chat.jsp --%>
<%@ include file="/WEB-INF/view/jspHeader.jsp" %>    
<!DOCTYPE html><html><head><meta charset="UTF-8">
<title>websocket client</title>
<c:set var="port" value="${pageContext.request.localPort}"/>
<c:set var="server" value="${pageContext.request.serverName}"/>
<c:set var="path" value="${pageContext.request.contextPath}"/>
<script type="text/javascript">
  $(function() {
	  //WebSocket : 자바스크립트의 채팅을 위한 소켓객체
	  // ws://localhost:8080/shop3/chatting.shop   => 서버소켓의 주소. 
	  // 서버와 연결됨.
	  var ws = new WebSocket("ws://${server}:${port}${path}/chatting.shop");
	  ws.onopen = function() {  //서버 연결 완료
		  $("#chatStatus").text("info:connection opened")
		  //name=chatInput 인 태그에 keydown 이벤트 등록
		  $("input[name=chatInput]").on("keydown",function(evt) {
			  if(evt.keyCode == 13) { //enter키가 눌려진 경우
				  var msg = $("input[name=chatInput]").val(); //등록한 메세지
				  ws.send(msg); //서버에 메세지 전송
				  $("input[name=chatInput]").val(""); //등록한 메세지 초기화
			  }
		  })
	  }
	  //서버로 부터 메세지를 수신한 경우
	  ws.onmessage = function(event) {
		  //prepend : 앞에 내용 추가
		  //append  : 뒤에 내용 추가
		  $("textarea").eq(0).prepend(event.data+"\n");
	  }
	  //서버와 연결 끊어 진 경우
	  ws.onclose = function(event) {
		  $("#chatStatus").text("info:connection close");
	  }
  })
</script></head>

<body>
<p><div id="chatStatus"></div>
<textarea name="chatMsg" rows="15" cols="40"></textarea><br>
메시지 입력 : <input type="text" name="chatInput">
</body></html>