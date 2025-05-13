package com.example.login.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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

		public void sendSimpleMailMessage() {

			SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

			try {
				// 메일 받는 수신자 설정
				simpleMailMessage.setTo("gong4857@naver.com");

				// 메일 제목 설정
				simpleMailMessage.setSubject("홍도현님의 근무시간이 변동되었습니다. ");

				// 송신자 메일 주소 변경
				simpleMailMessage.setFrom(user);

				// 메일 내용 설정
				String message =  "안녕하세요, 홍도현님.\n\n" +
					"2025년 5월 10일 근무 일정이 다음과 같이 교환되었습니다.\n\n" +
					"📌 변경 내용:\n" +
					"- 홍도현님: NIGHT → OFF\n" +
					"- 김아무개님: OFF → NIGHT\n\n" +
					"일정 관리 시스템에서 변경 사항을 확인해주세요.\n\n" +
					"감사합니다.\n" +
					"간호사 일정관리 시스템";
				simpleMailMessage.setText(message);

				javaMailSender.send(simpleMailMessage);

				log.info("메일 전송 완료! ");
			} catch (Exception e){
				log.info("메일 발송 실패!");
				throw new RuntimeException(e);
			}

		}












}
