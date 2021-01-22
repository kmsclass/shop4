package websocket;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
//chatting을 위한 서버 구현
@Component
public class EchoHandler extends TextWebSocketHandler 
                                      implements InitializingBean {
	//클라언트들의 연결객체들의 모임
	private Set<WebSocketSession> clients = new HashSet<WebSocketSession>();

	@Override   //소켓이 연결된경우
	public void afterConnectionEstablished
	    (WebSocketSession session)  throws Exception {
		//session : 클라이언트의 연결 객체
		super.afterConnectionEstablished(session);
		System.out.println("클라이언트 접속 : " + session.getId());
		clients.add(session);
	}
	@Override  //메세지 수신 받은 경우
	public void handleMessage(WebSocketSession session,
			WebSocketMessage<?> message) throws Exception {
		//클라이언트에서 보내준 메세지
		String loadMessage = (String)message.getPayload();
		System.out.println(session.getId() + ":클라이언트 메세지:" + loadMessage);
		clients.add(session); //추가된 클라이언트는 추가되지 않음.

		//모든 클라이언트에게 수신된 메세지 전송
		for(WebSocketSession s : clients) {
			s.sendMessage(new TextMessage(loadMessage));
		}
	}
	@Override   //오류 발생시.
	public void handleTransportError(WebSocketSession session,
			Throwable exception) throws Exception {
		super.handleTransportError(session,exception);
//		exception.printStackTrace();
		System.out.println("오류발생 : " + exception.getMessage());
	}
	@Override  //연결 종료
	public void afterConnectionClosed(WebSocketSession session,
			CloseStatus status) throws Exception {
		super.afterConnectionClosed(session, status);
		System.out.println("클라이언트 접속 해제 : " + status.getReason());
		clients.remove(session);
	}
	@Override  //기능 추가.
	public void afterPropertiesSet() throws Exception {	}
}
