package aop;

import javax.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import exception.LoginException;
import logic.User;

//@Component
//@Aspect
//@Order(1)
public class AdminLoginAspect {
	@Around("execution(* controller.Admin*.*(..)) && args(..,session)")
	public Object adminLoginCheck(ProceedingJoinPoint joinPoint
				,HttpSession session) throws Throwable{
		User login = (User)session.getAttribute("loginUser");
		
		if(login == null) {
			throw new LoginException("로그인 후 거래하세요","../user/login.shop");
		}
		if(!login.getUserid().equals("admin")) {
			throw new LoginException("관리자만 가능합니다.","../user/main.shop");
		}
		return joinPoint.proceed();
	}
}
