package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import exception.LoginException;
import logic.Mail;
import logic.ShopService;
import logic.User;

@Controller
@RequestMapping("admin")
public class AdminController {
	
	@Autowired
	private ShopService service;
	
	@RequestMapping("list")
	public ModelAndView adminCheckList(HttpSession session) {
		ModelAndView mav = new ModelAndView();
		List<User> userList = service.getUserList();
		mav.addObject("list", userList);
		return mav;
	}
	@RequestMapping("mailForm")
	public ModelAndView mailform (String[] idchks, HttpSession session) {
		ModelAndView mav = new ModelAndView("admin/mail");
		if(idchks == null || idchks.length==0) {
			throw new LoginException
			("메일을 보낼 대상자를 선택하세요","list.shop");
		}
		List<User> list = service.getUserList(idchks);
		//select * from useraccount where userid in ('test1','test2')
		mav.addObject("list",list);
		return mav;
	}	
	@RequestMapping("mail")
	public ModelAndView mail(Mail mail,HttpSession session) {
		ModelAndView mav = new ModelAndView("alert");
		mailSend(mail);
		mav.addObject("message","메일 전송이 완료 되었습니다.");
		mav.addObject("url","list.shop");
		return mav;
	}
	private final class MyAuthenticator extends Authenticator {
		private String id;
		private String pw;
		public MyAuthenticator(String id, String pw) {
			this.id = id;
			this.pw = pw;
		}
		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(id, pw);
		}
	}
	private void mailSend(Mail mail) {
		MyAuthenticator auth = new MyAuthenticator  //인증 객체
				       (mail.getNaverid(),mail.getNaverpw());
		//Properties 클래스 : Map 구현 클래스. 키와value의 자료형 String형
		//prop : mail.properties의 내용을 이용하여 mail전송을 위한 환경값을 저장
		Properties prop = new Properties();
		try {
//mail.properties 파일의 절대 경로로 파일을 읽기 위한 입력스트림			
			FileInputStream fis =new FileInputStream
 ("C:/kms/20200914/spring/workspace/shop3/src/main/resources/mail.properties");
			prop.load(fis);  //mail.properties의 내용을 prop객체로 저장.
			prop.put("mail.smtp.user",mail.getNaverid());
		} catch (IOException e) {
			e.printStackTrace();
		}
		//session : 메일서버와 연결.
		Session session = Session.getInstance(prop,auth);
		//mimeMsg : 메일 전송 객체
		MimeMessage mimeMsg = new MimeMessage(session);
		try {
			mimeMsg.setFrom   //보내는 주소 설정
			(new InternetAddress(mail.getNaverid()+"@naver.com"));
			List<InternetAddress> addrs = new ArrayList<InternetAddress>();
			//받는 사람 정보
			String[] emails = mail.getRecipient().split(",");
			for(String email : emails) {
			try {//홍길동<test@aaa.bbb>,김삿갓<test2@aaa.bbb>
				//수신메일에서 받는사람의 한글 깨지지 않도록 설정
					addrs.add(new InternetAddress
				   (new String(email.getBytes("utf-8"),"8859_1")));
				} catch(UnsupportedEncodingException ue) {
					ue.printStackTrace();
				}
			}
			InternetAddress[] arr =  new InternetAddress[emails.length];
			for(int i=0;i<addrs.size();i++) {
				arr[i] = addrs.get(i);
			}
			//전송일자 설정
			mimeMsg.setSentDate(new Date());
			//수신메일 설정
			mimeMsg.setRecipients(Message.RecipientType.TO,arr);
			//제목 설정
			mimeMsg.setSubject(mail.getTitle());
			//내용부분분리 : 내용, 첨부파일1, 첨부파일2
			MimeMultipart multipart = new MimeMultipart();
			MimeBodyPart message = new MimeBodyPart(); //내용,첨부파일 분리 부분 
			message.setContent(mail.getContents(),mail.getMtype()); //내용부분
			multipart.addBodyPart(message);  //내용 추가.
			//첨부파일 추가
			//mf : 첨부된 파일의 내용.
			for(MultipartFile mf : mail.getFile1()) {
				if((mf != null) && (!mf.isEmpty())) { //첨부파일 존재.
					multipart.addBodyPart(bodyPart(mf)); //첨부파일을 메일에 추가
				}
			}
			mimeMsg.setContent(multipart);
			Transport.send(mimeMsg); //메일 전송.
		} catch(MessagingException me) {
			me.printStackTrace();
		}
	}
	private BodyPart bodyPart(MultipartFile mf) {
		MimeBodyPart body = new MimeBodyPart();
		String orgFile = mf.getOriginalFilename(); //파일의 이름
		String path = "c:/mailupload/"; //메일전송전 업로드된 파일 저장 폴더
		File f = new File(path);
		if(!f.exists()) f.mkdirs(); //path가 없는 경우 폴더를 생성
		File f1 =new File(path + orgFile); //업로드될 파일
		try {
			mf.transferTo(f1); //업로된 파일의 내용을 f1파일에 저장
			body.attachFile(f1); //첨부파일을 메일에 추가
			//첨부파일의 이름 설정
			//받은 메일에서 한글부분 표시되도록 설정
            body.setFileName(new String(orgFile.getBytes("UTF-8"),"8859_1"));
		} catch(Exception e) {
			e.printStackTrace();
		} 
		return body;
	}	
}