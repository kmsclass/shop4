package aop;

import javax.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import exception.LoginException;
import logic.User;

@Component  //aop클래스의 객체화
@Aspect     //aop클래스가 Aspect 클래스로 지정
@Order(1)   //순서지정
public class UserLoginAspect {
	/*
	 * @Around : 핵심메서드 실행 전, 후에서 userLoginCheck메서드 실행.
	 * execution(* controller.User*.loginCheck*(..))
	 *    => controller 패키지의 User로 시작하는 이름을 가진 클래스 의 메서드의 이름이
	 *       loginCheck로 시작하는 메서드
	 * args(..,session)
	 *    => 메서드의 매개변수의 마지막의 매개변수가 session인 메서드      
	 */
	@Around("execution(* controller.User*.loginCheck*(..)) && args(..,session)")
	public Object userLoginCheck(ProceedingJoinPoint joinPoint,
			HttpSession session) throws Throwable {
		User loginUser = (User)session.getAttribute("loginUser");
		if(loginUser == null) {
			throw new LoginException("[userlogin]로그인 후 거래하세요","login.shop");
		}
		return joinPoint.proceed();
	}
	@Around("execution(* *.UserController.idCheck*(..)) && args(id,session,..)")
	public Object useridCheck(ProceedingJoinPoint joinPoint, 
			                  String id, HttpSession session) throws Throwable{
		User loginUser = (User)session.getAttribute("loginUser");
		if(loginUser == null) {
			throw new LoginException("로그인하세요", "login.shop");
		}else if(!loginUser.getUserid().equals("admin") && !id.equals(loginUser.getUserid())) {
			throw new LoginException("본인 정보만 거래 가능합니다.","main.shop");
		}
		return joinPoint.proceed();
	}
}
