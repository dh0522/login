package com.example.login.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
public class MailService {


	@Value("${spring.mail.username}")
	String user;

	@Autowired
	private JavaMailSender javaMailSender;

	public String sendMail(String url){
		String test = "성공";

		try{
			// 이메일 전체 메시지 객체 생성 MimeMessage
			MimeMessage m = javaMailSender.createMimeMessage();

			// 메일 전송할때, 전송자, 메일 제목, 메일 내용 etc 등을 쉽게 설정할 수 있도록 도와주는 클래스
			MimeMessageHelper h = new MimeMessageHelper(m, "UTF-8");

			// 내 계정으로 보내지만, 상대방이 메일을 받을 때 관리자라는 이름으로 메일을 받길 원함
			h.setFrom(user, "관리자");
			h.setTo("gong4857@naver.com");
			h.setSubject("홍도현 님과 김아무개 님의 근무가 변경되었습니다. ");
			h.setText("몇시 몇분 변경되었습니다.");

			javaMailSender.send(m);

		} catch (Exception e) {
			e.printStackTrace();
			test = "실패";
		}

		return test;
	}
}
